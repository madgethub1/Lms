package io.albot.lims.report.repos.postgres;

import io.albot.lims.report.model.dto.HelpEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HelpRepository extends JpaRepository<HelpEntity, Long> {

}
