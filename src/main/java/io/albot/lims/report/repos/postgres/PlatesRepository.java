package io.albot.lims.report.repos.postgres;

import io.albot.lims.report.model.dto.PlatesEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PlatesRepository extends JpaRepository<PlatesEntity, Long> {
    @Override
    PlatesEntity save(PlatesEntity platesEntity);

    @Override
    List<PlatesEntity> findAll();

    @Query("SELECT t FROM PlatesEntity t WHERE t.platesGeneratedId = :platesGeneratedId")
    PlatesEntity findByPlatesId(@Param("platesGeneratedId") String platesGeneratedId);

    @Query("SELECT t FROM PlatesEntity t WHERE lower(t.platesName) LIKE %:platesName%")
    List<PlatesEntity> getPlatesByName(@Param("platesName") String platesName);

    @Override
    Page<PlatesEntity> findAll(Pageable pageable);

    PlatesEntity findByPlatesId(Long aLong);

    @Query(value="SELECT * from plates_info t where t.user_id = ?1 and t.recent_view_date > (now() - interval '7 day')", nativeQuery=true)
	Page<PlatesEntity> findAllRecentViewers(int userId, PageRequest pageRequest);

    @Query("SELECT t FROM PlatesEntity t WHERE t.platesName = :platesName")
	PlatesEntity findByPlateName(@Param("platesName") String platesName);

    @Query(value="SELECT MAX(plates_id) from public.plates_info", nativeQuery=true)
    int findNextSeq();

}
