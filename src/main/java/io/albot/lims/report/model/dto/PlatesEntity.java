package io.albot.lims.report.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import io.albot.lims.report.model.web.ProtocolIdBean;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode
@JsonSerialize
@JsonDeserialize
@Entity
@TypeDefs({@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)})
@TypeDef(name = "PlatesEntity", typeClass = PlatesEntity.class)
@Table(name = "plates_info")
public class PlatesEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "plates_id", columnDefinition = "SERIAL", nullable = false, updatable = false)
    private long platesId;
    @Column(name = "plates_name")
    private String platesName;
    @Column(name = "plates_record_type")
    private String recordType;
    @Column(name = "plates_generated_id")
    private String platesGeneratedId;
    @Column(name = "plates_status")
    private String platesStatus;
    @Column(name = "createdDate")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Kolkata")
    private Date createdDate;
    @Column(name = "recent_view_date")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Kolkata")
    private Date recentViewDate;
    @Column(name = "user_id")
    private Integer userId;

    /*
    @OneToOne
    @JoinColumn(name = "protocolId", nullable = false, referencedColumnName = "PROTOCOL_ID")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private ProtocolEntity platesCurrentProtocol;
    */

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    private List<ProtocolIdBean> protocols;
}
