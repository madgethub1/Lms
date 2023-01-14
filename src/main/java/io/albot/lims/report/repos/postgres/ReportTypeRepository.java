package io.albot.lims.report.repos.postgres;

import org.springframework.data.jpa.repository.JpaRepository;

import io.albot.lims.report.model.dto.ReportTypeEntity;

public interface ReportTypeRepository extends JpaRepository<ReportTypeEntity, Long> {

}
