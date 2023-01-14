package io.albot.lims.report.cotroller;

import io.albot.lims.report.model.web.*;
import io.albot.lims.report.service.MicroBiomeService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.itextpdf.io.source.ByteArrayOutputStream;

import java.io.ByteArrayInputStream;
import java.util.Date;

@Slf4j
@Api(value = "MicroBiomesController", tags = {"MicroBiomesController"})
@CrossOrigin(origins = {"http://localhost:3000", "http://3.141.111.88"})
@RequestMapping("/v1/api/sample/demo")
@RestController
public class MicroBiomesController {
    private final MicroBiomeService microBiomeService;
    private String success = "Success";

    public MicroBiomesController(MicroBiomeService microBiomeService) {
        this.microBiomeService = microBiomeService;
    }

    @PostMapping("/save")
    public ResponseEntity<Response> createUser(@RequestBody MicroBiomesCreation microBiomesCreation) {
        return ResponseEntity.ok(new Response().setStatus(success)
                .setStatusCode(HttpStatus.OK.value())
                .setMessage(microBiomeService.saveReport(microBiomesCreation))
        );
    }

   /* @GetMapping("/view/report")
    public ResponseEntity<InputStreamResource> viewReport1(@RequestParam("phoneNumber") String phoneNumber, @RequestParam("reportName") String reportName) {
       HttpHeaders header = new HttpHeaders();
       header.set(HttpHeaders.CONTENT_DISPOSITION,"inline; filename=" +phoneNumber+"_"+System.currentTimeMillis()+".html");
       String bis =microBiomeService.viewReport1(phoneNumber, reportName);
       return ResponseEntity
               .ok()
               .headers(header)
               .contentType(MediaType.TEXT_HTML)
               .body(new InputStreamResource(new ByteArrayInputStream(bis.toString().getBytes())));
    }
    */


    @GetMapping("/view/report")
    public ResponseEntity<InputStreamResource> viewReport(@RequestParam("reportId") long reportid, @RequestParam("reportType") String reportType) {
        HttpHeaders header = new HttpHeaders();
        header.set(HttpHeaders.CONTENT_DISPOSITION,"inline; filename=" +reportid+"_"+System.currentTimeMillis()+".html");
        String bis =microBiomeService.viewReport(reportid, reportType);
        return ResponseEntity
                .ok()
                .headers(header)
                .contentType(MediaType.TEXT_HTML)
                .body(new InputStreamResource(new ByteArrayInputStream(bis.toString().getBytes())));
    }

    @GetMapping("/download/report")
    public ResponseEntity<InputStreamResource> downloadReport(@RequestParam("phoneNumber") String phoneNumber, @RequestParam("reportName") String reportName) {
        // public ResponseEntity<?> downloadReport(@RequestParam("phoneNumber") String phoneNumber, @RequestParam("reportName") String reportName) {
        HttpHeaders header = new HttpHeaders();
        header.set(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=" +phoneNumber+"_"+System.currentTimeMillis()+".pdf");
        ByteArrayInputStream bis =microBiomeService.getReport(phoneNumber, reportName);
        // ByteArrayOutputStream bos =microBiomeService.getReport(phoneNumber, reportName);
        // byte[] bytes = bos.toByteArray();
        return ResponseEntity
                .ok()
                .headers(header)
                .contentType(MediaType.APPLICATION_PDF)
                //.body(bytes);
                .body(new InputStreamResource(bis));
    }


    @PostMapping("/report/list/{page}")
    public ResponseEntity<Response> reportList(@RequestBody ReportListRequest reportListRequest, @PathVariable("page") int page,
                                               @RequestParam("sortField") String sortField,
                                               @RequestParam("sortDirection") String sortDirection,
                                               @RequestParam("recordPerPage") int recordPerPage) {
        log.info("Get All reports {reportList();}");
        return ResponseEntity.ok(new Response().setStatus(success)
                .setStatusCode(HttpStatus.OK.value())
                .setMessage("Successfully retrieving all Reports information")
                .setData(microBiomeService.findAllReports(reportListRequest, page, sortField, sortDirection, recordPerPage)));
    }


    @GetMapping("/userType/{userId}")
    public String getUserType(@PathVariable("userId") String userId) {
        log.info("Get All reports {getUserType();}");
        /*return ResponseEntity.ok(new Response().setStatus(success)
                .setStatusCode(HttpStatus.OK.value())
                .setMessage("Successfully retrieving userType information")
                .setData(microBiomeService.getUserTypeDetails(userId))); */
        return microBiomeService.getUserTypeDetails(userId);
    }


    @GetMapping("/reportType/all")
    public ResponseEntity<Response> getReportTypeAll() {
        log.info("Get All reports {getReportTypeAll();}");
        return ResponseEntity.ok(new Response().setStatus(success)
                .setStatusCode(HttpStatus.OK.value())
                .setMessage("Successfully retrieving report types information")
                .setData(microBiomeService.getReportTypeAll()));
    }

    @PostMapping("/globalSearch")
    public ResponseEntity<Response> globalSearch(@RequestParam("searchtype") String searchtype, @RequestParam("searchName") String searchName, @RequestBody PaginationRequestBean pagination) {
        log.info("GlobalSearch  {globalSearch();}");
        return ResponseEntity.ok(new Response().setStatus(success)
                .setStatusCode(HttpStatus.OK.value())
                .setMessage("Successfully retrieving all Search information")
                .setData(microBiomeService.globalSearch(searchtype,searchName , pagination.getPage(), pagination.getSortField(), pagination.getSortDirection(), pagination.getRecordPerPage())));
    }

    @PostMapping("/saveUpload")
    public ResponseEntity<Response> saveUpload(@RequestBody String jsonReport) {
        return ResponseEntity.ok(new Response().setStatus(success)
                .setStatusCode(HttpStatus.OK.value())
                .setMessage(microBiomeService.saveUpload(jsonReport))
        );
    }

    @PostMapping("/validateUpload")
    public ResponseEntity<Response> validateUpload(@RequestBody String jsonReport) {
        return ResponseEntity.ok(new Response().setStatus(success)
                .setStatusCode(HttpStatus.OK.value())
                .setMessage("Report Validation")
                .setData(microBiomeService.validateUpload(jsonReport))
        );
    }

    @PostMapping("/reportStatus")
    public ResponseEntity<Response> reportStatus(@RequestBody ReportStatusBean reportBean) {
        return ResponseEntity.ok(new Response().setStatus(success)
                .setStatusCode(HttpStatus.OK.value())
                .setData(microBiomeService.reportStatus(reportBean))
        );
    }

    @PostMapping("/report/details")
    public ResponseEntity<Response> getReportDetails(@RequestParam("reportId") long reportid, @RequestParam("reportType") String reportType) {
        log.info("Get reports {getReportDetails();}");
        return ResponseEntity.ok(new Response().setStatus(success)
                .setStatusCode(HttpStatus.OK.value())
                .setMessage("Successfully retrieving Reports information")
                .setData(microBiomeService.getReportDetails(reportid, reportType)));
    }
}
