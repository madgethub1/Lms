package io.albot.lims.report.service;

import io.albot.lims.report.model.dto.SampleEntity;
import io.albot.lims.report.model.web.SampleByPlateUpdate;
import io.albot.lims.report.model.web.SampleCreation;
import io.albot.lims.report.model.web.SampleResponseBean;
import org.springframework.data.domain.Page;

import java.util.List;

public interface SampleService {
    String saveSample(SampleCreation sampleCreation);

    SampleResponseBean getBySampleId(String uniqueId);

    List<SampleEntity> findAll();

    Page<SampleEntity> findByStages(String stage,int page, String sortField, String sortDirection, int recordPerPage);

    Page<SampleEntity> getSampleByPagination(int page, String sortField, String sortDirection, int recordPerPage);

    SampleEntity updateSample(SampleCreation sampleCreation);

	//List<SampleEntity> findAllRecentViewers(int userId);
	Page<SampleEntity> findAllRecentViewers(int userid, int page, String sortField, String sortDirection, int recordPerPage);
	
	String updateSampleByPlate(SampleByPlateUpdate sampleByPlateUpdate);

	Page<SampleEntity> findSampleSearch(String searchName, int page, String sortField, String sortDirection, int recordPerPage);

    String generateSampleId();

    Page<SampleEntity> getSamplesByPlateId(int page, String plateId, String sortField, String sortDirection, int recordPerPage);
}
