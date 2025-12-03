package com.AirQuality.airquality;

import com.AirQuality.airquality.model.AirQuality;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

// Menerapkan materi Slide 12: Membuat Test Class
class AirQualityTest {

    // Test Case 1: Batas Bawah (Baik)
    @Test
    void testStatus_WhenAqiIs1_ShouldBeBaik() {
        AirQuality aq = new AirQuality();
        aq.setAqiIndex(1);
        aq.calculateStatus(); // Jalankan logika
        
        // Sesuai materi Slide 13 & 19: assertEquals(message, expected, actual)
        assertEquals("Baik", aq.getStatus(), "AQI 1 harusnya status Baik");
    }

    // Test Case 2: Batas Tengah (Sedang)
    @Test
    void testStatus_WhenAqiIs3_ShouldBeSedang() {
        AirQuality aq = new AirQuality();
        aq.setAqiIndex(3);
        aq.calculateStatus();
        assertEquals("Sedang", aq.getStatus(), "AQI 3 harusnya status Sedang");
    }

    // Test Case 3: Batas Bahaya (Tidak Sehat)
    @Test
    void testStatus_WhenAqiIs4_ShouldBeTidakSehat() {
        AirQuality aq = new AirQuality();
        aq.setAqiIndex(4);
        aq.calculateStatus();
        assertEquals("Tidak Sehat", aq.getStatus(), "AQI 4 harusnya status Tidak Sehat");
    }

    // Test Case 4: Ekstrem (Sangat Berbahaya)
    @Test
    void testStatus_WhenAqiIsHigh_ShouldBeBerbahaya() {
        AirQuality aq = new AirQuality();
        aq.setAqiIndex(5); // Atau angka lebih tinggi
        aq.calculateStatus();
        assertEquals("Berbahaya", aq.getStatus(), "AQI 5+ harusnya status Berbahaya");
    }

    // Test Case 5: Negative Testing (Slide 16 - Testing for anomalies)
    // Apa yang terjadi jika AQI null? (Misal data API rusak)
    @Test
    void testStatus_WhenAqiIsNull_ShouldHandleGracefully() {
        AirQuality aq = new AirQuality();
        aq.setAqiIndex(null);
        aq.calculateStatus();
        assertEquals("Tidak Diketahui", aq.getStatus(), "Jika null, status harus aman (tidak crash)");
    }
}