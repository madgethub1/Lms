package io.albot.lims.report.service;

import io.albot.lims.report.model.dto.ReportTypeEntity;
import io.albot.lims.report.model.web.*;

import java.io.ByteArrayInputStream;
import java.util.List;

import org.springframework.data.domain.Page;

import com.itextpdf.io.source.ByteArrayOutputStream;

public interface MicroBiomeService {
	String saveReport(MicroBiomesCreation microBiomesCreation);

	ByteArrayInputStream getReport(String mobileNo, String fileType);

	// String viewReport1(String mobileNo, String fileType);

	ReportListResponse findAllReports(ReportListRequest reportListRequest, int page, String sortField,
									  String sortDirection, int recordPerPage);


	String getUserTypeDetails(String userid);

	List<ReportTypeEntity> getReportTypeAll();

	String viewReport(long reportid, String reportType);

	GlobalSearchResponse globalSearch(String searchtype, String searchName, int page, String sortField, String sortDirection, int recordPerPage);

	String saveUpload(String jsonReport);

	List<BacteriaReportInfo> validateUpload(String jsonReport);

	ReportListResponse findAllReportsLab(ReportListRequest reportListRequest, int page, String sortField, String sortDirection, int recordPerPage);

	String reportStatus(ReportStatusBean reportBean);

	ReportListContent getReportDetails(long reportid, String reportType);
}
