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
    private Double pm25;      // Partikel debu halus
    private Double co2;       // Karbon monoksida/dioksida
    
    private String status;    // ex: "Sehat", "Tidak Sehat", "Berbahaya"
    
    private LocalDateTime timestamp; // Waktu data diambil

    private Double temperature;  // Menampung suhu (Celcius)
    private String weatherIcon;  // Kode icon cuaca (misal: '10d' untuk hujan)
}