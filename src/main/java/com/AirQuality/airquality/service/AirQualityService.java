package com.AirQuality.airquality.service;

import com.AirQuality.airquality.model.AirQuality;
import java.util.List;

public interface AirQualityService {
    // Kontrak: Method untuk ambil data dari API
    AirQuality getAirQualityData(String cityName);
    
    // Kontrak: Method untuk ambil history dari database
    List<AirQuality> getHistory(String cityName);
}