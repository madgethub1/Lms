package io.albot.lims.report.service.impl;

import io.albot.lims.report.exceptions.PlateNotFoundException;
import io.albot.lims.report.model.dto.PlatesEntity;
import io.albot.lims.report.model.dto.ProtocolEntity;
import io.albot.lims.report.model.web.PlatesCreation;
import io.albot.lims.report.model.web.PlatesEnum;
import io.albot.lims.report.repos.postgres.PlatesRepository;
import io.albot.lims.report.service.PlatesService;
import lombok.extern.slf4j.Slf4j;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class PlatesServiceImpl implements PlatesService {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private PlatesRepository platesRepository;

    @Override
    public String savePlates(PlatesCreation platesCreation) {
        
        boolean platename = UniquePlateName(platesCreation.getPlatesName());

        if(platename == true) {
        	throw new PlateNotFoundException("Plate name should be unique");
        }

        String plateGen = generateBatchId();
        String returnValue = "";

		try {
            PlatesEntity platesEntity = new PlatesEntity();
			//platesEntity = convertToAppointmentEntity(platesCreation);
            platesEntity.setPlatesName(platesCreation.getPlatesName());
            platesEntity.setPlatesStatus(platesCreation.getPlatesStatus());
            platesEntity.setRecordType(platesCreation.getRecordType());
            platesEntity.setUserId(platesCreation.getUserId());
            /*
			ProtocolEntity protocolEntity = new ProtocolEntity();
			protocolEntity.setProtocolId(platesCreation.getPlatesCurrentProtocol());
			platesEntity.setPlatesCurrentProtocol(protocolEntity);
            */
            platesEntity.setPlatesGeneratedId(plateGen);
            Date curdate = new Date();
            platesEntity.setRecentViewDate(curdate);
            platesEntity.setCreatedDate(curdate);

            platesEntity.setProtocols(platesCreation.getProtocols());

			platesEntity = platesRepository.save(platesEntity);
            returnValue = "Plates Created With Plate_ID : " + plateGen;

		} catch (Exception ex) {
           log.debug(ex.toString());
           throw new PlateNotFoundException("Bad Request Body");
		}
        return returnValue; //+ genratePlatesId(platesEntity);
    }

    private boolean UniquePlateName(String platesName) {
    	  PlatesEntity platesEntity = new PlatesEntity();
    	  platesEntity = platesRepository.findByPlateName(platesName); 
    	  if(Objects.isNull(platesEntity)) {
    		  return false;
    	  }
		return true;
	}


    private boolean UniquePlateGenerateId(String platesID) {
        PlatesEntity platesEntity = new PlatesEntity();
        platesEntity = platesRepository.findByPlatesId(platesID);
        if(Objects.isNull(platesEntity)) {
            return false;
        }
        return true;
    }

	@Override
    public List<PlatesEntity> findAll() {
        return platesRepository.findAll();
    }

    @Override
    public PlatesEntity getPlatesById(String uniqueId) {
        return platesRepository.findByPlatesId(uniqueId);
    }

    @Override
	public List<PlatesEntity> getPlatesByName(String name) {
		List<PlatesEntity> platesEntity = new ArrayList<PlatesEntity>();
		if (name.equals("*")) {
			platesEntity = platesRepository.getPlatesByName("");
		} else {
			platesEntity = platesRepository.getPlatesByName(name.toLowerCase());
		}
		if (Objects.isNull(platesEntity) || platesEntity.isEmpty()) {
			log.error("Plates information data not found in database based on condition : Plate Name = {} " + name);
			throw new PlateNotFoundException(
					String.format("Given plate name not found in Plates information with value  %d", name));
		}

		return platesEntity;
	}

    @Override
    public Page<PlatesEntity> getPlatesByPagination(int page, String sortField, String sortDirection, int recordPerPage) {
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        PageRequest pageRequest = PageRequest.of(page - 1, recordPerPage, sort);
        return platesRepository.findAll(pageRequest);
    }

    @Override
    public PlatesEntity updatePlates(PlatesCreation platesCreation) {
        String platesgd = platesCreation.getPlatesGeneratedId();
        PlatesEntity platesEntity = platesRepository.findByPlatesId(platesgd);
        if (!Objects.isNull(platesEntity)) {
            if (!Objects.isNull(platesCreation.getPlatesName())) {

                PlatesEntity pe = new PlatesEntity();
                pe = platesRepository.findByPlateName(platesCreation.getPlatesName());
                if(!Objects.isNull(pe)) {
                    if(platesEntity.getPlatesId() != pe.getPlatesId()) {
                        throw new PlateNotFoundException("Plate name should be unique");
                    }
                }

                platesEntity.setPlatesName(platesCreation.getPlatesName());
            }
            if (!Objects.isNull(platesCreation.getRecordType())) {
                platesEntity.setRecordType(platesCreation.getRecordType());
            }
            if (!Objects.isNull(platesCreation.getPlatesStatus())) {
                platesEntity.setPlatesStatus(platesCreation.getPlatesStatus());
            }
            /*
            if (!Objects.isNull(platesCreation.getRecentViewDate())) {
                platesEntity.setRecentViewDate(platesCreation.getRecentViewDate());
            }*/
            platesEntity.setRecentViewDate(new Date());

            if (!Objects.isNull(platesCreation.getUserId())) {
                platesEntity.setUserId(platesCreation.getUserId());
            }
            /*
            if (!Objects.isNull(platesCreation.getPlatesCurrentProtocol())) {
                ProtocolEntity protocolEntity=new ProtocolEntity();
                protocolEntity.setProtocolId(platesCreation.getPlatesCurrentProtocol());
                platesEntity.setPlatesCurrentProtocol(protocolEntity);
            }
            */
            if (!Objects.isNull(platesCreation.getProtocols())) {
                platesEntity.setProtocols(platesCreation.getProtocols());
            }
        }else{
            throw new PlateNotFoundException("PlateId not found");
        }
        return platesRepository.save(platesEntity);
    }

    private PlatesEntity convertToAppointmentEntity(PlatesCreation platesCreation) {
        return modelMapper.map(platesCreation, PlatesEntity.class);
    }

    private String genratePlatesId(PlatesEntity platesEntity) {
        String platesId = "PT" + String.format("%06d", platesEntity.getPlatesId());
        platesEntity.setPlatesGeneratedId(platesId);
        platesRepository.save(platesEntity);
        return platesId;
    }

	@Override
	public Page<PlatesEntity> findAllRecentViewers(int userId, int page, String sortField1, String sortDirection,
			int recordPerPage) {
		String sortField = PlatesEnum.valueOf(sortField1).label;
		Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        PageRequest pageRequest = PageRequest.of(page - 1, recordPerPage, sort);
		return platesRepository.findAllRecentViewers(userId, pageRequest);
	}

    @Override
    public String generateBatchId() {
        int totalSize = platesRepository.findNextSeq();
        String batchgeneratedId ="";
        if(totalSize > 0){
            batchgeneratedId = "PT" + String.format("%06d", totalSize + 1);
        }
        return batchgeneratedId;
    }


}
