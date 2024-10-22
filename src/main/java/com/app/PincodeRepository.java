package com.app;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PincodeRepository extends JpaRepository<PincodeEntity, Long> {
    Optional<PincodeEntity> findByPincode(String pincode);
}

