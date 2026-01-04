package com.rescue.system.service;

import com.rescue.system.dto.request.CreateServiceRequest;
import com.rescue.system.dto.request.UpdateServiceRequest;
import com.rescue.system.dto.response.ServiceDetailResponse;

import java.util.List;

public interface ServiceService {
    
    /**
     * Get all services for a company
     */
    List<ServiceDetailResponse> getServicesByCompanyId(Long companyId);
    
    /**
     * Get service by ID
     */
    ServiceDetailResponse getServiceById(Long serviceId);
    
    /**
     * Create a new service for a company (accountId to determine which company)
     */
    ServiceDetailResponse createService(Long accountId, CreateServiceRequest request);
    
    /**
     * Update an existing service
     */
    ServiceDetailResponse updateService(Long serviceId, Long accountId, UpdateServiceRequest request);
    
    /**
     * Delete a service
     */
    void deleteService(Long serviceId, Long accountId);
    
    /**
     * Get available services for a company
     */
    List<ServiceDetailResponse> getAvailableServicesByCompanyId(Long companyId);
}
