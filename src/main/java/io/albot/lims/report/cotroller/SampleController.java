package io.albot.lims.report.cotroller;

import io.albot.lims.report.model.web.*;
import io.albot.lims.report.service.MicroBiomeService;
import io.albot.lims.report.service.SampleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
//@CrossOrigin(origins = "http://localhost:3000")
@Api(value = "SampleController", tags = {"SampleController"})
@CrossOrigin(origins = {"http://localhost:3000", "http://3.141.111.88"})
@RequestMapping("/v1/api")
@RestController
public class SampleController {
    private static final Logger logger = LoggerFactory.getLogger(SampleController.class);
    private final SampleService sampleService;
    private final MicroBiomeService microBiomeService;
    private String success = "Success";

    public SampleController(SampleService sampleService, MicroBiomeService microBiomeService) {
        this.sampleService = sampleService;
        this.microBiomeService = microBiomeService;
    }

    @PostMapping("/sample/save")
    @ApiOperation("Create Sample")
    public ResponseEntity<Response> createSample(@RequestBody SampleCreation sampleCreation) {
        logger.info("Create Sample {createSample();}");
        return ResponseEntity.ok(new Response().setStatus(success)
                .setStatusCode(HttpStatus.OK.value())
                .setMessage(sampleService.saveSample(sampleCreation))
        );
    }

    @GetMapping("/sample/{uniqueId}")
    @ApiOperation("Fetch Sample Through Sample Id")
    public ResponseEntity<Response> getSampleById(@PathVariable("uniqueId") String uniqueId) {
        logger.info("Fetch Sample Through Sample Id {getSampleById();}");
        return ResponseEntity.ok(new Response().setStatus(success)
                .setStatusCode(HttpStatus.OK.value())
                .setMessage("Successfully retrieving a Sample information")
                .setData(sampleService.getBySampleId(uniqueId)));
    }

    @GetMapping("/sample/all")
    @ApiOperation("Fetch All Sample")
    public ResponseEntity<Response> getAllSample() {
        logger.info("Fetch All Sample {getAllSample();}");
        return ResponseEntity.ok(new Response().setStatus(success)
                .setStatusCode(HttpStatus.OK.value())
                .setMessage("Successfully retrieving all sample information")
                .setData(sampleService.findAll()));
    }

    @GetMapping("/sample/stage/{stage}/page/{page}")
    @ApiOperation("Fetch All Sample Through Sample Stage")
    public ResponseEntity<Response> getSampleByStages(@PathVariable("stage") String stage,
                                                      @PathVariable("page") int page,
                                                      @RequestParam("sortField") String sortField,
                                                      @RequestParam("sortDirection") String sortDirection,
                                                      @RequestParam("recordPerPage") int recordPerPage) {
        logger.info("Fetch All Sample Through Sample Stage {getSampleByStages();}");
        return ResponseEntity.ok(new Response().setStatus(success)
                .setStatusCode(HttpStatus.OK.value())
                .setMessage("Successfully retrieving all selected sample information")
                .setData(sampleService.findByStages(stage, page, sortField, sortDirection, recordPerPage)));
    }

    @GetMapping("/sample/page/{page}")
    @ApiOperation("Get All Sample Data with Pagination")
    public ResponseEntity<Response> getSampleByPagination(@PathVariable("page") int page,
                                                          @RequestParam("sortField") String sortField,
                                                          @RequestParam("sortDirection") String sortDirection,
                                                          @RequestParam("recordPerPage") int recordPerPage) {
        logger.info("Get All Sample Data with Pagination {getPlatesByPagination();}");
        return ResponseEntity.ok(new Response().setStatus(success)
                .setStatusCode(HttpStatus.OK.value())
                .setMessage("Successfully retrieving Pagination on Sample")
                .setData(sampleService.getSampleByPagination(page, sortField, sortDirection, recordPerPage)));
    }

    @PutMapping("/sample/update")
    @ApiOperation("Update Sample Data By Sample Id")
    public ResponseEntity<Response> updateSample(@RequestBody SampleCreation sampleCreation) {
        logger.info("Update Sample Data By Sample Id {updateSample();}");
        return ResponseEntity.ok(new Response().setStatus(success)
                .setStatusCode(HttpStatus.OK.value())
                .setMessage("Sample Data Updated Successfully")
                .setData(sampleService.updateSample(sampleCreation))
        );
    }
  /*  
    @GetMapping("/recentView/{userid}")
    @ApiOperation("Fetch All Recent view Samples")
    public ResponseEntity<Response> getAllRecentViewers(@PathVariable("userid") int userId) {
        logger.info("Fetch All Recent View Samples {getAllRecentViewers();}");
        return ResponseEntity.ok(new Response().setStatus(success)
                .setStatusCode(HttpStatus.OK.value())
                .setMessage("Successfully retrieving all sample information")
                .setData(sampleService.findAllRecentViewers(userId)));
    } */
    
