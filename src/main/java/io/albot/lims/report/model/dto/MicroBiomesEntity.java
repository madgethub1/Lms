package io.albot.lims.report.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.util.Date;

@Data
@NoArgsConstructor
@EqualsAndHashCode
@JsonSerialize
@JsonDeserialize
@Entity
@TypeDef(name = "MicroBiomesEntity", typeClass = MicroBiomesEntity.class)
@Table(name = "report_info")
public class MicroBiomesEntity {
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
    @Column(name = "Vaginal_PH")
    private String vaginalPh;
    @Column(name = "Vaginal_Health_Markers")
    private String vaginalHealthMarkers;
    @Column(name = "Lactobacillus_Crispatus")
    private String lactobacillusCrispatus;
    @Column(name = "Lactobacillus_Gasseri")
    private String lactobacillusGasseri;
    @Column(name = "Lactobacillus_Iners")
    private String lactobacillusIners;
    @Column(name = "Lactobacillus_Jensenii")
    private String lactobacillusJensenii;
    @Column(name = "Atopobium_Vaginae")
    private String atopobiumVaginae;
    @Column(name = "BVAB_2")
    private String bVAB2;
    @Column(name = "Gardnerella_Vaginalis")
    private String gardnerellaVaginalis;
    @Column(name = "Megasphera_1")
    private String megasphera1;
    @Column(name = "Megasphera_2")
    private String megasphera2;
    @Column(name = "Mobiluncus_Curtisii")
    private String mobiluncusCurtisii;
    @Column(name = "Mobiluncus_Mulieris")
    private String mobiluncusMulieris;
    @Column(name = "Prevotella_Bivia")
    private String prevotellaBivia;
    @Column(name = "Ureaplasma_Urealyticum")
    private String ureaplasmaUrealyticum;
    @Column(name = "Enterococcus_Faecalis")
    private String enterococcusFaecalis;
    @Column(name = "Escherichia_Coli")
    private String escherichiaColi;
    @Column(name = "Staphylococcus_Aureus")
    private String staphylococcusAureus;
    @Column(name = "Streptococcus_Agalactiae")
    private String streptococcusAgalactiae;
    @Column(name = "Candida_Albicans")
    private String candidaAlbicans;
    @Column(name = "Candida_Glabrata")
    private String candidaGlabrata;
    @Column(name = "Candida_Krusei")
    private String candidaKrusei;
    @Column(name = "Candida_Parapsilosis")
    private String candidaParapsilosis;
    @Column(name = "Candida_Tropicalis")
    private String candidaTropicalis;
    @Column(name = "Report_Type")
    private String reportType;

    @Column(name = "VaginalHealthMarkers_Rating")
    private String vaginalHealthMarkersRating;
    @Column(name = "LactobacillusCrispatus_Ambudance")
    private String lactobacillusCrispatusAmbudance;
    @Column(name = "LactobacillusGasseri_Ambudance")
    private String lactobacillusGasseriAmbudance;
    @Column(name = "LactobacillusIners_Ambudance")
    private String lactobacillusInersAmbudance;
    @Column(name = "LactobacillusJensenii_Ambudance")
    private String lactobacillusJenseniiAmbudance;
    @Column(name = "AtopobiumVaginae_Ambudance")
    private String atopobiumVaginaeAmbudance;
    @Column(name = "BVAB2_Ambudance")
    private String bVAB2Ambudance;
    @Column(name = "GardnerellaVaginalis_Ambudance")
    private String gardnerellaVaginalisAmbudance;
    @Column(name = "Megasphera1_Ambudance")
    private String megasphera1Ambudance;
    @Column(name = "Megasphera2_Ambudance")
    private String megasphera2Ambudance;
    @Column(name = "MobiluncusCurtisii_Ambudance")
    private String mobiluncusCurtisiiAmbudance;
    @Column(name = "MobiluncusMulieris_Ambudance")
    private String mobiluncusMulierisAmbudance;
    @Column(name = "PrevotellaBivia_Ambudance")
    private String prevotellaBiviaAmbudance;
    @Column(name = "UreaplasmaUrealyticum_Ambudance")
    private String ureaplasmaUrealyticumAmbudance;
    @Column(name = "EnterococcusFaecalis_Ambudance")
    private String enterococcusFaecalisAmbudance;
    @Column(name = "EscherichiaColi_Ambudance")
    private String escherichiaColiAmbudance;
    @Column(name = "StaphylococcusAureus_Ambudance")
    private String staphylococcusAureusAmbudance;
    @Column(name = "StreptococcusAgalactiae_Ambudance")
    private String streptococcusAgalactiaeAmbudance;
    @Column(name = "CandidaAlbicans_Ambudance")
    private String candidaAlbicansAmbudance;
    @Column(name = "CandidaGlabrata_Ambudance")
    private String candidaGlabrataAmbudance;
    @Column(name = "CandidaKrusei_Ambudance")
    private String candidaKruseiAmbudance;
    @Column(name = "CandidaParapsilosis_Ambudance")
    private String candidaParapsilosisAmbudance;
    @Column(name = "CandidaTropicalis_Ambudance")
    private String candidaTropicalisAmbudance;
    @Column(name = "megasphaeraSpp")
    private String megasphaeraSpp;
    @Column(name = "veillonellaSpp")
    private String veillonellaSpp;
    @Column(name = "peptostreptococcusAnaerobius")
    private String peptostreptococcusAnaerobius;
    @Column(name = "file_name")
    private String fileName;
    @Column(name = "patient_name")
    private String patientName;
    @Column(name = "email_id")
    private String emailid;

    @Column(name = "gender")
    private String gender;
    @Column(name = "patient_dob")
    private Date patientDob;
    @Column(name = "sampleid")
    private String sampleid;

    /*
    @OneToOne
    @JoinColumn(name = "patientId", nullable = false, referencedColumnName = "user_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private UserDemoGraphicsEntity phoneNumber;
    */
}
