package io.albot.lims.report.repos.postgres;

import io.albot.lims.report.model.dto.PatientEntity;
import io.albot.lims.report.model.dto.SampleEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PatientRepository extends JpaRepository<PatientEntity, Long> {

    @Override
    PatientEntity save(PatientEntity patientEntity);


    @Query("SELECT t FROM PatientEntity t WHERE t.patientPhone = :patientPhone")
    PatientEntity findByPhone(@Param("patientPhone") String patientPhone);

    @Query("SELECT t FROM PatientEntity t WHERE t.patientEmail = :patientEmail")
    PatientEntity findByEmail(@Param("patientEmail") String patientEmail);

    @Query("SELECT t FROM SampleEntity t WHERE t.patientId = :patientId")
    Page<SampleEntity> findAllSampleByPatientId(@Param("patientId") PatientEntity patientId, Pageable pageable);

    @Query("SELECT t FROM SampleEntity t WHERE t.patientId = :patientId")
    SampleEntity findByPatientId(@Param("patientId") PatientEntity patientId);
}
