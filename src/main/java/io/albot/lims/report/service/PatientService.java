package io.albot.lims.report.service;

import io.albot.lims.report.model.dto.SampleEntity;
import io.albot.lims.report.model.web.PatientCreation;
import org.springframework.data.domain.Page;

public interface PatientService {
    PatientCreation getPatientByEmailId(String emailId);

    Page<SampleEntity> getSamplesByEmailId(String emailId, int page, String sortField, String sortDirection, int recordPerPage);
}
