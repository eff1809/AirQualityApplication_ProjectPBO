package com.AirQuality.airquality.repository;

import com.AirQuality.airquality.model.AirQuality;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AirQualityRepository extends JpaRepository<AirQuality, Long> {
    
    // Fitur History: Mencari 10 data terakhir berdasarkan nama kota
    // Spring Boot otomatis paham maksud nama method ini tanpa kita tulis query SQL!
    List<AirQuality> findTop10ByCityNameOrderByTimestampDesc(String cityName);
}

