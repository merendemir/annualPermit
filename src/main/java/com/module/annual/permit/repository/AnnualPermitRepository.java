package com.module.annual.permit.repository;

import com.module.annual.permit.enums.AnnualPermitStatus;
import com.module.annual.permit.model.AnnualPermit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface AnnualPermitRepository extends JpaRepository<AnnualPermit, Long> {

    List<AnnualPermit> findAllByEmployeeIdAndAnnualPermitStatusAndCreatedOnBetween(Long employeeId,
                                                                                   AnnualPermitStatus status,
                                                                                   Date startDate,
                                                                                   Date endDate);

    Boolean existsByEmployeeIdAndAnnualPermitStatus(Long employeeId, AnnualPermitStatus status);

    Optional<AnnualPermit> findByEmployeeIdAndAnnualPermitStatus(Long employeeId, AnnualPermitStatus status);
}
