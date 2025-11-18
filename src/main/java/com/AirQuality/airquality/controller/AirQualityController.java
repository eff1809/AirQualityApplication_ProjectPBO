package com.AirQuality.airquality.controller;

import com.AirQuality.airquality.model.AirQuality;
import com.AirQuality.airquality.service.AirQualityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
}