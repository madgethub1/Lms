package io.albot.lims.report.cotroller;

import io.albot.lims.report.model.web.Response;
import io.albot.lims.report.service.PatientService;
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
@Api(value = "PatientController", tags = {"PatientController"})
@CrossOrigin(origins = {"http://localhost:3000", "http://3.141.111.88"})
@RequestMapping("/v1/api/patient")
@RestController
public class PatientController {
    private static final Logger logger = LoggerFactory.getLogger(PatientController.class);

    private final PatientService patientService;
    private String success = "Success";

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping("/{emailId}")
    @ApiOperation("Fetch Patient Data By Email_ID")
    public ResponseEntity<Response> getPatientByEmailId(@PathVariable("emailId") String emailId) {
        logger.info("For login Identify by given user {}", emailId);
        return ResponseEntity.ok(new Response().setStatus(success)
                .setStatusCode(HttpStatus.OK.value())
                .setMessage("Successfully retrieving a Patient information")
                .setData(patientService.getPatientByEmailId(emailId)));
    }

    @GetMapping("/sampledata/{emailId}/{page}")
    @ApiOperation("Fetch Patient Data By PatientId")
    public ResponseEntity<Response> getSamplesByEmailId(@PathVariable("emailId") String emailId,
                                                        @PathVariable("page") int page,
                                                        @RequestParam("sortField") String sortField,
                                                        @RequestParam("sortDirection") String sortDirection,
                                                        @RequestParam("recordPerPage") int recordPerPage) {
        logger.info("For login Identify by given user {}", emailId);
        return ResponseEntity.ok(new Response().setStatus(success)
                .setStatusCode(HttpStatus.OK.value())
                .setMessage("Successfully retrieving a sample information")
                .setData(patientService.getSamplesByEmailId(emailId, page, sortField, sortDirection, recordPerPage)));
    }
}
