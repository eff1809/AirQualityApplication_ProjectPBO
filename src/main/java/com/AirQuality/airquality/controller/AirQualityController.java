package com.AirQuality.airquality.controller;

import com.AirQuality.airquality.model.AirQuality;
import com.AirQuality.airquality.service.AirQualityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

    // --- import baru untuk fitur export data to csv ---
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import java.util.List;

@Controller
public class AirQualityController {

    @Autowired
    private AirQualityService service;

    // Halaman Utama (Dashboard)
    // Method ini akan dipanggil saat user membuka browser (localhost:8080)
    @GetMapping("/")
    public String home(Model model, @RequestParam(value = "city", required = false, defaultValue = "Makassar") String city) {
        // "Makassar" kita set sebagai default karena ini project Unhas :)
        
        // 1. Panggil Service untuk ambil Data Real-time
        AirQuality currentData = service.getAirQualityData(city);
        
        // 2. Panggil Service untuk ambil History pencarian
        List<AirQuality> historyData = service.getHistory(city);

        // 3. Masukkan data ke dalam "amplop" (Model) untuk dikirim ke HTML
        model.addAttribute("current", currentData);
        model.addAttribute("history", historyData);
        model.addAttribute("city", city);
        
        // 4. Logic Notifikasi Sederhana (Fitur 3)
        // Jika AQI >= 4 (Buruk/Berbahaya), kirim sinyal bahaya ke frontend
        boolean isDanger = currentData != null && currentData.getAqiIndex() >= 4;
        model.addAttribute("isDanger", isDanger);

        // 5. Kembalikan nama file HTML yang akan ditampilkan
        return "dashboard"; 
    }


    // Fitur: Export Data ke CSV (Versi Super-CSV)
    @GetMapping("/export")
    public void exportToCSV(@RequestParam(value = "city", defaultValue = "Makassar") String city, HttpServletResponse response) throws IOException {
        // 1. Set Tipe File agar Browser mendownloadnya
        response.setContentType("text/csv");
        
        // Buat nama file unik dengan tanggal jam sekarang (biar tidak duplikat)
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=Laporan_" + city + "_" + timestamp + ".csv";
        response.setHeader(headerKey, headerValue);

        // 2. Ambil data dari Service
        List<AirQuality> historyData = service.getHistory(city);

        // 3. Gunakan Super-CSV untuk menulis data
        // CsvPreference.EXCEL_NORTH_PREFERENCE artinya ikut standar Excel EROPA/INDONESIA )
        ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.EXCEL_NORTH_EUROPE_PREFERENCE);

        // Header: Judul Kolom yang muncul di Excel (Baris paling atas)
        String[] csvHeader = {"ID Data", "Nama Kota", "Indeks AQI", "PM 2.5", "CO (Karbon)", "Status", "Waktu Record"};
        
        // Mapping: Nama variabel di file 'AirQuality.java' (Harus SAMA PERSIS huruf besar/kecilnya!)
        String[] nameMapping = {"id", "cityName", "aqiIndex", "pm25", "co2", "status", "timestamp"};

        // Tulis Header
        csvWriter.writeHeader(csvHeader);

        // Tulis Isi Data (Looping otomatis oleh library)
        for (AirQuality data : historyData) {
            csvWriter.write(data, nameMapping);
        }

        csvWriter.close();
    }
}