package io.albot.lims.report.service;

import io.albot.lims.report.model.dto.ProtocolEntity;
import io.albot.lims.report.model.web.ProtocolCreation;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.net.URISyntaxException;
import java.util.List;

public interface ProtocolService {
    String saveProtocol(String fileName, File file, String bucketName, String protocolName, String createdBy)throws URISyntaxException;

    File convertMultiPartFileToFile(MultipartFile file);

    String getS3Link(String fileName, String bucketName);

    List<ProtocolEntity> findAll();

    List<ProtocolEntity> getProtocolByName(String name);

    ProtocolEntity getProtocolById(long id);

    ProtocolEntity updateProtocol(ProtocolCreation protocolCreation);

	Page<ProtocolEntity> getProtocolByPagination(int page, String sortField, String sortDirection, int recordPerPage);
}
