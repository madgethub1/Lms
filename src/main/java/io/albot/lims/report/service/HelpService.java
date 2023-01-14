package io.albot.lims.report.service;

import io.albot.lims.report.model.dto.HelpEntity;
import io.albot.lims.report.model.web.HelpBean;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public interface HelpService {
    File convertMultiPartFileToFile(MultipartFile mfile);

    String saveHelpDetails(String originalFilename, HelpBean helpBean, File file) throws URISyntaxException, IOException;

    String getS3Link(String filename);

    List<HelpEntity> getHelp();
}
