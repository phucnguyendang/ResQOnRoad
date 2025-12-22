package com.rescue.system.repository;

import com.rescue.system.entity.Service;
import com.rescue.system.entity.ServiceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceRepository extends JpaRepository<Service, Long> {

    List<Service> findByCompanyIdAndIsAvailableTrue(Long companyId);

    List<Service> findByTypeAndIsAvailableTrue(ServiceType type);

    List<Service> findByCompanyId(Long companyId);
}
