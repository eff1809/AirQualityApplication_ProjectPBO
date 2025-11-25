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
        // 1. Cari Koordinat (Geo API) - KODE LAMA (TETAP)
        String geoUrl = "http://api.openweathermap.org/geo/1.0/direct?q=" + cityName + "&limit=1&appid=" + apiKey;
        
        try {
            JsonNode geoResponse = restTemplate.getForObject(geoUrl, JsonNode.class);
            if (geoResponse == null || geoResponse.isEmpty()) {
                throw new RuntimeException("Kota tidak ditemukan: " + cityName);
            }
            
            double lat = geoResponse.get(0).get("lat").asDouble();
            double lon = geoResponse.get(0).get("lon").asDouble();

            // 2. Ambil Data Polusi (Air Pollution API) - KODE LAMA (TETAP)
            String pollutionUrl = apiUrl + "?lat=" + lat + "&lon=" + lon + "&appid=" + apiKey;
            JsonNode pollutionResponse = restTemplate.getForObject(pollutionUrl, JsonNode.class);
            
            JsonNode listData = pollutionResponse.get("list").get(0);
            JsonNode components = listData.get("components");
            JsonNode main = listData.get("main");

            AirQuality airQuality = new AirQuality();
            airQuality.setCityName(cityName);
            airQuality.setAqiIndex(main.get("aqi").asInt());
            airQuality.setPm25(components.get("pm2_5").asDouble());
            airQuality.setCo2(components.get("co").asDouble());
            airQuality.setTimestamp(LocalDateTime.now());

            // ---------------------------------------------------------
            // 3. FITUR BARU: Ambil Data Cuaca & Suhu (Weather API)
            // Tambahkan parameter '&units=metric' agar otomatis jadi Celcius
            // ---------------------------------------------------------
            String weatherUrl = "https://api.openweathermap.org/data/2.5/weather?lat=" + lat + "&lon=" + lon + "&units=metric&appid=" + apiKey;
            JsonNode weatherResponse = restTemplate.getForObject(weatherUrl, JsonNode.class);

            if (weatherResponse != null) {
                // Ambil suhu
                double temp = weatherResponse.get("main").get("temp").asDouble();
                airQuality.setTemperature(temp);

                // Ambil icon cuaca (misal: "04d" = berawan)
                String iconCode = weatherResponse.get("weather").get(0).get("icon").asText();
                airQuality.setWeatherIcon(iconCode);
            }
            // ---------------------------------------------------------

            // 4. Logic Status AQI - KODE LAMA (TETAP)
            int aqi = airQuality.getAqiIndex();
            if (aqi == 1) airQuality.setStatus("Baik");
            else if (aqi == 2) airQuality.setStatus("Cukup");
            else if (aqi == 3) airQuality.setStatus("Sedang");
            else if (aqi == 4) airQuality.setStatus("Tidak Sehat");
            else airQuality.setStatus("Berbahaya");

            return repository.save(airQuality);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<AirQuality> getHistory(String cityName) {
        return repository.findTop10ByCityNameOrderByTimestampDesc(cityName);
    }
}