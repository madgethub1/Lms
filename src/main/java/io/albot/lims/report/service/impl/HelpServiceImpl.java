package io.albot.lims.report.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.google.common.io.Files;
import io.albot.lims.report.exceptions.HelpException;
import io.albot.lims.report.model.dto.HelpEntity;
import io.albot.lims.report.model.web.HelpBean;
import io.albot.lims.report.repos.postgres.HelpRepository;
import io.albot.lims.report.service.HelpService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
public class HelpServiceImpl implements HelpService {

    @Autowired
    private AmazonS3 amazonS3Client;

    @Value("${aws-s3-details.bucket}")
    String bucket;

    @Autowired
    private HelpRepository helpRepository;

    @Override
    public File convertMultiPartFileToFile(MultipartFile file) {
        File convertedFile = new File(file.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(convertedFile)) {
            fos.write(file.getBytes());
        } catch (IOException e) {
            log.debug(e.toString());
        }
        return convertedFile;
    }

    @Override
    @Transactional
    public String saveHelpDetails(String filename, HelpBean helpBean, File file) throws IOException, URISyntaxException {
        HelpEntity helpEntity;
        String fileName="";
        try {
            if(file != null) {
                String fileExtension = Files.getFileExtension(filename);
                String fileNameWtExt = Files.getNameWithoutExtension(filename);
                 fileName = fileNameWtExt + "_" + System.currentTimeMillis() + "." + fileExtension;
                 amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, file));
            }
            Optional<HelpEntity> help = null;
            if(!Objects.isNull(helpBean.getId()))
                 help = helpRepository.findById(helpBean.getId());
            if(!Objects.isNull(help)){
                helpEntity = help.get();
                helpEntity.setHelpId(helpBean.getId());
                helpEntity.setCreatedDate(new Date());
                helpEntity.setTitle(helpBean.getTitle());
                helpEntity.setDescription(helpBean.getDescription());
                helpEntity.setSeverityLevel(helpBean.getSeverityLevel());
                if(file != null)
                    helpEntity.setAttachmentName(fileName);
            }else{
                helpEntity = new HelpEntity();
                helpEntity.setCreatedDate(new Date());
                helpEntity.setDescription(helpBean.getDescription());
                helpEntity.setTitle(helpBean.getTitle());
                helpEntity.setSeverityLevel(helpBean.getSeverityLevel());
                if(file != null)
                    helpEntity.setAttachmentName(fileName);
            }

            helpRepository.save(helpEntity);
        } catch (Exception e) {
            //return "Failed to Save";
            throw new HelpException("Failed to Save");
        }
        return "Help information saved successfully";
    }

    @Override
    public String getS3Link(String filename) {
        URL url = null;
        try {
            java.util.Date expiration = new java.util.Date();
            long expTimeMillis = expiration.getTime();
            expTimeMillis += 1000 * 60 * 60;
            expiration.setTime(expTimeMillis);
            url = amazonS3Client.generatePresignedUrl(bucket, filename, expiration);
        } catch (Exception e) {
            return "failed";
        }
        return url.toExternalForm();
    }

    @Override
    public List<HelpEntity> getHelp() {
        return helpRepository.findAll();
    }
}
