package io.albot.lims.report.repos.postgres;

import io.albot.lims.report.model.dto.ProtocolEntity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProtocolRepository extends JpaRepository<ProtocolEntity, Long> {

    ProtocolEntity findByProtocolId(Long id);

    ProtocolEntity save(ProtocolEntity protocolEntity);

    @Override
    List<ProtocolEntity> findAll();

    @Query("SELECT t FROM ProtocolEntity t WHERE t.protocolName like %:protocolName%")
    List<ProtocolEntity> getProtocolByName(String protocolName);

    @Override
    Page<ProtocolEntity> findAll(Pageable pageable);
}
