package com.rescue.system.dto.response;

import com.rescue.system.entity.VehicleStatus;

public class VehicleResponse {

    private Long id;
    private String plateNumber;
    private String vehicleType;
    private String equipmentDesc;
    private VehicleStatus status;

    public VehicleResponse() {
    }

    public VehicleResponse(Long id, String plateNumber, String vehicleType, String equipmentDesc, VehicleStatus status) {
        this.id = id;
        this.plateNumber = plateNumber;
        this.vehicleType = vehicleType;
        this.equipmentDesc = equipmentDesc;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public VehicleStatus getStatus() {
        return status;
    }

    public void setStatus(VehicleStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "VehicleResponse{" +
                "id=" + id +
                ", plateNumber='" + plateNumber + '\'' +
                ", vehicleType='" + vehicleType + '\'' +
                ", equipmentDesc='" + equipmentDesc + '\'' +
                ", status=" + status +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VehicleResponse that = (VehicleResponse) o;

        if (!id.equals(that.id)) return false;
        if (!plateNumber.equals(that.plateNumber)) return false;
        if (!vehicleType.equals(that.vehicleType)) return false;
        if (equipmentDesc != null ? !equipmentDesc.equals(that.equipmentDesc) : that.equipmentDesc != null)
            return false;
        return status == that.status;
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + plateNumber.hashCode();
        result = 31 * result + vehicleType.hashCode();
        result = 31 * result + (equipmentDesc != null ? equipmentDesc.hashCode() : 0);
        result = 31 * result + status.hashCode();
        return result;
    }

    public static class VehicleResponseBuilder{
        private Long id;
        private String plateNumber;
        private String vehicleType;
        private String equipmentDesc;
        private VehicleStatus status;

        VehicleResponseBuilder() {
        }

        public VehicleResponseBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public VehicleResponseBuilder plateNumber(String plateNumber) {
            this.plateNumber = plateNumber;
            return this;
        }

        public VehicleResponseBuilder vehicleType(String vehicleType) {
            this.vehicleType = vehicleType;
            return this;
        }

        public VehicleResponseBuilder equipmentDesc(String equipmentDesc) {
            this.equipmentDesc = equipmentDesc;
            return this;
        }

        public VehicleResponseBuilder status(VehicleStatus status) {
            this.status = status;
            return this;
        }

        public VehicleResponse build() {
            return new VehicleResponse(id, plateNumber, vehicleType, equipmentDesc, status);
        }
    }
}