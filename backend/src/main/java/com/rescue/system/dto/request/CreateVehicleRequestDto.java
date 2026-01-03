package com.rescue.system.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateVehicleRequestDto {

    @NotBlank
    @Size(max = 20, message = "Plate number must not exceed 20 characters")
    private String plateNumber;

    @NotBlank
    @Size(max = 50, message = "Vehicle type must not exceed 50 characters")
    private String vehicleType;

    @Size(max = 500, message = "Equipment description must not exceed 500 characters")
    private String equipmentDesc;

    public CreateVehicleRequestDto() {
    }

    public CreateVehicleRequestDto(String plateNumber, String vehicleType, String equipmentDesc) {
        this.plateNumber = plateNumber;
        this.vehicleType = vehicleType;
        this.equipmentDesc = equipmentDesc;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getEquipmentDesc() {
        return equipmentDesc;
    }

    public void setEquipmentDesc(String equipmentDesc) {
        this.equipmentDesc = equipmentDesc;
    }
}
