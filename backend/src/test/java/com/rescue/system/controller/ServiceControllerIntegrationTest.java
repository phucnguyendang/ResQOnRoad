package com.rescue.system.controller;

import com.rescue.system.dto.request.CreateServiceRequest;
import com.rescue.system.dto.request.UpdateServiceRequest;
import com.rescue.system.entity.ServiceType;
import com.rescue.system.repository.ServiceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * UC302 - Service Management Integration Tests
 * 
 * Tests all 7 endpoints:
 * 1. GET /api/services/company/{companyId} - Get all services
 * 2. GET /api/services/{serviceId} - Get service detail
 * 3. GET /api/services/company/{companyId}/available - Get available services
 * 4. POST /api/services - Create new service
 * 5. PUT /api/services/{serviceId} - Update service
 * 6. DELETE /api/services/{serviceId} - Delete service
 * 7. GET /api/services/company/my - Get my company services (authenticated)
 */
@SpringBootTest
@AutoConfigureMockMvc
public class ServiceControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ServiceRepository serviceRepository;

    private String validToken;
    private Long companyId = 1L;
    private Long serviceId = 1L;

    @BeforeEach
    public void setUp() {
        // Setup test data - token should come from login endpoint
        // For testing, we can mock the authentication
        validToken = "Bearer test_token_123";
    }

    /**
     * TC1: Get all services for a company (Public endpoint)
     */
    @Test
    public void testGetServicesByCompanyId() throws Exception {
        mockMvc.perform(get("/api/services/company/" + companyId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Lấy danh sách dịch vụ thành công"))
                .andExpect(jsonPath("$.data").isArray());
    }

    /**
     * TC2: Get service detail (Public endpoint)
     */
    @Test
    public void testGetServiceById() throws Exception {
        mockMvc.perform(get("/api/services/" + serviceId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Lấy chi tiết dịch vụ thành công"))
                .andExpect(jsonPath("$.data.id").value(serviceId));
    }

    /**
     * TC3: Get available services (Public endpoint)
     */
    @Test
    public void testGetAvailableServices() throws Exception {
        mockMvc.perform(get("/api/services/company/" + companyId + "/available")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Lấy danh sách dịch vụ khả dụng thành công"))
                .andExpect(jsonPath("$.data").isArray());
    }

    /**
     * TC4: Create new service (Authenticated endpoint)
     */
    @Test
    public void testCreateService() throws Exception {
        CreateServiceRequest request = new CreateServiceRequest();
        request.setName("Vá lốp Test");
        request.setDescription("Test service");
        request.setType("TIRE_CHANGE");
        request.setBasePrice(150000.0);
        request.setPriceUnit("VND");
        request.setIsAvailable(true);
        request.setEstimatedTime(30);

        mockMvc.perform(post("/api/services")
                .header("Authorization", validToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(201))
                .andExpect(jsonPath("$.message").value("Tạo dịch vụ thành công"))
                .andExpect(jsonPath("$.data.name").value("Vá lốp Test"))
                .andExpect(jsonPath("$.data.basePrice").value(150000.0));
    }

    /**
     * TC5: Create service with invalid data (Bad request)
     */
    @Test
    public void testCreateServiceWithInvalidData() throws Exception {
        CreateServiceRequest request = new CreateServiceRequest();
        request.setName(""); // Empty name - should fail
        request.setType("INVALID_TYPE");
        request.setBasePrice(-100.0); // Negative price - should fail

        mockMvc.perform(post("/api/services")
                .header("Authorization", validToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));
    }

    /**
     * TC6: Update service (Authenticated endpoint)
     */
    @Test
    public void testUpdateService() throws Exception {
        UpdateServiceRequest request = new UpdateServiceRequest();
        request.setBasePrice(200000.0);
        request.setEstimatedTime(25);

        mockMvc.perform(put("/api/services/" + serviceId)
                .header("Authorization", validToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Cập nhật dịch vụ thành công"))
                .andExpect(jsonPath("$.data.basePrice").value(200000.0));
    }

    /**
     * TC7: Update non-existent service (Not found)
     */
    @Test
    public void testUpdateNonExistentService() throws Exception {
        UpdateServiceRequest request = new UpdateServiceRequest();
        request.setBasePrice(200000.0);

        mockMvc.perform(put("/api/services/99999")
                .header("Authorization", validToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404));
    }

    /**
     * TC8: Delete service (Authenticated endpoint)
     */
    @Test
    public void testDeleteService() throws Exception {
        mockMvc.perform(delete("/api/services/" + serviceId)
                .header("Authorization", validToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Xóa dịch vụ thành công"));
    }

    /**
     * TC9: Get my company services (Authenticated endpoint)
     */
    @Test
    public void testGetMyCompanyServices() throws Exception {
        mockMvc.perform(get("/api/services/company/my")
                .header("Authorization", validToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Lấy danh sách dịch vụ thành công"))
                .andExpect(jsonPath("$.data").isArray());
    }

    /**
     * TC10: Access protected endpoint without token (Unauthorized)
     */
    @Test
    public void testAccessProtectedEndpointWithoutToken() throws Exception {
        CreateServiceRequest request = new CreateServiceRequest();
        request.setName("Test");
        request.setType("TIRE_CHANGE");
        request.setBasePrice(150000.0);

        mockMvc.perform(post("/api/services")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    /**
     * TC11: Test all service types
     */
    @Test
    public void testAllServiceTypes() throws Exception {
        String[] serviceTypes = {
            "TOW_TRUCK",
            "TIRE_CHANGE",
            "BATTERY_JUMP",
            "FUEL_DELIVERY",
            "LOCKOUT",
            "WINCH_OUT",
            "ACCIDENT_RECOVERY",
            "MECHANICAL_REPAIR"
        };

        for (String type : serviceTypes) {
            CreateServiceRequest request = new CreateServiceRequest();
            request.setName("Service " + type);
            request.setType(type);
            request.setBasePrice(100000.0);

            mockMvc.perform(post("/api/services")
                    .header("Authorization", validToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.data.type").value(type));
        }
    }

    /**
     * TC12: Partial update - only update price
     */
    @Test
    public void testPartialUpdate() throws Exception {
        UpdateServiceRequest request = new UpdateServiceRequest();
        request.setBasePrice(300000.0);
        // Other fields are null - should only update price

        mockMvc.perform(put("/api/services/" + serviceId)
                .header("Authorization", validToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.basePrice").value(300000.0));
    }
}
