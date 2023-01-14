package io.albot.lims.report.model.web;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@JsonSerialize
@JsonDeserialize
@JsonPropertyOrder
public class MicroBiomesCreation {
    @ApiModelProperty(value = "Field is used for holding id data as a Long type.", required = true)
    private long reportId;
    @ApiModelProperty(value = "Field is used for holding testReported data as a Long type.", required = true)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Kolkata")
    private Date testReported;
    @ApiModelProperty(value = "Field is used for holding testReceived data as a Long type.", required = true)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Kolkata")
    private Date testReceived;
    @ApiModelProperty(value = "Field is used for holding sampleTypee data as a String type.")
    private String sampleType;
    @ApiModelProperty(value = "Field is used for holding reportStatus data as a String type.")
    private String reportStatus;
    @ApiModelProperty(value = "Field is used for holding clinicianName data as a String type.")
    private String clinicianName;
    @ApiModelProperty(value = "Field is used for holding accessionNo Data as a String type.")
    private String accessionNo;
    @ApiModelProperty(value = "Field is used for holding vaginalPh data as a String type.")
    private String vaginalPh;
    @ApiModelProperty(value = "Field is used for holding vaginalHealthMarkers data as a String type.")
    private String vaginalHealthMarkers;
    @ApiModelProperty(value = "Field is used for holding lactobacillusCrispatus data as a String type.")
    private String lactobacillusCrispatus;
    @ApiModelProperty(value = "Field is used for holding lactobacillusGasseri data as a String type.")
    private String lactobacillusGasseri;
    @ApiModelProperty(value = "Field is used for holding lactobacillusIners data as a String type.")
    private String lactobacillusIners;
    @ApiModelProperty(value = "Field is used for holding lactobacillusJensenii data as a String type.")
    private String lactobacillusJensenii;
    @ApiModelProperty(value = "Field is used for holding atopobiumVaginae data as a String type.")
    private String atopobiumVaginae;
    @ApiModelProperty(value = "Field is used for holding bVAB2 data as a String type.")
    private String bVAB2;
    @ApiModelProperty(value = "Field is used for holding gardnerellaVaginalis data as a String type.")
    private String gardnerellaVaginalis;
    @ApiModelProperty(value = "Field is used for holding megasphera1 data as a String type.")
    private String megasphera1;
    @ApiModelProperty(value = "Field is used for holding megasphera2 data as a String type.")
    private String megasphera2;
    @ApiModelProperty(value = "Field is used for holding mobiluncusCurtisii data as a String type.")
    private String mobiluncusCurtisii;
    @ApiModelProperty(value = "Field is used for holding mobiluncusMulieris data as a String type.")
    private String mobiluncusMulieris;
    @ApiModelProperty(value = "Field is used for holding prevotellaBivia data as a String type.")
    private String prevotellaBivia;
    @ApiModelProperty(value = "Field is used for holding ureaplasmaUrealyticum data as a String type.")
    private String ureaplasmaUrealyticum;
    @ApiModelProperty(value = "Field is used for holding enterococcusFaecalis data as a String type.")
    private String enterococcusFaecalis;
    @ApiModelProperty(value = "Field is used for holding fileName data as a String type.")
    private String escherichiaColi;
    @ApiModelProperty(value = "Field is used for holding escherichiaColi data as a String type.")
    private String staphylococcusAureus;
    @ApiModelProperty(value = "Field is used for holding streptococcusAgalactiae data as a String type.")
    private String streptococcusAgalactiae;
    @ApiModelProperty(value = "Field is used for holding candidaAlbicans data as a String type.")
    private String candidaAlbicans;
    @ApiModelProperty(value = "Field is used for holding candidaGlabrata data as a String type.")
    private String candidaGlabrata;
    @ApiModelProperty(value = "Field is used for holding candidaKrusei data as a String type.")
    private String candidaKrusei;
    @ApiModelProperty(value = "Field is used for holding candidaParapsilosis data as a String type.")
    private String candidaParapsilosis;
    @ApiModelProperty(value = "Field is used for holding candidaTropicalis data as a String type.")
    private String candidaTropicalis;
    @ApiModelProperty(value = "Field is used for holding reportType data as a String type.")
    private String reportType;
    @ApiModelProperty(value = "Field is used for holding fileName data as a String type.")
    private String phoneNumber;
    @ApiModelProperty(value = "Field is used for holding fileName data as a String type.")
    private String vaginalHealthMarkersRating;
    @ApiModelProperty(value = "Field is used for holding fileName data as a String type.")
    private String lactobacillusCrispatusAmbudance;
    @ApiModelProperty(value = "Field is used for holding fileName data as a String type.")
    private String lactobacillusGasseriAmbudance;
    @ApiModelProperty(value = "Field is used for holding fileName data as a String type.")
    private String lactobacillusInersAmbudance;
    @ApiModelProperty(value = "Field is used for holding fileName data as a String type.")
    private String lactobacillusJenseniiAmbudance;
    @ApiModelProperty(value = "Field is used for holding fileName data as a String type.")
    private String atopobiumVaginaeAmbudance;
    @ApiModelProperty(value = "Field is used for holding fileName data as a String type.")
    private String bVAB2Ambudance;
    @ApiModelProperty(value = "Field is used for holding fileName data as a String type.")
    private String gardnerellaVaginalisAmbudance;
    @ApiModelProperty(value = "Field is used for holding fileName data as a String type.")
    private String megasphera1Ambudance;
    @ApiModelProperty(value = "Field is used for holding fileName data as a String type.")
    private String megasphera2Ambudance;
    @ApiModelProperty(value = "Field is used for holding fileName data as a String type.")
    private String mobiluncusCurtisiiAmbudance;
    @ApiModelProperty(value = "Field is used for holding fileName data as a String type.")
    private String mobiluncusMulierisAmbudance;
    @ApiModelProperty(value = "Field is used for holding fileName data as a String type.")
    private String prevotellaBiviaAmbudance;
    @ApiModelProperty(value = "Field is used for holding fileName data as a String type.")
    private String ureaplasmaUrealyticumAmbudance;
    @ApiModelProperty(value = "Field is used for holding fileName data as a String type.")
    private String enterococcusFaecalisAmbudance;
    @ApiModelProperty(value = "Field is used for holding fileName data as a String type.")
    private String escherichiaColiAmbudance;
    @ApiModelProperty(value = "Field is used for holding fileName data as a String type.")
    private String staphylococcusAureusAmbudance;
    @ApiModelProperty(value = "Field is used for holding fileName data as a String type.")
    private String streptococcusAgalactiaeAmbudance;
    @ApiModelProperty(value = "Field is used for holding fileName data as a String type.")
    private String candidaAlbicansAmbudance;
    @ApiModelProperty(value = "Field is used for holding fileName data as a String type.")
    private String candidaGlabrataAmbudance;
    @ApiModelProperty(value = "Field is used for holding fileName data as a String type.")
    private String candidaKruseiAmbudance;
    @ApiModelProperty(value = "Field is used for holding fileName data as a String type.")
    private String candidaParapsilosisAmbudance;
    @ApiModelProperty(value = "Field is used for holding fileName data as a String type.")
    private String candidaTropicalisAmbudance;
}