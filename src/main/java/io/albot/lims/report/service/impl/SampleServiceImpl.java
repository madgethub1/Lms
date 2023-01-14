package io.albot.lims.report.service.impl;

import io.albot.lims.report.config.Constants;
import io.albot.lims.report.exceptions.NotFoundException;
import io.albot.lims.report.model.dto.MicroBiomesEntity;
import io.albot.lims.report.model.dto.OralEcologixReportEntity;
import io.albot.lims.report.model.dto.PatientEntity;
import io.albot.lims.report.model.dto.SampleEntity;
import io.albot.lims.report.model.web.*;
import io.albot.lims.report.repos.postgres.MicroBiomesRepository;
import io.albot.lims.report.repos.postgres.OralEcologixRepository;
import io.albot.lims.report.repos.postgres.PatientRepository;
import io.albot.lims.report.repos.postgres.SampleRepository;
import io.albot.lims.report.service.SampleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class SampleServiceImpl implements SampleService {

    @Autowired
    private SampleRepository sampleRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private MicroBiomesRepository microBiomesRepository;

    @Autowired
    private OralEcologixRepository oralEcologixRepository;

    @Override
    public String saveSample(SampleCreation sampleCreation) {
        SampleEntity sampleEntity = null;
        sampleEntity = findSample(sampleCreation.getSampleId(), sampleEntity);
        String sampleId = generateSampleId();
        if (!Objects.isNull(sampleCreation.getPatientPhone())) {
            PatientEntity patientEntity = patientRepository.findByEmail(sampleCreation.getPatientEmail());
            if (!Objects.isNull(patientEntity)) {
                patientEntity = updatePatientEntity(patientEntity, sampleCreation);
                //sampleId = genrateSampleId(setSampleEntityData(sampleCreation, patientEntity, sampleEntity));
                 setSampleEntityData(sampleCreation, patientEntity, sampleEntity);
            } else {
                patientRepository.save(setPatientEntityData(sampleCreation));
                patientEntity = patientRepository.findByPhone(sampleCreation.getPatientPhone());
               // sampleId = genrateSampleId(setSampleEntityData(sampleCreation, patientEntity, sampleEntity));
                setSampleEntityData(sampleCreation, patientEntity, sampleEntity);
            }
        }
        return "Generated Sample Id : " + sampleId;
    }

    @Override
    public SampleResponseBean getBySampleId(String sampleGenerateId) {
        SampleResponseBean sample = new SampleResponseBean();
        MicroBiomesEntity mb = new MicroBiomesEntity();
        OralEcologixReportEntity oe = new OralEcologixReportEntity();


        SampleEntity sampleEntity = sampleRepository.findBySampleId(sampleGenerateId);
        if(!Objects.isNull(sampleEntity)){
            sample.setSampleData(sampleEntity);

            if(sampleEntity.getTestOrdered().equals(Constants.femaleEcolife)){
                mb = microBiomesRepository.findBySampleId(sampleGenerateId);
                if(!Objects.isNull(mb)) {
                    sample.setReportId(mb.getReportId());
                }
            }else if(sampleEntity.getTestOrdered().equals(Constants.oralEcolife)){
                oe = oralEcologixRepository.findBySampleId(sampleGenerateId);
                if(!Objects.isNull(oe)) {
                    sample.setReportId(oe.getReportId());
                }
            }
        }else{
            throw new NotFoundException("No data found for given sampleId");
        }

        return sample;
    }

    @Override
    public List<SampleEntity> findAll() {
        return sampleRepository.findAll();
    }

    public SampleEntity updateSampleEntity(SampleCreation sampleCreation, PatientEntity patientEntity, SampleEntity sampleEntity) {
        if (!Objects.isNull(sampleCreation.getSubmittedSampleName())) {
            sampleEntity.setSubmittedSampleName(sampleCreation.getSubmittedSampleName());  
        }
        if (!Objects.isNull(sampleCreation.getRecordType())) {
            sampleEntity.setRecordType(sampleCreation.getRecordType());
        }
        if (!Objects.isNull(sampleCreation.getTestOrdered())) {
            sampleEntity.setTestOrdered(sampleCreation.getTestOrdered());
        }
        if (!Objects.isNull(sampleCreation.getTestReceiveDate())) {
            sampleEntity.setTestReceiveDate(sampleCreation.getTestReceiveDate());
        }
        if (!Objects.isNull(sampleCreation.getTestReportDate())) {
            sampleEntity.setTestReportDate(sampleCreation.getTestReportDate());
        }
        if (!Objects.isNull(sampleCreation.getSampleType())) {
            sampleEntity.setSampleType(sampleCreation.getSampleType());
        }
        if (!Objects.isNull(sampleCreation.getSampleCollectionDate())) {
            sampleEntity.setSampleCollectionDate(sampleCreation.getSampleCollectionDate());
        }
        if (!Objects.isNull(sampleCreation.getPH())) {
            sampleEntity.setPH(sampleCreation.getPH());
        }
        if (!Objects.isNull(sampleCreation.getExtractionType())) {
            sampleEntity.setExtractionType(sampleCreation.getExtractionType());
        }
        if (!Objects.isNull(sampleCreation.getPlates())) {
            sampleEntity.setPlates(sampleCreation.getPlates());
        }
        if (!Objects.isNull(sampleCreation.getQPcrComplete())) {
            sampleEntity.setQPcrComplete(sampleCreation.getQPcrComplete());
        }
        if (!Objects.isNull(sampleCreation.getElisaComplete())) {
            sampleEntity.setElisaComplete(sampleCreation.getElisaComplete());
        }
        if (!Objects.isNull(sampleCreation.getReportApproved())) {
            sampleEntity.setReportApproved(sampleCreation.getReportApproved());
        }
       /* if (!Objects.isNull(sampleCreation.getSampleStatus())) {
            sampleEntity.setSampleStatus(sampleCreation.getSampleStatus());
        } */

        if (!Objects.isNull(sampleCreation.getRecentViewDate())) {
            sampleEntity.setRecentViewDate(sampleCreation.getRecentViewDate());
        }

        sampleEntity.setRecentViewDate(new Date());
        if (!Objects.isNull(sampleCreation.getUserId())) {
            sampleEntity.setUserId(sampleCreation.getUserId());
        }

        //report table update
        if(sampleCreation.getSampleGenerateId() != null) {
            MicroBiomesEntity mb = microBiomesRepository.findBySampleId(sampleCreation.getSampleGenerateId());
            if(!Objects.isNull(mb)){
                /*if (!Objects.isNull(sampleCreation.getSampleStatus())) {
                    mb.setReportStatus(sampleCreation.getSampleStatus());
                } */
                if (!Objects.isNull(sampleCreation.getTestOrdered())) {
                    mb.setReportType(sampleCreation.getTestOrdered());
                }
                if (!Objects.isNull(sampleCreation.getTestReceiveDate())) {
                    mb.setTestReceived(sampleCreation.getTestReceiveDate());
                }
                if (!Objects.isNull(sampleCreation.getTestReportDate())) {
                    mb.setTestReported(sampleCreation.getTestReportDate());
                }
                if (!Objects.isNull(sampleCreation.getPH())) {
                    mb.setVaginalPh(sampleCreation.getPH());
                }

                microBiomesRepository.save(mb);
            }
        }

        return sampleRepository.save(sampleEntity);
    }

    public SampleEntity setSampleEntityData(SampleCreation sampleCreation, PatientEntity patientEntity, SampleEntity sampleEntity1) {
    	SampleEntity sampleEntity = new SampleEntity();
        sampleEntity.setSampleGenerateId(generateSampleId());
        sampleEntity.setSubmittedSampleName(sampleCreation.getSubmittedSampleName());
        sampleEntity.setRecordType(sampleCreation.getRecordType());
        sampleEntity.setTestOrdered(sampleCreation.getTestOrdered());
        sampleEntity.setTestReceiveDate(sampleCreation.getTestReceiveDate());
        sampleEntity.setTestReportDate(sampleCreation.getTestReportDate());
        sampleEntity.setSampleType(sampleCreation.getSampleType());
        sampleEntity.setSampleCollectionDate(sampleCreation.getSampleCollectionDate());
        sampleEntity.setPH(sampleCreation.getPH());
        sampleEntity.setExtractionType(sampleCreation.getExtractionType());
        sampleEntity.setPlates(sampleCreation.getPlates());
        sampleEntity.setSampleChose(sampleCreation.getSampleChose());
        sampleEntity.setQPcrComplete(sampleCreation.getQPcrComplete());
        sampleEntity.setElisaComplete(sampleCreation.getElisaComplete());
        sampleEntity.setReportApproved(sampleCreation.getReportApproved());
        sampleEntity.setPatientId(patientEntity);
        sampleEntity.setCreatedBy(sampleCreation.getCreatedBy());
        //sampleEntity.setSampleStatus(sampleCreation.getSampleStatus());
        Date date = new Date();
        sampleEntity.setCreatedOn(date);
        return sampleRepository.save(sampleEntity);
    }

    public PatientEntity setPatientEntityData(SampleCreation sampleCreation) {
        PatientEntity patientEntity = new PatientEntity();
        patientEntity.setPatientFirstName(sampleCreation.getPatientFirstName());
        patientEntity.setPatientLastName(sampleCreation.getPatientLastName());
        patientEntity.setPatientAddress(sampleCreation.getPatientAddress());
        patientEntity.setPatientPhone(sampleCreation.getPatientPhone());
        patientEntity.setPatientEmail(sampleCreation.getPatientEmail());
        patientEntity.setPatientDOB(sampleCreation.getPatientDOB());
        patientEntity.setPatientSex(sampleCreation.getPatientSex());
        patientEntity.setClinicianName(sampleCreation.getClinicianName());
        return patientEntity;
    }

    public PatientEntity updatePatientEntity(PatientEntity patientEntity, SampleCreation sampleCreation) {
        patientEntity.setPatientFirstName(sampleCreation.getPatientFirstName());
        patientEntity.setPatientLastName(sampleCreation.getPatientLastName());
        patientEntity.setPatientAddress(sampleCreation.getPatientAddress());
        patientEntity.setPatientPhone(sampleCreation.getPatientPhone());
        patientEntity.setPatientDOB(sampleCreation.getPatientDOB());
        patientEntity.setPatientSex(sampleCreation.getPatientSex());
        patientEntity.setClinicianName(sampleCreation.getClinicianName());
        return patientEntity;
    }

    public String genrateSampleId(SampleEntity sampleEntity) {
        String sampleId = "S" + String.format("%06d", sampleEntity.getSampleId());
        sampleEntity.setSampleGenerateId(sampleId);
        sampleRepository.save(sampleEntity);
        return sampleId;
    }

    private SampleEntity findSample(long id, SampleEntity sampleEntity) {
        if (!Objects.isNull(id)) {
            sampleEntity = sampleRepository.findBySampleId(id);
        }
        return sampleEntity;
    }

    @Override
    public Page<SampleEntity> findByStages(String stage, int page, String sortField, String sortDirection, int recordPerPage) {
        String stageFirst = "qPcrComplete";
        String stageSecond = "elisaComplete";
        String stageThird = "reportApproved";
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        PageRequest pageRequest = PageRequest.of(page - 1, recordPerPage, sort);
        if (stage.equalsIgnoreCase(stageFirst)) {
            return sampleRepository.findByStagesFirst(pageRequest);
        } else if (stage.equalsIgnoreCase(stageSecond)) {
            return sampleRepository.findByStagesSecond(pageRequest);
        } else if (stage.equalsIgnoreCase(stageThird)) {
            return sampleRepository.findByStagesThird(pageRequest);
        } else {
            return sampleRepository.findAll(pageRequest);
        }
    }

    @Override
    public Page<SampleEntity> getSampleByPagination(int page, String sortField, String sortDirection, int recordPerPage) {
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        PageRequest pageRequest = PageRequest.of(page - 1, recordPerPage, sort);
        return sampleRepository.findAll(pageRequest);
    }

    @Override
    public SampleEntity updateSample(SampleCreation sampleCreation) {
        SampleEntity sampleEntity = null;
        sampleEntity = findSample(sampleCreation.getSampleId(), sampleEntity);
        if(!Objects.isNull(sampleEntity)){
            PatientEntity patientEntity = sampleEntity.getPatientId();
            if (!Objects.isNull(patientEntity)) {
                sampleEntity = updateSampleEntity(sampleCreation, patientEntity, sampleEntity);
            }
        }
        else{
            return null;
        }
        return sampleEntity;
    }

	@Override
	//public List<SampleEntity> findAllRecentViewers(int userId) {
	public Page<SampleEntity> findAllRecentViewers(int userId, int page, String sortField1, String sortDirection, int recordPerPage) {
		List<SampleEntity> se = new ArrayList<SampleEntity>();
		String sortField = PatientEnum.valueOf(sortField1).label;
		Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        PageRequest pageRequest = PageRequest.of(page - 1, recordPerPage, sort);
		return sampleRepository.findAllRecentViewers(userId, pageRequest);
	}

	@Override
	public String updateSampleByPlate(SampleByPlateUpdate sampleByPlateUpdate) {

		List<SampleUpdate> sampleUpdate = new ArrayList<SampleUpdate>();
		sampleUpdate = sampleByPlateUpdate.getSampleUpdate();
		String PlateId = sampleByPlateUpdate.getPlateId();
		int size = sampleUpdate.size();
		String res = "";
		if (size > 0) {
			for (int i = 0; i < size; i++) {
               String sampleGeneratedId = sampleUpdate.get(i).getSampleGenerateId();
               SampleEntity sampleEntity = sampleRepository.findBySampleId(sampleGeneratedId);
               if(!Objects.isNull(sampleEntity)) {
                    sampleRepository.updateSampleByPlate(PlateId, sampleGeneratedId);
               }
			}
			res = "Sample Data Updated with plateid Successfully"; 
		}else {
			res = "SampleId information not found to update the samples";
		}
		return res;
	}

	@Override
	public Page<SampleEntity> findSampleSearch(String searchName, int page, String sortField, String sortDirection,
			int recordPerPage) {
		//String sortField = PatientEnum.valueOf(sortField1).label;
		Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        PageRequest pageRequest = PageRequest.of(page - 1, recordPerPage, sort);
        
		return sampleRepository.findSampleSearch(searchName.toLowerCase(), pageRequest);
	}

    @Override
    public String generateSampleId() {
        int totalSize = sampleRepository.findNextSeq();
        String samplegeneratedId ="";
        if(totalSize > 0){
            samplegeneratedId = "S" + String.format("%06d", totalSize + 1);
        }
        return samplegeneratedId;
    }

    @Override
    public Page<SampleEntity> getSamplesByPlateId(int page, String plateId, String sortField, String sortDirection, int recordPerPage) {

        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        PageRequest pageRequest = PageRequest.of(page - 1, recordPerPage, sort);

        return sampleRepository.getAllSamplesByPlateId(plateId, pageRequest);
    }
}
