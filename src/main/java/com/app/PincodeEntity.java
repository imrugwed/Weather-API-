package com.app;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "pincodes")
@Data
public class PincodeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String pincode;

    private Double latitude;
    private Double longitude;
}
