package io.albot.lims.report.repos.postgres;

import io.albot.lims.report.model.dto.SampleEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


public interface SampleRepository extends JpaRepository<SampleEntity, Long> {

    @Override
    List<SampleEntity> findAll();

    @Override
    SampleEntity save(SampleEntity sampleEntity);

    @Query("SELECT t FROM SampleEntity t WHERE t.sampleGenerateId = :sampleGenerateId")
    SampleEntity findBySampleId(@Param("sampleGenerateId") String sampleGenerateId);

    @Query("SELECT t FROM SampleEntity t WHERE t.qPcrComplete = 'false' ")
    Page<SampleEntity> findByStagesFirst(Pageable pageable);

    @Query("SELECT t FROM SampleEntity t WHERE t.elisaComplete = 'false' ")
    Page<SampleEntity> findByStagesSecond(Pageable pageable);

    @Query("SELECT t FROM SampleEntity t WHERE t.reportApproved = 'false' ")
    Page<SampleEntity> findByStagesThird(Pageable pageable);

    @Override
    Page<SampleEntity> findAll(Pageable pageable);

    SampleEntity findBySampleId(long id);
    
    @Query(value="SELECT * from sample_info t where t.user_id = ?1 and t.recent_view_date > (now() - interval '7 day')", nativeQuery=true)
	//List<SampleEntity> findAllRecentViewers(int userId);
    Page<SampleEntity> findAllRecentViewers(int userId, Pageable pageable);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(value= "UPDATE sample_info SET plate_id = ?1 WHERE sample_genrate_id = ?2 ", nativeQuery=true)
	void updateSampleByPlate(String plateId, String sampleGeneratedId);

    @Query(value= "SELECT t FROM SampleEntity t WHERE lower(t.submittedSampleName) LIKE CONCAT('%',:searchName,'%') OR lower(t.sampleType) LIKE CONCAT('%',:searchName,'%') OR lower(t.sampleGenerateId) LIKE CONCAT('%',:searchName,'%') OR lower(t.extractionType) LIKE CONCAT('%',:searchName,'%')")
    Page<SampleEntity> findSampleSearch(@Param("searchName") String searchName, Pageable pageable);


    @Query(value="SELECT MAX(sample_id) from public.sample_info", nativeQuery=true)
    int findNextSeq();

    @Query("SELECT t FROM SampleEntity t WHERE t.plates = :plateId")
    Page<SampleEntity> getAllSamplesByPlateId(@Param("plateId") String plateId, Pageable pageable);
}