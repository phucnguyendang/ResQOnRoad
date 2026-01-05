package com.rescue.system.entity;
import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(
    name = "vehicles",
    indexes = {
        @Index(name = "idx_vehicles_company_id", columnList = "company_id"),
        @Index(name = "uk_vehicles_plate_number", columnList = "plate_number")
    }
)
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private RescueCompany company;

    @Column(name = "plate_number", nullable = false, length = 20)
    private String plateNumber;

    @Column(name = "vehicle_type", nullable = false, length = 50)
    private String vehicleType;

    @Column(name = "equipment_desc")
    private String equipmentDesc;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private VehicleStatus status = VehicleStatus.Available;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = LocalDateTime.now();
    }


    public Vehicle(){
    }

    public Vehicle(Long id, RescueCompany company, String plateNumber, String vehicleType, String equipmentDesc, VehicleStatus status) {
        this.id = id;
        this.company = company;
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

    public RescueCompany getCompany() {
        return company;
    }

    public void setCompany(RescueCompany company) {
        this.company = company;
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
}