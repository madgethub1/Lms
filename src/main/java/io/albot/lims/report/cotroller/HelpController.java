package io.albot.lims.report.cotroller;

import io.albot.lims.report.model.web.HelpBean;
import io.albot.lims.report.model.web.Response;
import io.albot.lims.report.service.HelpService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

@Slf4j
@Api(value = "HelpController", tags = {"HelpController"})
@CrossOrigin(origins = {"http://localhost:3000", "http://3.141.111.88"})
@RequestMapping("/v1/api")
@RestController
public class HelpController {

    private HelpService helpService;
    private String success = "Success";

    public HelpController(HelpService helpService) {
        this.helpService = helpService;
    }

    @ApiOperation("Create Help Data")
    @PostMapping("/help/create")
    public ResponseEntity<Response> helpCreate(@RequestParam(value = "attachment", required = false) MultipartFile mfile, @RequestPart("title") String title,  @RequestPart("description") String description,  @RequestPart("severityLevel") String severityLevel) throws IOException, URISyntaxException {
        HelpBean helpBean = new HelpBean();
        helpBean.setDescription(description);
        helpBean.setTitle(title);
        helpBean.setSeverityLevel(severityLevel);
        File file = null;
        String fileName = "";
        if(mfile != null) {
            file = helpService.convertMultiPartFileToFile(mfile);
            fileName = mfile.getOriginalFilename();
        }
        return ResponseEntity.ok(new Response().setStatus(success)
                .setStatusCode(HttpStatus.OK.value())
                .setMessage(helpService.saveHelpDetails(fileName, helpBean, file)));
    }

    @ApiOperation("Get Document By Document Name")
    @GetMapping("/document")
    public String getImage(@RequestParam("filename") String filename) {
        return helpService.getS3Link(filename);
    }

    @ApiOperation("Get Help Data")
    @GetMapping("/help")
    public ResponseEntity<Response> getHelpData() {
        log.info("Get help data {getHelpData();}");
        return ResponseEntity.ok(new Response().setStatus(success)
                .setStatusCode(HttpStatus.OK.value())
                .setMessage("Successfully retrieving Help information")
                .setData(helpService.getHelp()));
    }
}
