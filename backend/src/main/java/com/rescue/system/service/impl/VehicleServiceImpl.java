package com.rescue.system.service.impl;
import com.rescue.system.dto.request.CreateVehicleRequestDto;
import com.rescue.system.dto.response.VehicleResponse;
import com.rescue.system.entity.RescueCompany;
import com.rescue.system.entity.Vehicle;
import com.rescue.system.exception.ApiException;
import com.rescue.system.repository.VehicleRepository;
import com.rescue.system.service.VehicleService;
import java.util.List;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;

@Service
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;

    public VehicleServiceImpl(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    @Override
    public List<VehicleResponse> getCompanyVehicles(Long companyId) {
        return vehicleRepository.findByCompanyId(companyId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    @Override
    public VehicleResponse createVehicle(RescueCompany company, CreateVehicleRequestDto request) {

        if (vehicleRepository.existsByCompanyAndPlateNumber(company, request.getPlateNumber())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Vehicle already exists");
        }

        Vehicle vehicle = new Vehicle();
        vehicle.setCompany(company);
        vehicle.setPlateNumber(request.getPlateNumber());
        vehicle.setVehicleType(request.getVehicleType());
        vehicle.setEquipmentDesc(request.getEquipmentDesc());

        vehicleRepository.save(vehicle);
        return toResponse(vehicle);
    }

    private VehicleResponse toResponse(Vehicle v) {
        VehicleResponse r = new VehicleResponse();
        r.setId(v.getId());
        r.setPlateNumber(v.getPlateNumber());
        r.setVehicleType(v.getVehicleType());
        r.setEquipmentDesc(v.getEquipmentDesc());
        r.setStatus(v.getStatus());
        return r;
    }
}