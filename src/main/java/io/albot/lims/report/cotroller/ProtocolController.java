package io.albot.lims.report.cotroller;

import io.albot.lims.report.model.web.ProtocolCreation;
import io.albot.lims.report.model.web.Response;
import io.albot.lims.report.model.web.ViewDownloadReport;
import io.albot.lims.report.service.ProtocolService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

@Slf4j
//@CrossOrigin(origins = "http://localhost:3000")
@Api(value = "ProtocolController", tags = {"ProtocolController"})
@CrossOrigin(origins = {"http://localhost:3000", "http://3.141.111.88"})
@RequestMapping("/v1/api/protocol")
@RestController
public class ProtocolController {
    private static final Logger logger = LoggerFactory.getLogger(ProtocolController.class);

    @Value("${aws-s3-details.bucket}")
    String bucket;

    private String success = "Success";
    private final ProtocolService protocolService;

    public ProtocolController(ProtocolService protocolService) {
        this.protocolService = protocolService;
    }

    @PostMapping("/save")
    @ApiOperation("Create Protocol")
    public ResponseEntity<Response> createProtocol(@RequestParam("pdf") MultipartFile multipartFile, @RequestParam("protocolName") String protocolName, @RequestParam("createdBy") String createdBy) throws IOException, URISyntaxException {
        logger.info("Create Protocol {createProtocol();}");
        File file = protocolService.convertMultiPartFileToFile(multipartFile);

        multipartFile.transferTo(file);
        return ResponseEntity.ok(new Response().setStatus(success)
                .setStatusCode(HttpStatus.OK.value())
                .setMessage(protocolService.saveProtocol(multipartFile.getOriginalFilename(), file, bucket, protocolName, createdBy))
        );
    }

	/*
	 * @GetMapping("/pdf")
	 * 
	 * @ApiOperation("Get Image Link From Bucket Through FileName") public String
	 * getPdfFile(@RequestParam("filename") String filename) {
	 * logger.info("Get Image Link From Bucket Through FileName {getPdfFile();}");
	 * return protocolService.getS3Link(filename, bucket); }
	 */
    
    @GetMapping("/pdf")
    @ResponseBody
    @ApiOperation("Get Image Link From Bucket Through FileName")
    public ViewDownloadReport  downloadFile(@RequestParam("filename") String filename) {
    	String file = protocolService.getS3Link(filename, bucket);
    	ResponseEntity<String> download = ResponseEntity.ok().contentType(MediaType.parseMediaType("application/pdf"))
    			.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
    			.body(file);
    	ViewDownloadReport  viewDownload = new ViewDownloadReport();
    	viewDownload.setView(file);
    	viewDownload.setDownload(download);
        return viewDownload;
    }

    @GetMapping("/all")
    @ApiOperation("Fetch All Protocol")
    public ResponseEntity<Response> getAllProtocol() {
        logger.info("Fetch All Protocol {getAllProtocol();}");
        return ResponseEntity.ok(new Response().setStatus(success)
                .setStatusCode(HttpStatus.OK.value())
                .setMessage("Successfully retrieving all protocol information")
                .setData(protocolService.findAll()));
    }

    @GetMapping("/name/{name}")
    @ApiOperation("Fetch Protocol By Protocol Name")
    public ResponseEntity<Response> getProtocolByName(@PathVariable("name") String name) {
        logger.info("Fetch Protocol By Protocol Name {getProtocolByName();}");
        return ResponseEntity.ok(new Response().setStatus(success)
                .setStatusCode(HttpStatus.OK.value())
                .setMessage("Successfully retrieving Protocol information By Name")
                .setData(protocolService.getProtocolByName(name)));
    }

    @GetMapping("/id/{id}")
    @ApiOperation("Fetch Protocol By Protocol Id")
    public ResponseEntity<Response> getProtocolById(@PathVariable("id") long id) {
        logger.info("Fetch Protocol By Protocol Id {getProtocolById();}");
        return ResponseEntity.ok(new Response().setStatus(success)
                .setStatusCode(HttpStatus.OK.value())
                .setMessage("Successfully retrieving Protocol information By Name")
                .setData(protocolService.getProtocolById(id)));
    }

    @PutMapping("/update")
    @ApiOperation("Update Protocol Through Protocol Id")
    public ResponseEntity<Response> updateProtocol(@RequestBody ProtocolCreation protocolCreation) {
        logger.info("Update Protocol Through Protocol Id {updateProtocol();}");
        return ResponseEntity.ok(new Response().setStatus(success)
                .setStatusCode(HttpStatus.OK.value())
                .setMessage("protocol success fully updated")
                .setData(protocolService.updateProtocol(protocolCreation))
        );
    }
    
    @GetMapping("/page/{page}")
    @ApiOperation("Pagination On Protocol")
    public ResponseEntity<Response> getProtocolByPagination(@PathVariable("page") int page,
                                                          @RequestParam("sortField") String sortField,
                                                          @RequestParam("sortDirection") String sortDirection,
                                                          @RequestParam("recordPerPage") int recordPerPage) {
        logger.info("Pagination On Protocol {getProtocolByPagination();}");
        return ResponseEntity.ok(new Response().setStatus(success)
                .setStatusCode(HttpStatus.OK.value())
                .setMessage("Successfully retrieving Pagination on Protocol")
                .setData(protocolService.getProtocolByPagination(page,sortField,sortDirection,recordPerPage)));
    }
}
