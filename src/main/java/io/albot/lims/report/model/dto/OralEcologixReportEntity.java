package io.albot.lims.report.model.dto;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.TypeDef;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode
@JsonSerialize
@JsonDeserialize
@Entity
@TypeDef(name = "OralEcologixReportEntity", typeClass = OralEcologixReportEntity.class)
@Table(name = "oral_ecologix_report_info")
public class OralEcologixReportEntity {
	
	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    @Column(name = "report_id", columnDefinition = "SERIAL", nullable = false, updatable = false)
	    private long reportId;
	    @Column(name = "test_reported")
	    private Date testReported;
	    @Column(name = "test_received")
	    private Date testReceived;
	    @Column(name = "sample_type")
	    private String sampleType;
	    @Column(name = "report_status")
	    private String reportStatus;
	    @Column(name = "clinician_name")
	    private String clinicianName;
	    @Column(name = "accessionNo")
	    private String accessionNo;
	    @Column(name = "Report_Type")
	    private String reportType;
	    @Column(name = "file_name")
	    private String fileName;
	    @Column(name = "patient_name")
	    private String patientName;
	    @Column(name = "email_id")
	    private String emailid;
	    @Column(name = "patient_dob")
	    private Date patientdob;
	    @Column(name = "gender")
	    private String gender;
	    	      
	    @Column(name = "aggregatibacter_actinomycetemcomitans")
	    private String aggregatibacterActinomycetemcomitans;
	    @Column(name = "porphyromonas_gingivalis")
	    private String porphyromonasGingivalis;
	    @Column(name = "tannerella_forsythia")
	    private String tannerellaForsythia;
	    @Column(name = "treponema_denticola")
	    private String treponemaDenticola;
	    @Column(name = "candida_albicans")
	    private String candidaAlbicans;
	    @Column(name = "campylobacter_rectus")
	    private String campylobacterRectus;
	    @Column(name = "eubacterium_nodatum")
	    private String eubacteriumNodatum;
	    @Column(name = "fusobacterium_nucleatum")
	    private String fusobacteriumNucleatum;
	    @Column(name = "lactobacillus_spp")
	    private String lactobacillusSpp;
	    @Column(name = "parvimonas_micra")
	    private String parvimonasMicra;
	    @Column(name = "peptostreptococcus_anaerobius")
	    private String peptostreptococcusAnaerobius;
	    @Column(name = "prevotella_intermedia")
	    private String prevotellaIntermedia;
	    @Column(name = "prevotella_nigrescens")
	    private String prevotellaNigrescens;
	    @Column(name = "streptococcus_mutans")
	    private String streptococcusMutans;
	    @Column(name = "enterococcusFaecalis")
	    private String enterococcusFaecalis;

		@Column(name = "sampleid")
		private String sampleid;

}
