package com.rescue.system.service.impl;

import com.rescue.system.dto.request.CreateServiceRequest;
import com.rescue.system.dto.request.UpdateServiceRequest;
import com.rescue.system.dto.response.ServiceDetailResponse;
import com.rescue.system.entity.Account;
import com.rescue.system.entity.RescueCompany;
import com.rescue.system.entity.Service;
import com.rescue.system.entity.ServiceType;
import com.rescue.system.repository.AccountRepository;
import com.rescue.system.repository.RescueCompanyRepository;
import com.rescue.system.repository.ServiceRepository;
import com.rescue.system.service.ServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ServiceServiceImpl implements ServiceService {

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private RescueCompanyRepository companyRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public List<ServiceDetailResponse> getServicesByCompanyId(Long companyId) {
        RescueCompany company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Công ty cứu hộ không tồn tại"));

        return serviceRepository.findByCompanyId(companyId).stream()
                .map(this::mapToServiceDetailResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ServiceDetailResponse getServiceById(Long serviceId) {
        Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("Dịch vụ không tồn tại"));

        return mapToServiceDetailResponse(service);
    }

    @Override
    @Transactional
    public ServiceDetailResponse createService(Long accountId, CreateServiceRequest request) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Tài khoản không tồn tại"));

        Long companyId = account.getCompanyId();
        if (companyId == null) {
            throw new RuntimeException("Tài khoản này không thuộc công ty cứu hộ nào");
        }

        RescueCompany company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Công ty cứu hộ không tồn tại"));

        Service service = new Service();
        service.setName(request.getName());
        service.setDescription(request.getDescription());
        service.setType(ServiceType.valueOf(request.getType()));
        service.setBasePrice(request.getBasePrice());
        service.setPriceUnit(request.getPriceUnit() != null ? request.getPriceUnit() : "VND");
        service.setIsAvailable(request.getIsAvailable() != null ? request.getIsAvailable() : true);
        service.setEstimatedTime(request.getEstimatedTime());
        service.setCompany(company);

        Service savedService = serviceRepository.save(service);
        return mapToServiceDetailResponse(savedService);
    }

    @Override
    @Transactional
    public ServiceDetailResponse updateService(Long serviceId, Long accountId, UpdateServiceRequest request) {
        Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("Dịch vụ không tồn tại"));

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Tài khoản không tồn tại"));

        Long companyId = account.getCompanyId();
        if (companyId == null || !service.getCompany().getId().equals(companyId)) {
            throw new RuntimeException("Bạn không có quyền cập nhật dịch vụ này");
        }

        if (request.getName() != null) {
            service.setName(request.getName());
        }
        if (request.getDescription() != null) {
            service.setDescription(request.getDescription());
        }
        if (request.getType() != null) {
            service.setType(ServiceType.valueOf(request.getType()));
        }
        if (request.getBasePrice() != null) {
            service.setBasePrice(request.getBasePrice());
        }
        if (request.getPriceUnit() != null) {
            service.setPriceUnit(request.getPriceUnit());
        }
        if (request.getIsAvailable() != null) {
            service.setIsAvailable(request.getIsAvailable());
        }
        if (request.getEstimatedTime() != null) {
            service.setEstimatedTime(request.getEstimatedTime());
        }

        Service updatedService = serviceRepository.save(service);
        return mapToServiceDetailResponse(updatedService);
    }

    @Override
    @Transactional
    public void deleteService(Long serviceId, Long accountId) {
        Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("Dịch vụ không tồn tại"));

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Tài khoản không tồn tại"));

        Long companyId = account.getCompanyId();
        if (companyId == null || !service.getCompany().getId().equals(companyId)) {
            throw new RuntimeException("Bạn không có quyền xóa dịch vụ này");
        }

        serviceRepository.delete(service);
    }

    @Override
    public List<ServiceDetailResponse> getAvailableServicesByCompanyId(Long companyId) {
        RescueCompany company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Công ty cứu hộ không tồn tại"));

        return serviceRepository.findByCompanyIdAndIsAvailableTrue(companyId).stream()
                .map(this::mapToServiceDetailResponse)
                .collect(Collectors.toList());
    }

    private ServiceDetailResponse mapToServiceDetailResponse(Service service) {
        return new ServiceDetailResponse(
                service.getId(),
                service.getName(),
                service.getDescription(),
                service.getType().toString(),
                service.getType().getDisplayName(),
                service.getBasePrice(),
                service.getPriceUnit(),
                service.getIsAvailable(),
                service.getEstimatedTime(),
                service.getCreatedAt()
        );
    }
}
