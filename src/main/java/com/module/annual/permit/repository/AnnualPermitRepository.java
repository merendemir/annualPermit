package com.module.annual.permit.repository;

import com.module.annual.permit.model.AnnualPermit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnnualPermitRepository extends JpaRepository<AnnualPermit, Long> {

}