    @GetMapping("/sample/recentView/{page}/{userid}")
    @ApiOperation("Fetch All Recent view Samples")
    public ResponseEntity<Response> getAllRecentViewers(@PathVariable("userid") int userId,
														 @PathVariable("page") int page,
                                                          @RequestParam("sortField") String sortField,
                                                          @RequestParam("sortDirection") String sortDirection,
                                                          @RequestParam("recordPerPage") int recordPerPage) {
        logger.info("Fetch All Recent View Samples {getAllRecentViewers();}");
        return ResponseEntity.ok(new Response().setStatus(success)
                .setStatusCode(HttpStatus.OK.value())
                .setMessage("Successfully retrieving all sample information")
                .setData(sampleService.findAllRecentViewers(userId, page, sortField, sortDirection, recordPerPage)));
    }
    
    
    @GetMapping("/sample/search/{page}/{searchName}")
    @ApiOperation("Fetch samples based on user search")
    public ResponseEntity<Response> getSampleSearch(@PathVariable("searchName") String searchName,
														 @PathVariable("page") int page,
                                                          @RequestParam("sortField") String sortField,
                                                          @RequestParam("sortDirection") String sortDirection,
                                                          @RequestParam("recordPerPage") int recordPerPage) {
        logger.info("Fetch samples based on user search {getSampleSearch();}");
        return ResponseEntity.ok(new Response().setStatus(success)
                .setStatusCode(HttpStatus.OK.value())
                .setMessage("Successfully retrieving sample information")
                .setData(sampleService.findSampleSearch(searchName, page, sortField, sortDirection, recordPerPage)));
    }
    
    
    @PutMapping("/sample/update/plates")
    @ApiOperation("Update Sample Data By Sample Id")
    public ResponseEntity<Response> updateSampleByPlate(@RequestBody SampleByPlateUpdate sampleByPlateUpdate) {
        logger.info("Update Sample Data with plate Id {updateSamplePlate();}");
        return ResponseEntity.ok(new Response().setStatus(success)
                .setStatusCode(HttpStatus.OK.value())
                .setData(sampleService.updateSampleByPlate(sampleByPlateUpdate))
        );
    }

    @GetMapping("/sample/sampleIdGeneration")
    @ApiOperation("Auto populate Sample Id")
    public ResponseEntity<Response> generateSampleId() {
        logger.info("Sample Id generated");
        return ResponseEntity.ok(new Response().setStatus(success)
                .setStatusCode(HttpStatus.OK.value())
                .setData(sampleService.generateSampleId())
        );
    }

    @GetMapping("/sample/byPlateId/{page}")
    @ApiOperation("Get All Sample Data with PlateId")
    public ResponseEntity<Response> getSamplesByPlateId(@PathVariable("page") int page,
                                                          @RequestParam("plateId") String plateId,
                                                          @RequestParam("sortField") String sortField,
                                                          @RequestParam("sortDirection") String sortDirection,
                                                          @RequestParam("recordPerPage") int recordPerPage) {
        logger.info("Get All Sample Data with PlateId {getSamplesByPlateId();}");
        return ResponseEntity.ok(new Response().setStatus(success)
                .setStatusCode(HttpStatus.OK.value())
                .setMessage("Successfully retrieving sample by plateid")
                .setData(sampleService.getSamplesByPlateId(page, plateId, sortField, sortDirection, recordPerPage)));
    }

    @PostMapping("/samples/{page}")
    public ResponseEntity<Response> sampleReports(@RequestBody ReportListRequest reportListRequest, @PathVariable("page") int page,
                                                  @RequestParam("sortField") String sortField,
                                                  @RequestParam("sortDirection") String sortDirection,
                                                  @RequestParam("recordPerPage") int recordPerPage) {
        log.info("Get All reports {reportList();}");
        return ResponseEntity.ok(new Response().setStatus(success)
                .setStatusCode(HttpStatus.OK.value())
                .setMessage("Successfully retrieving all Reports information")
                .setData(microBiomeService.findAllReportsLab(reportListRequest, page, sortField, sortDirection, recordPerPage)));
    }
}