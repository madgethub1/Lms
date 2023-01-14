package io.albot.lims.report.repos.postgres;

import io.albot.lims.report.model.dto.UserDemoGraphicsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserDemoGraphicsRepository extends JpaRepository<UserDemoGraphicsEntity, Long> {

    @Query("SELECT t FROM UserDemoGraphicsEntity t WHERE t.phoneNumber = ?1 ")
    UserDemoGraphicsEntity findByPhoneNumber(String phoneNumber);


    UserDemoGraphicsEntity save(UserDemoGraphicsEntity userDemoGraphicsEntity);
}