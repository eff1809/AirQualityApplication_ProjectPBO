package com.AirQuality.airquality.service;

import com.AirQuality.airquality.model.AirQuality;
import com.AirQuality.airquality.repository.AirQualityRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AirQualityServiceImpl implements AirQualityService {

    @Autowired
    private AirQualityRepository repository;

    @Value("${weather.api.key}")
    private String apiKey;

    @Value("${weather.api.url}")
    private String apiUrl;

    // RestTemplate adalah alat Spring buat request ke internet
    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public AirQuality getAirQualityData(String cityName) {
        // 1. Cari koordinat kota dulu (Geocoding sederhana)
        // Karena API Air Pollution butuh Lat/Lon, bukan nama kota.
        // Kita pakai endpoint Geo OpenWeatherMap.
        String geoUrl = "http://api.openweathermap.org/geo/1.0/direct?q=" + cityName + "&limit=1&appid=" + apiKey;
        
        try {
            // Panggil API Geo
            JsonNode geoResponse = restTemplate.getForObject(geoUrl, JsonNode.class);
            if (geoResponse == null || geoResponse.isEmpty()) {
                throw new RuntimeException("Kota tidak ditemukan: " + cityName);
            }
            
            double lat = geoResponse.get(0).get("lat").asDouble();
            double lon = geoResponse.get(0).get("lon").asDouble();

            // 2. Panggil API Kualitas Udara pakai Lat/Lon tadi
            String url = apiUrl + "?lat=" + lat + "&lon=" + lon + "&appid=" + apiKey;
            JsonNode response = restTemplate.getForObject(url, JsonNode.class);

            // 3. Ambil data penting dari JSON response
            JsonNode listData = response.get("list").get(0);
            JsonNode components = listData.get("components");
            JsonNode main = listData.get("main");

            AirQuality airQuality = new AirQuality();
            airQuality.setCityName(cityName);
            airQuality.setAqiIndex(main.get("aqi").asInt()); // 1 = Bagus, 5 = Sangat Buruk
            airQuality.setPm25(components.get("pm2_5").asDouble());
            airQuality.setCo2(components.get("co").asDouble()); // CO (Karbon Monoksida)
            airQuality.setTimestamp(LocalDateTime.now());

            // 4. Tentukan Status (Logic sederhana)
            // Skala AQI OpenWeather: 1 (Good), 2 (Fair), 3 (Moderate), 4 (Poor), 5 (Very Poor)
            int aqi = airQuality.getAqiIndex();
            if (aqi == 1) airQuality.setStatus("Baik");
            else if (aqi == 2) airQuality.setStatus("Cukup");
            else if (aqi == 3) airQuality.setStatus("Sedang");
            else if (aqi == 4) airQuality.setStatus("Tidak Sehat");
            else airQuality.setStatus("Berbahaya");

            // 5. Simpan ke Database (Otomatis untuk History)
            return repository.save(airQuality);

        } catch (Exception e) {
            e.printStackTrace();
            // Jika error, kembalikan object kosong atau null biar gak crash
            return null;
        }
    }

    @Override
    public List<AirQuality> getHistory(String cityName) {
        return repository.findTop10ByCityNameOrderByTimestampDesc(cityName);
    }
}