package io.albot.lims.report.service.impl;

import io.albot.lims.report.exceptions.NotFoundException;
import io.albot.lims.report.model.dto.PatientEntity;
import io.albot.lims.report.model.dto.SampleEntity;
import io.albot.lims.report.model.web.PatientCreation;
import io.albot.lims.report.repos.postgres.PatientRepository;
import io.albot.lims.report.service.PatientService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class PatientServiceImpl implements PatientService {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private final PatientRepository patientRepository;

    public PatientServiceImpl(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @Override
    public PatientCreation getPatientByEmailId(String emailId) {
        PatientEntity patientEntity=patientRepository.findByEmail(emailId);
        if(Objects.isNull(patientEntity)){
           throw new NotFoundException("No data found for given emailId : "+ emailId);
        }
        return convertToPatients(patientEntity);
    }

    @Override
    public Page<SampleEntity> getSamplesByEmailId(String emailId, int page, String sortField, String sortDirection, int recordPerPage) {
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        PageRequest pageRequest = PageRequest.of(page - 1, recordPerPage, sort);
        long patientId = 0;
        PatientEntity patient = new PatientEntity();
        if(!emailId.isEmpty()){
            PatientCreation patientcret =  getPatientByEmailId(emailId);
            if(patientcret == null){
                throw new NotFoundException("No data found for given emailId");
            }else {
                patientId = patientcret.getPatientId();
            }
        }else{
            throw new NotFoundException("Please enter valid emailId");
        }

        patient.setPatientId(patientId);
        return patientRepository.findAllSampleByPatientId(patient, pageRequest);
    }

    private PatientCreation convertToPatients(PatientEntity patientEntity) {
        return modelMapper
                .map(patientEntity, PatientCreation.class);
    }
}
