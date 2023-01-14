package io.albot.lims.report.repos.postgres;

import io.albot.lims.report.model.dto.MicroBiomesEntity;
import io.albot.lims.report.model.dto.PlatesEntity;
import io.albot.lims.report.model.dto.ProtocolEntity;
import io.albot.lims.report.model.dto.UserDemoGraphicsEntity;
import io.albot.lims.report.model.web.ReportListRequest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface MicroBiomesRepository extends JpaRepository<MicroBiomesEntity, Long> {
    @Override
    MicroBiomesEntity save(MicroBiomesEntity microBiomesEntity);
/*
    @Query("SELECT t FROM MicroBiomesEntity t WHERE t.phoneNumber=?1 and t.reportType=?2 ORDER BY t.reportId DESC")
    List<MicroBiomesEntity> getReport1(UserDemoGraphicsEntity user, String reportType);
*/
    @Query("SELECT t FROM MicroBiomesEntity t WHERE t.reportId=?1 and t.reportType=?2")
    MicroBiomesEntity getReport(long reportid, String reportType);

    @Query("SELECT t FROM MicroBiomesEntity t WHERE t.reportType=?1 and t.emailid =?2 and (t.testReported >= ?3 and t.testReported <= ?4)")
    Page<MicroBiomesEntity> getReportList(String reportType, String emailid, Date startDate, Date endDate, Pageable pageable);

    @Query(value= "SELECT t FROM PlatesEntity t WHERE lower(t.platesName) LIKE CONCAT('%',:searchName,'%') OR lower(t.platesGeneratedId) LIKE CONCAT('%',:searchName,'%') OR lower(t.platesStatus) LIKE CONCAT('%',:searchName,'%') OR lower(t.recordType) LIKE CONCAT('%',:searchName,'%')")
    Page<PlatesEntity> findPlateSearch(@Param("searchName") String searchName, Pageable pageable);

    @Query(value= "SELECT t FROM ProtocolEntity t WHERE lower(t.protocolName) LIKE CONCAT('%',:searchName,'%') OR lower(t.isActive) LIKE CONCAT('%',:searchName,'%')")
    Page<ProtocolEntity> findProtocolSearch(@Param("searchName") String searchName, Pageable pageable);

    @Query("SELECT t FROM MicroBiomesEntity t WHERE t.sampleid=?1")
    MicroBiomesEntity findBySampleId(String sampleid);

    @Query("SELECT t FROM MicroBiomesEntity t WHERE t.reportType=?1 and (t.testReported >= ?2 and t.testReported <= ?3)")
    Page<MicroBiomesEntity> getReportListData(String reportType, Date startDate, Date endDate, Pageable pageable);
}