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
@TypeDef(name = "ProtocolEntity", typeClass = ProtocolEntity.class)
@Table(name = "protocol_basic_info")
public class ProtocolEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PROTOCOL_ID", columnDefinition = "SERIAL", nullable = false, updatable = false)
    private long protocolId;
    @Column(name = "PROTOCOL_NAME")
    private String protocolName;
    @Column(name = "PROTOCOL_PDF")
    private String protocolPDF;
    @Column(name = "IS_ACTIVE")
    private String isActive;
    @Column(name = "CREATED_ON")
    private Date createdOn;
    @Column(name = "CREATED_BY")
    private String createdBy;
}
