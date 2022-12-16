package com.module.annual.permit.repository;

import com.module.annual.permit.model.AnnualPermit;
import com.module.annual.permit.model.AnnualPermitRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnnualPermitRequestRepository extends JpaRepository<AnnualPermitRequest, Long> {

}
