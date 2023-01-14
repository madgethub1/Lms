package io.albot.lims.report.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.util.Date;

@Data
@NoArgsConstructor
@EqualsAndHashCode
@JsonSerialize
@JsonDeserialize
@Entity
@TypeDef(name = "HelpEntity", typeClass = HelpEntity.class)
@Table(name = "help_info")
public class HelpEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "help_id", columnDefinition = "SERIAL", nullable = false, updatable = false)
    private long helpId;
    @Column(name = "title")
    private String title;
    @Column(name = "description")
    private String description;
    @Column(name = "attachment_name")
    private String attachmentName;
    @Column(name = "severity_level")
    private String severityLevel;
    @Column(name = "created_date")
    private Date createdDate;
}
