package com.rescue.system.repository;

import com.rescue.system.entity.RescueRequestImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RescueRequestImageRepository extends JpaRepository<RescueRequestImage, Long> {

    List<RescueRequestImage> findByRescueRequestIdOrderByIdAsc(Long rescueRequestId);
}
