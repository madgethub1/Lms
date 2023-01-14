package io.albot.lims.report.repos.postgres;

import java.util.Date;
import java.util.List;

import io.albot.lims.report.model.dto.MicroBiomesEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import io.albot.lims.report.model.dto.OralEcologixReportEntity;

public interface OralEcologixRepository extends JpaRepository<OralEcologixReportEntity, Long> {
	
    @Query("SELECT t FROM OralEcologixReportEntity t WHERE t.reportId=?1 and t.reportType=?2")
    OralEcologixReportEntity getReport(long reportid, String reportType);

    @Query("SELECT t FROM OralEcologixReportEntity t WHERE t.reportType=?1 and t.emailid =?2 and (t.testReported >= ?3 and t.testReported <= ?4)")
	Page<OralEcologixReportEntity> getReportList(String reportType, String emailid, Date currminusdate, Date currdate, Pageable pageRequest);

    @Query("SELECT t FROM OralEcologixReportEntity t WHERE t.sampleid=?1")
    OralEcologixReportEntity findBySampleId(String sampleid);

    @Query("SELECT t FROM OralEcologixReportEntity t WHERE t.reportType=?1 and (t.testReported >= ?2 and t.testReported <= ?3)")
    Page<OralEcologixReportEntity> getReportListData(String reportType, Date currminusdate, Date currdate, Pageable pageRequest);
}
