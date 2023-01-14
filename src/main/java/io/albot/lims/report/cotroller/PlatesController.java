package io.albot.lims.report.cotroller;

import io.albot.lims.report.model.web.PlatesCreation;
import io.albot.lims.report.model.web.Response;
import io.albot.lims.report.service.PlatesService;
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
@Api(value = "PlatesController", tags = {"PlatesController"})
@CrossOrigin(origins = {"http://localhost:3000", "http://3.141.111.88"})
@RequestMapping("/v1/api/plates")
@RestController
public class PlatesController {
    private static final Logger logger = LoggerFactory.getLogger(PlatesController.class);
    private final PlatesService platesServices;
    private String success = "Success";

    public PlatesController(PlatesService platesServices) {
        this.platesServices = platesServices;
    }

    @PostMapping("/save")
    @ApiOperation("Create Plates")
    public ResponseEntity<Response> createPlates(@RequestBody PlatesCreation platesCreation) {
        logger.info("Create Plates {createPlates();}");
        return ResponseEntity.ok(new Response().setStatus(success)
                .setStatusCode(HttpStatus.OK.value())
                .setMessage(platesServices.savePlates(platesCreation))
        );
    }

    @GetMapping("/all")
    @ApiOperation("Fetch All Plates")
    public ResponseEntity<Response> getAllPlates() {
        logger.info("Get All Plates {getAllPlates();}");
        return ResponseEntity.ok(new Response().setStatus(success)
                .setStatusCode(HttpStatus.OK.value())
                .setMessage("Successfully retrieving all Plates information")
                .setData(platesServices.findAll()));
    }

    @GetMapping("/{uniqueId}")
    @ApiOperation("Fetch Plates By PlateId")
    public ResponseEntity<Response> getPlatesById(@PathVariable("uniqueId") String uniqueId) {
        logger.info("Get Plates By Id {getPlatesById();}");
        return ResponseEntity.ok(new Response().setStatus(success)
                .setStatusCode(HttpStatus.OK.value())
                .setMessage("Successfully retrieving Plate information")
                .setData(platesServices.getPlatesById(uniqueId)));
    }

    @GetMapping("/name/{name}")
    @ApiOperation("Fetch Plates By Plate Name")
    public ResponseEntity<Response> getPlatesByName(@PathVariable("name") String name) {
        logger.info("Fetch Plates By Plate Name {getPlatesByName();}");
        return ResponseEntity.ok(new Response().setStatus(success)
                .setStatusCode(HttpStatus.OK.value())
                .setMessage("Successfully retrieving Plate information By Name")
                .setData(platesServices.getPlatesByName(name)));
    }

    @GetMapping("/page/{page}")
    @ApiOperation("Pagination On Plates")
    public ResponseEntity<Response> getPlatesByPagination(@PathVariable("page") int page,
                                                          @RequestParam("sortField") String sortField,
                                                          @RequestParam("sortDirection") String sortDirection,
                                                          @RequestParam("recordPerPage") int recordPerPage) {
        logger.info("Pagination On Plates {getPlatesByPagination();}");
        return ResponseEntity.ok(new Response().setStatus(success)
                .setStatusCode(HttpStatus.OK.value())
                .setMessage("Successfully retrieving Pagination on Plate")
                .setData(platesServices.getPlatesByPagination(page,sortField,sortDirection,recordPerPage)));
    }

    @PutMapping("/update")
    @CrossOrigin(origins = {"http://localhost:3000", "http://3.141.111.88"})
    @ApiOperation("update plates")
    public ResponseEntity<Response> updatePlates(@RequestBody PlatesCreation platesCreation) {
        logger.info("update plates {updatePlates();}");
        return ResponseEntity.ok(new Response().setStatus(success)
                .setStatusCode(HttpStatus.OK.value())
                .setMessage("Plates Updated Successfully")
                .setData(platesServices.updatePlates(platesCreation))
        );
    }
    
    @GetMapping("/recentView/{page}/{userid}")
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
                .setData(platesServices.findAllRecentViewers(userId, page, sortField, sortDirection, recordPerPage)));
    }

    @GetMapping("/batchIdGeneration")
    @ApiOperation("Auto populate Batch Id")
    public ResponseEntity<Response> generateBatchId() {
        logger.info("Batch Id generated");
        return ResponseEntity.ok(new Response().setStatus(success)
                .setStatusCode(HttpStatus.OK.value())
                .setData(platesServices.generateBatchId())
        );
    }
}
