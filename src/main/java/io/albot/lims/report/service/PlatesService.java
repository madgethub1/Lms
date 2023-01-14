package io.albot.lims.report.service;

import io.albot.lims.report.model.dto.PlatesEntity;
import io.albot.lims.report.model.web.PlatesCreation;
import org.springframework.data.domain.Page;

import java.util.List;

public interface PlatesService {

    String savePlates(PlatesCreation platesCreation);

    List<PlatesEntity> findAll();

    PlatesEntity getPlatesById(String uniqueId);

    List<PlatesEntity> getPlatesByName(String name);

    Page<PlatesEntity> getPlatesByPagination(int page,String sortField,String sortDirection,int recordPerPage);

    PlatesEntity updatePlates(PlatesCreation platesCreation);

	Page<PlatesEntity> findAllRecentViewers(int userId, int page, String sortField, String sortDirection, int recordPerPage);

    String generateBatchId();
}
