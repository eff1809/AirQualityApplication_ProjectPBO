package com.AirQuality.airquality.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity // Menandakan ini adalah tabel database
@Data // Lombok: Otomatis bikin Getter, Setter, toString (Hemat waktu!)
@NoArgsConstructor // Lombok: Constructor kosong
@AllArgsConstructor // Lombok: Constructor isi semua
@Table(name = "air_quality_history") // Nama tabel di database H2 nanti
public class AirQuality {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String cityName;
    private Integer aqiIndex; // Indeks kualitas udara (ex: 120)
    private Double pm25; // Partikel debu halus
    private Double co2; // Karbon monoksida/dioksida

    private String status; // ex: "Sehat", "Tidak Sehat", "Berbahaya"

    private LocalDateTime timestamp; // Waktu data diambil

    private Double temperature; // Menampung suhu (Celcius)
    private String weatherIcon; // Kode icon cuaca (misal: '10d' untuk hujan)

    // Method baru untuk logika bisnis status berdasarkan aqiIndex
    public void calculateStatus() {
        if (this.aqiIndex == null) {
            this.status = "Tidak Diketahui";
        } else if (this.aqiIndex <= 2) {
            this.status = "Baik";
        } else if (this.aqiIndex == 3) {
            this.status = "Sedang";
        } else if (this.aqiIndex == 4) {
            this.status = "Tidak Sehat";
        } else {
            this.status = "Berbahaya";
        }
    }
}