package com.rescue.system.service;

import com.rescue.system.dto.request.CreateVehicleRequestDto;
import com.rescue.system.dto.response.VehicleResponse;
import com.rescue.system.entity.RescueCompany;
import java.util.List;

public interface VehicleService {

    List<VehicleResponse> getCompanyVehicles(Long companyId);

    VehicleResponse createVehicle(RescueCompany company, CreateVehicleRequestDto request);
}