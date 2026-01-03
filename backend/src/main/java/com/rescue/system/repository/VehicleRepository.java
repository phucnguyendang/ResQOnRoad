package com.rescue.system.repository;

import com.rescue.system.entity.RescueCompany;
import com.rescue.system.entity.Vehicle;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    List<Vehicle> findByCompanyId(Long companyId);

    boolean existsByCompanyAndPlateNumber(RescueCompany company, String plateNumber);
}
