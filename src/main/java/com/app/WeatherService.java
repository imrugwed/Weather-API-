package com.app;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class WeatherService {
    private final PincodeRepository pincodeRepository;
    private final WeatherRepository weatherRepository;
    private final RestTemplate restTemplate;


    private final String apiKey="83582419eff72ca418b0922adba79fdc";

    public WeatherService(PincodeRepository pincodeRepository, WeatherRepository weatherRepository) {
        this.pincodeRepository = pincodeRepository;
        this.weatherRepository = weatherRepository;
        this.restTemplate = new RestTemplate();
    }

    public WeatherEntity getWeather(String pincode, LocalDate date) {
        // Check if weather info already exists in the DB
        return weatherRepository.findByPincodeAndDate(pincode, date)
                .orElseGet(() -> fetchAndSaveWeather(pincode, date));
    }

    private WeatherEntity fetchAndSaveWeather(String pincode, LocalDate date) {
        // Fetch lat/long if not available in DB
        PincodeEntity pincodeEntity = pincodeRepository.findByPincode(pincode)
                .orElseGet(() -> fetchAndSavePincodeDetails(pincode));

        // Fetch weather data using OpenWeather API
        String weatherUrl = String.format(
                "https://api.openweathermap.org/data/2.5/weather?lat=%s&lon=%s&appid=%s",
                pincodeEntity.getLatitude(), pincodeEntity.getLongitude(), apiKey);

        ResponseEntity<Map> response = restTemplate.getForEntity(weatherUrl, Map.class);
        Map<String, Object> weatherData = response.getBody();

        String weatherDescription = (String) ((Map) ((List) weatherData.get("weather")).get(0)).get("description");
        Double temperature = (Double) ((Map) weatherData.get("main")).get("temp");

        WeatherEntity weatherEntity = new WeatherEntity();
        weatherEntity.setPincode(pincode);
        weatherEntity.setDate(date);
        weatherEntity.setWeatherDescription(weatherDescription);
        weatherEntity.setTemperature(temperature);
        weatherEntity.setPincodeEntity(pincodeEntity);

        return weatherRepository.save(weatherEntity);
    }

    private PincodeEntity fetchAndSavePincodeDetails(String pincode) {
        String geocodeUrl = String.format("http://api.openweathermap.org/geo/1.0/zip?zip=%s&appid=%s", pincode, apiKey);
        ResponseEntity<Map> response = restTemplate.getForEntity(geocodeUrl, Map.class);
        Map<String, Object> locationData = response.getBody();

        Double lat = (Double) locationData.get("lat");
        Double lon = (Double) locationData.get("lon");

        PincodeEntity entity = new PincodeEntity();
        entity.setPincode(pincode);
        entity.setLatitude(lat);
        entity.setLongitude(lon);

        return pincodeRepository.save(entity);
    }
}
