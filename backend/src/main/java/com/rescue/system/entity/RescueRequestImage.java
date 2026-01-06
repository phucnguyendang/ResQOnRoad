package com.rescue.system.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "rescue_request_images")
public class RescueRequestImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "rescue_request_id", nullable = false)
    private RescueRequest rescueRequest;

    @Column(name = "image_base64", nullable = false, columnDefinition = "TEXT")
    private String imageBase64;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    public RescueRequestImage() {
    }

    public RescueRequestImage(RescueRequest rescueRequest, String imageBase64) {
        this.rescueRequest = rescueRequest;
        this.imageBase64 = imageBase64;
        this.createdAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RescueRequest getRescueRequest() {
        return rescueRequest;
    }

    public void setRescueRequest(RescueRequest rescueRequest) {
        this.rescueRequest = rescueRequest;
    }

    public String getImageBase64() {
        return imageBase64;
    }

    public void setImageBase64(String imageBase64) {
        this.imageBase64 = imageBase64;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
