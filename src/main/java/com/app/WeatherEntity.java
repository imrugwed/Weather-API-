package com.app;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "weather")
@Data
public class WeatherEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String pincode;
    private LocalDate date;

    private String weatherDescription;
    private Double temperature;

    @ManyToOne
    @JoinColumn(name = "pincode_id")
    private PincodeEntity pincodeEntity;
}
