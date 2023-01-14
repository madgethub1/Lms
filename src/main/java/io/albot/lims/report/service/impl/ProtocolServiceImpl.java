package io.albot.lims.report.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import io.albot.lims.report.model.dto.ProtocolEntity;
import io.albot.lims.report.model.web.ProtocolCreation;
import io.albot.lims.report.repos.postgres.ProtocolRepository;
import io.albot.lims.report.service.ProtocolService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class ProtocolServiceImpl implements ProtocolService {

    @Autowired
    private AmazonS3 amazonS3Client;

    private final ProtocolRepository protocolRepository;

    public ProtocolServiceImpl(ProtocolRepository protocolRepository) {
        this.protocolRepository = protocolRepository;
    }

    @Override
    public String saveProtocol(String fileName, File file, String bucketName, String protocolName, String createdBy) throws URISyntaxException {
        try {
            protocolRepository.save(fillEntity(fileName, protocolName, createdBy));
            amazonS3Client.putObject(new PutObjectRequest(bucketName, fileName, file));

        } catch (Exception e) {
            log.error("Error uploading aws file", e);
            return "failed";
        }
        return "uploaded";
    }

    @Override
    public File convertMultiPartFileToFile(MultipartFile file) {
        File convertedFile = new File(file.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(convertedFile)) {
            fos.write(file.getBytes());
        } catch (IOException e) {
            log.error("Error converting multipartFile to file", e);
        }
        return convertedFile;
    }

    @Override
    public String getS3Link(String fileName, String bucketName) {
        URL url = null;
        try {
            java.util.Date expiration = new java.util.Date();
            long expTimeMillis = expiration.getTime();
            expTimeMillis += 1000 * 60 * 60;
            expiration.setTime(expTimeMillis);
            url = amazonS3Client.generatePresignedUrl(bucketName, fileName, expiration);
        } catch (Exception e) {
            log.error("Error uploading aws file", e);
            return "failed";
        }
        return url.toExternalForm();
    }

    @Override
    public List<ProtocolEntity> findAll() {
        return protocolRepository.findAll();
    }

    @Override
    public List<ProtocolEntity> getProtocolByName(String name) {
        return protocolRepository.getProtocolByName(name);
    }

    @Override
    public ProtocolEntity getProtocolById(long id) {
        return protocolRepository.findByProtocolId(id);
    }

    @Override
    public ProtocolEntity updateProtocol(ProtocolCreation protocolCreation) {
        ProtocolEntity protocolEntity = protocolRepository.findByProtocolId(protocolCreation.getProtocolId());
        if(!Objects.isNull(protocolEntity)){
            if(!Objects.isNull(protocolCreation.getProtocolName())){
                protocolEntity.setProtocolName(protocolCreation.getProtocolName());
            }
            if(!Objects.isNull(protocolCreation.getIsActive())){
                protocolEntity.setIsActive(protocolCreation.getIsActive());
            }
        }
        return protocolRepository.save(protocolEntity);
    }

    private ProtocolEntity fillEntity(String fileName, String protocolName, String createdBy) {

        ProtocolEntity protocolEntity = new ProtocolEntity();
        protocolEntity.setProtocolPDF(fileName);
        protocolEntity.setIsActive("Y");
        protocolEntity.setProtocolName(protocolName);
        protocolEntity.setCreatedBy(createdBy);
        Date date = new Date();
        protocolEntity.setCreatedOn(date);

        return protocolEntity;
    }

	@Override
	public Page<ProtocolEntity> getProtocolByPagination(int page, String sortField, String sortDirection,
			int recordPerPage) {
		Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        PageRequest pageRequest = PageRequest.of(page - 1, recordPerPage, sort);
		return protocolRepository.findAll(pageRequest);
	}

}
