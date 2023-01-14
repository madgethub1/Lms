package io.albot.lims.report.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;

import io.albot.lims.report.config.AccessToken;
import io.albot.lims.report.config.Constants;
import io.albot.lims.report.exceptions.NotFoundException;
import io.albot.lims.report.exceptions.PlateNotFoundException;
import io.albot.lims.report.exceptions.ReportNotFoundException;
import io.albot.lims.report.model.dto.*;
import io.albot.lims.report.model.web.*;
import io.albot.lims.report.repos.postgres.*;
import io.albot.lims.report.service.MicroBiomeService;
import lombok.extern.slf4j.Slf4j;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.joda.time.Months;
import org.json.JSONArray;
import org.json.JSONObject;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import com.itextpdf.io.source.ByteArrayOutputStream;
import org.springframework.web.util.HtmlUtils;

@Slf4j
@Service
public class MicroBiomeServiceImpl implements MicroBiomeService {

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private final MicroBiomesRepository microBiomesRepository;

	@Autowired
	private final ReportTypeRepository reportTypeRepository;

	@Autowired
	private final OralEcologixRepository oralEcologixRepository;

	@Autowired
	private final UserDemoGraphicsRepository userDemoGraphicsRepository;

	@Autowired
	private VelocityEngine velocityEngine;

	@Autowired
	private SampleRepository samplerepo;

	static RestTemplate restTemplate = new RestTemplate();

	@Value("${limstoken.username}")
	String username;

	@Value("${limstoken.password}")
	String password;

	@Value("${limstoken.tokenurl}")
	String tokenurl;

	@Value("${apiurl.userapi}")
	String userapi;

	@Value("${searchtype.sample}")
	String sample;

	@Value("${searchtype.plate}")
	String plate;

	@Value("${searchtype.protocol}")
	String protocol;

	@Value("${searchtype.all}")
	String all;

	@Value("${sortfield.samplefield}")
	String samplefield;

	@Value("${sortfield.platefield}")
	String platefield;

	@Value("${sortfield.protocolfield}")
	String protocolfield;


	private String femaleEcoPath = "/templates/general/index.html";
	private String oralEcoPath = "/templates/general/indexOral.html";


	LocalDate currentDate;
	LocalDate currentDateMinusMonths;
	Date date1 = null;
	Date date2 = null;
	Page<MicroBiomesEntity> reportList1 = null;
	Page<OralEcologixReportEntity> reportListOral = null;

	@Autowired
	private SampleRepository sampleRepository;

	@Autowired
	private  PatientRepository patientRepository;

	public Sort sort;
	public PageRequest pageRequest;

	public MicroBiomeServiceImpl(ModelMapper modelMapper, MicroBiomesRepository microBiomesRepository,
								 UserDemoGraphicsRepository userDemoGraphicsRepository, ReportTypeRepository reportTypeRepository,
								 OralEcologixRepository oralEcologixRepository) {
		this.modelMapper = modelMapper;
		this.microBiomesRepository = microBiomesRepository;
		this.userDemoGraphicsRepository = userDemoGraphicsRepository;
		this.reportTypeRepository = reportTypeRepository;
		this.oralEcologixRepository = oralEcologixRepository;
	}

	@Override
	public ByteArrayInputStream getReport(String phoneNumber, String fileType) {
		// public ByteArrayOutputStream getReport(String phoneNumber, String fileType) {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		MicroBiomesEntity latestReport = null;
		String pdf = null;
		UserDemoGraphicsEntity userDemoGraphicsEntity = userDemoGraphicsRepository.findByPhoneNumber(phoneNumber);
		if (!Objects.isNull(userDemoGraphicsEntity)) {
			List<MicroBiomesEntity> reportList = null;//microBiomesRepository.getReport1(userDemoGraphicsEntity, fileType);
			if (!reportList.isEmpty() && reportList.size() > 0) {
				latestReport = reportList.get(0);
				byteArrayOutputStream = genratePdfThroughItext(htmlFile(latestReport));
			}
			return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
			// return byteArrayOutputStream;
		}
		return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
		// return byteArrayOutputStream;
	}

	@Override
	public String saveReport(MicroBiomesCreation microBiomesCreation) {
		UserDemoGraphicsEntity userDemoGraphicsEntity = userDemoGraphicsRepository
				.findByPhoneNumber(microBiomesCreation.getPhoneNumber());
		if (!Objects.isNull(userDemoGraphicsEntity)) {
			MicroBiomesEntity microBiomesEntity = convertToAppointmentEntity(microBiomesCreation);
			//microBiomesEntity.setPhoneNumber(userDemoGraphicsEntity);
			setDefaultValue(microBiomesEntity);
			microBiomesRepository.save(microBiomesEntity);
			return "InforMation Submitted SuccessFully";
		}
		return "User Not exist";
	}

	private MicroBiomesEntity convertToAppointmentEntity(MicroBiomesCreation microBiomesCreation) {
		return modelMapper.map(microBiomesCreation, MicroBiomesEntity.class);
	}

	private void setDefaultValue(MicroBiomesEntity microBiomesEntity) {
		String defaultValue = "&lt;DL";
		if (Objects.isNull(microBiomesEntity.getLactobacillusCrispatus()))
			microBiomesEntity.setLactobacillusCrispatus(defaultValue);
		if (Objects.isNull(microBiomesEntity.getLactobacillusGasseri()))
			microBiomesEntity.setLactobacillusGasseri(defaultValue);
		if (Objects.isNull(microBiomesEntity.getLactobacillusIners()))
			microBiomesEntity.setLactobacillusIners(defaultValue);
		if (Objects.isNull(microBiomesEntity.getLactobacillusJensenii()))
			microBiomesEntity.setLactobacillusJensenii(defaultValue);
		if (Objects.isNull(microBiomesEntity.getAtopobiumVaginae()))
			microBiomesEntity.setAtopobiumVaginae(defaultValue);
		if (Objects.isNull(microBiomesEntity.getBVAB2()))
			microBiomesEntity.setBVAB2(defaultValue);
		if (Objects.isNull(microBiomesEntity.getGardnerellaVaginalis()))
			microBiomesEntity.setGardnerellaVaginalis(defaultValue);
		if (Objects.isNull(microBiomesEntity.getMegasphera1()))
			microBiomesEntity.setMegasphera1(defaultValue);
		if (Objects.isNull(microBiomesEntity.getMegasphera2()))
			microBiomesEntity.setMegasphera2(defaultValue);
		if (Objects.isNull(microBiomesEntity.getMobiluncusCurtisii()))
			microBiomesEntity.setMobiluncusCurtisii(defaultValue);
		if (Objects.isNull(microBiomesEntity.getMobiluncusMulieris()))
			microBiomesEntity.setMobiluncusMulieris(defaultValue);
		if (Objects.isNull(microBiomesEntity.getPrevotellaBivia()))
			microBiomesEntity.setPrevotellaBivia(defaultValue);
		if (Objects.isNull(microBiomesEntity.getUreaplasmaUrealyticum()))
			microBiomesEntity.setUreaplasmaUrealyticum(defaultValue);
		if (Objects.isNull(microBiomesEntity.getEnterococcusFaecalis()))
			microBiomesEntity.setEnterococcusFaecalis(defaultValue);
		if (Objects.isNull(microBiomesEntity.getEscherichiaColi()))
			microBiomesEntity.setEscherichiaColi(defaultValue);
		if (Objects.isNull(microBiomesEntity.getStaphylococcusAureus()))
			microBiomesEntity.setStaphylococcusAureus(defaultValue);
		if (Objects.isNull(microBiomesEntity.getStreptococcusAgalactiae()))
			microBiomesEntity.setStreptococcusAgalactiae(defaultValue);
		if (Objects.isNull(microBiomesEntity.getCandidaAlbicans()))
			microBiomesEntity.setCandidaAlbicans(defaultValue);
		if (Objects.isNull(microBiomesEntity.getCandidaGlabrata()))
			microBiomesEntity.setCandidaGlabrata(defaultValue);
		if (Objects.isNull(microBiomesEntity.getCandidaKrusei()))
			microBiomesEntity.setCandidaKrusei(defaultValue);
		if (Objects.isNull(microBiomesEntity.getCandidaParapsilosis()))
			microBiomesEntity.setCandidaParapsilosis(defaultValue);
		if (Objects.isNull(microBiomesEntity.getCandidaTropicalis()))
			microBiomesEntity.setCandidaTropicalis(defaultValue);
		if (Objects.isNull(microBiomesEntity.getVaginalHealthMarkersRating()))
			microBiomesEntity.setVaginalHealthMarkersRating(defaultValue);
		if (Objects.isNull(microBiomesEntity.getLactobacillusCrispatusAmbudance()))
			microBiomesEntity.setLactobacillusCrispatusAmbudance(defaultValue);
		if (Objects.isNull(microBiomesEntity.getLactobacillusGasseriAmbudance()))
			microBiomesEntity.setLactobacillusGasseriAmbudance(defaultValue);
		if (Objects.isNull(microBiomesEntity.getLactobacillusInersAmbudance()))
			microBiomesEntity.setLactobacillusInersAmbudance(defaultValue);
		if (Objects.isNull(microBiomesEntity.getLactobacillusJenseniiAmbudance()))
			microBiomesEntity.setLactobacillusJenseniiAmbudance(defaultValue);
		if (Objects.isNull(microBiomesEntity.getLactobacillusJenseniiAmbudance()))
			microBiomesEntity.setLactobacillusJenseniiAmbudance(defaultValue);
		if (Objects.isNull(microBiomesEntity.getAtopobiumVaginaeAmbudance()))
			microBiomesEntity.setAtopobiumVaginaeAmbudance(defaultValue);
		if (Objects.isNull(microBiomesEntity.getBVAB2Ambudance()))
			microBiomesEntity.setBVAB2Ambudance(defaultValue);
		if (Objects.isNull(microBiomesEntity.getGardnerellaVaginalisAmbudance()))
			microBiomesEntity.setGardnerellaVaginalisAmbudance(defaultValue);
		if (Objects.isNull(microBiomesEntity.getMegasphera1Ambudance()))
			microBiomesEntity.setMegasphera1Ambudance(defaultValue);
		if (Objects.isNull(microBiomesEntity.getMegasphera2Ambudance()))
			microBiomesEntity.setMegasphera2Ambudance(defaultValue);
		if (Objects.isNull(microBiomesEntity.getMobiluncusCurtisiiAmbudance()))
			microBiomesEntity.setMobiluncusCurtisiiAmbudance(defaultValue);
		if (Objects.isNull(microBiomesEntity.getMobiluncusMulierisAmbudance()))
			microBiomesEntity.setMobiluncusMulierisAmbudance(defaultValue);
		if (Objects.isNull(microBiomesEntity.getPrevotellaBiviaAmbudance()))
			microBiomesEntity.setPrevotellaBiviaAmbudance(defaultValue);
		if (Objects.isNull(microBiomesEntity.getUreaplasmaUrealyticumAmbudance()))
			microBiomesEntity.setUreaplasmaUrealyticumAmbudance(defaultValue);
		if (Objects.isNull(microBiomesEntity.getEnterococcusFaecalisAmbudance()))
			microBiomesEntity.setEnterococcusFaecalisAmbudance(defaultValue);
		if (Objects.isNull(microBiomesEntity.getEscherichiaColiAmbudance()))
			microBiomesEntity.setEscherichiaColiAmbudance(defaultValue);
		if (Objects.isNull(microBiomesEntity.getStaphylococcusAureusAmbudance()))
			microBiomesEntity.setStaphylococcusAureusAmbudance(defaultValue);
		if (Objects.isNull(microBiomesEntity.getStreptococcusAgalactiaeAmbudance()))
			microBiomesEntity.setStreptococcusAgalactiaeAmbudance(defaultValue);
		if (Objects.isNull(microBiomesEntity.getCandidaAlbicansAmbudance()))
			microBiomesEntity.setCandidaAlbicansAmbudance(defaultValue);
		if (Objects.isNull(microBiomesEntity.getCandidaGlabrataAmbudance()))
			microBiomesEntity.setCandidaGlabrataAmbudance(defaultValue);
		if (Objects.isNull(microBiomesEntity.getCandidaKruseiAmbudance()))
			microBiomesEntity.setCandidaKruseiAmbudance(defaultValue);
		if (Objects.isNull(microBiomesEntity.getCandidaParapsilosisAmbudance()))
			microBiomesEntity.setCandidaParapsilosisAmbudance(defaultValue);
		if (Objects.isNull(microBiomesEntity.getCandidaTropicalisAmbudance()))
			microBiomesEntity.setCandidaTropicalisAmbudance(defaultValue);
	}

	public VelocityContext writeHtml(VelocityContext context, MicroBiomesEntity latestReport) {
		if(latestReport.getSampleid() != null) {
			context.put("sampleId", latestReport.getSampleid());
		}else{
			context.put("sampleId", "");
		}
		if(latestReport.getTestReported() != null) {
			context.put("testReported", latestReport.getTestReported());
		}else{
			context.put("testReported", "");
		}
		if(latestReport.getTestReceived() != null) {
			context.put("testReceived", latestReport.getTestReceived());
		}else{
			context.put("testReceived", "");
		}
		if(latestReport.getSampleType() != null) {
			context.put("sampleType", HtmlUtils.htmlEscape(latestReport.getSampleType()));
		}else{
			context.put("sampleType", "");
		}
		if(latestReport.getGender() != null) {
			context.put("gender", latestReport.getGender());
		}else{
			context.put("gender", "");
		}
		if(latestReport.getReportStatus() != null) {
			context.put("reportStatus", latestReport.getReportStatus());
		}else{
			context.put("reportStatus", "");
		}
		if(latestReport.getClinicianName() != null) {
			context.put("clinicianName", latestReport.getClinicianName());
		}else{
			context.put("clinicianName", "");
		}
		if(latestReport.getAccessionNo() != null) {
			context.put("accessionNo", latestReport.getAccessionNo());
		}else{
			context.put("accessionNo", "");
		}
		if(latestReport.getVaginalPh() != null) {
			context.put("vaginalPh", HtmlUtils.htmlEscape(latestReport.getVaginalPh()));
		}else{
			context.put("vaginalPh", "");
		}

//		context.put("vaginalHealthMarkers", HtmlUtils.htmlEscape(latestReport.getVaginalHealthMarkers()));
		context.put("lactobacillusCrispatus", HtmlUtils.htmlEscape(latestReport.getLactobacillusCrispatus()));
		context.put("lactobacillusGasseri", HtmlUtils.htmlEscape(latestReport.getLactobacillusGasseri()));
		context.put("lactobacillusIners", HtmlUtils.htmlEscape(latestReport.getLactobacillusIners()));
		context.put("lactobacillusJensenii", HtmlUtils.htmlEscape(latestReport.getLactobacillusJensenii()));
		context.put("atopobiumVaginae", HtmlUtils.htmlEscape(latestReport.getAtopobiumVaginae()));
		context.put("bVAB2", HtmlUtils.htmlEscape(latestReport.getBVAB2()));
		context.put("gardnerellaVaginalis", HtmlUtils.htmlEscape(latestReport.getGardnerellaVaginalis()));
		context.put("megasphera1", HtmlUtils.htmlEscape(latestReport.getMegasphera1()));
		context.put("megasphera2", HtmlUtils.htmlEscape(latestReport.getMegasphera2()));
		context.put("mobiluncusCurtisii", HtmlUtils.htmlEscape(latestReport.getMobiluncusCurtisii()));
		context.put("mobiluncusMulieris", HtmlUtils.htmlEscape(latestReport.getMobiluncusMulieris()));
		context.put("prevotellaBivia", HtmlUtils.htmlEscape(latestReport.getPrevotellaBivia()));
		context.put("ureaplasmaUrealyticum", HtmlUtils.htmlEscape(latestReport.getUreaplasmaUrealyticum()));
		context.put("enterococcusFaecalis", HtmlUtils.htmlEscape(latestReport.getEnterococcusFaecalis()));
		context.put("escherichiaColi", HtmlUtils.htmlEscape(latestReport.getEscherichiaColi()));
		context.put("staphylococcusAureus", HtmlUtils.htmlEscape(latestReport.getStaphylococcusAureus()));
		context.put("streptococcusAgalactiae", HtmlUtils.htmlEscape(latestReport.getStreptococcusAgalactiae()));
		context.put("candidaAlbicans", HtmlUtils.htmlEscape(latestReport.getCandidaAlbicans()));
		context.put("candidaGlabrata", HtmlUtils.htmlEscape(latestReport.getCandidaGlabrata()));
		context.put("candidaKrusei", HtmlUtils.htmlEscape(latestReport.getCandidaKrusei()));
		context.put("candidaParapsilosis", HtmlUtils.htmlEscape(latestReport.getCandidaParapsilosis()));
		context.put("candidaTropicalis", HtmlUtils.htmlEscape(latestReport.getCandidaTropicalis()));
		if(latestReport.getPatientName() != null) {
			context.put("user_name", latestReport.getPatientName());//getPhoneNumber().getUserName());
		}else{
			context.put("user_name", "");
		}
		if(latestReport.getPatientDob() != null) {
			context.put("user_dob", latestReport.getPatientDob().toString());//getPhoneNumber().getPatientDOB());
		}else{
			context.put("user_dob", "");
		}
		context.put("vaginalHealthMarkersRating", HtmlUtils.htmlEscape(latestReport.getVaginalHealthMarkersRating()));
		context.put("lactobacillusCrispatusAmbudance", HtmlUtils.htmlEscape(latestReport.getLactobacillusCrispatusAmbudance()));
		context.put("lactobacillusGasseriAmbudance", HtmlUtils.htmlEscape(latestReport.getLactobacillusGasseriAmbudance()));
		context.put("lactobacillusInersAmbudance", HtmlUtils.htmlEscape(latestReport.getLactobacillusInersAmbudance()));
		context.put("lactobacillusJenseniiAmbudance", HtmlUtils.htmlEscape(latestReport.getLactobacillusJenseniiAmbudance()));
		context.put("atopobiumVaginaeAmbudance", HtmlUtils.htmlEscape(latestReport.getAtopobiumVaginaeAmbudance()));
		context.put("bVAB2Ambudance", HtmlUtils.htmlEscape(latestReport.getBVAB2Ambudance()));
		context.put("gardnerellaVaginalisAmbudance", HtmlUtils.htmlEscape(latestReport.getGardnerellaVaginalisAmbudance()));
		context.put("megasphera1Ambudance", HtmlUtils.htmlEscape(latestReport.getMegasphera1Ambudance()));
		context.put("megasphera2Ambudance", HtmlUtils.htmlEscape(latestReport.getMegasphera2Ambudance()));
		context.put("mobiluncusCurtisiiAmbudance", HtmlUtils.htmlEscape(latestReport.getMobiluncusCurtisiiAmbudance()));
		context.put("mobiluncusMulierisAmbudance", HtmlUtils.htmlEscape(latestReport.getMobiluncusMulierisAmbudance()));
		context.put("prevotellaBiviaAmbudance", HtmlUtils.htmlEscape(latestReport.getPrevotellaBiviaAmbudance()));
		context.put("ureaplasmaUrealyticumAmbudance", HtmlUtils.htmlEscape(latestReport.getUreaplasmaUrealyticumAmbudance()));
		context.put("enterococcusFaecalisAmbudance", HtmlUtils.htmlEscape(latestReport.getEnterococcusFaecalisAmbudance()));
		context.put("escherichiaColiAmbudance", HtmlUtils.htmlEscape(latestReport.getEscherichiaColiAmbudance()));
		context.put("staphylococcusAureusAmbudance", HtmlUtils.htmlEscape(latestReport.getStaphylococcusAureusAmbudance()));
		context.put("streptococcusAgalactiaeAmbudance", HtmlUtils.htmlEscape(latestReport.getStreptococcusAgalactiaeAmbudance()));
		context.put("candidaAlbicansAmbudance", HtmlUtils.htmlEscape(latestReport.getCandidaAlbicansAmbudance()));
		context.put("candidaGlabrataAmbudance", HtmlUtils.htmlEscape(latestReport.getCandidaGlabrataAmbudance()));
		context.put("candidaKruseiAmbudance", HtmlUtils.htmlEscape(latestReport.getCandidaKruseiAmbudance()));
		context.put("candidaParapsilosisAmbudance", HtmlUtils.htmlEscape(latestReport.getCandidaParapsilosisAmbudance()));
		context.put("candidaTropicalisAmbudance", HtmlUtils.htmlEscape(latestReport.getCandidaTropicalisAmbudance()));
		return context;
	}

	public void genratePdfByHtml() {

		try {
			HtmlConverter.convertToPdf(new FileInputStream("src/main/resources/templates/general/test.html"),
					new FileOutputStream("src/main/resources/templates/general/string-to-pdf.pdf"));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/*
	 * public ByteArrayOutputStream fetchingVM(MicroBiomesEntity latestReport) {
	 *
	 * VelocityEngine velocity = new VelocityEngine();
	 * velocity.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
	 * velocity.setProperty("classpath.resource.loader.class",
	 * ClasspathResourceLoader.class.getName()); velocity.init();
	 *
	 *
	 * Template template = velocity.getTemplate(path);
	 *
	 * VelocityContext context = new VelocityContext(); context = writeHtml(context,
	 * latestReport);
	 *
	 * StringWriter writer = new StringWriter(); template.merge(context, writer);
	 *
	 * System.out.println(writer);
	 *
	 * //genratePdfByHtml(); //ByteArrayOutputStream target = new
	 * ByteArrayOutputStream(); //HtmlConverter.convertToPdf(writer.toString() ,
	 * target);
	 *
	 * return genratePdfThroughItext(writer.toString()); //target; }
	 */
	ByteArrayOutputStream genratePdfThroughItext(String html) {
		PdfWriter pdfWriter = null;
		Document document = new Document();
		try {
			document = new Document();
			document.addAuthor("albot");
			document.addAuthor("albot");
			document.addCreationDate();
			document.addProducer();
			document.addCreator("");
			document.addTitle("");
			document.setPageSize(PageSize.LETTER);

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			PdfWriter.getInstance(document, baos);
			ByteArrayInputStream bis = new ByteArrayInputStream(html.toString().getBytes());

			document.open();
			XMLWorkerHelper xmlWorkerHelper = XMLWorkerHelper.getInstance();
			xmlWorkerHelper.getDefaultCssResolver(true);
			xmlWorkerHelper.parseXHtml(pdfWriter, document, bis);

			document.close();
			return baos;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/*
	 * @Override public String viewReport1(String phoneNumber, String fileType) {
	 * MicroBiomesEntity latestReport = null; String pdf = null;
	 * UserDemoGraphicsEntity userDemoGraphicsEntity =
	 * userDemoGraphicsRepository.findByPhoneNumber(phoneNumber); if
	 * (!Objects.isNull(userDemoGraphicsEntity)) { List<MicroBiomesEntity>
	 * reportList = microBiomesRepository.getReport1(userDemoGraphicsEntity,
	 * fileType); if (!reportList.isEmpty() && reportList.size() > 0) { latestReport
	 * = reportList.get(0); pdf = htmlFile(latestReport); } } return pdf; }
	 */
	@Override
	public String viewReport(long reportid, String reportType) {
		MicroBiomesEntity report = null;
		OralEcologixReportEntity oralReport = null;
		String pdf = null;
		if (reportid < 1) {
			throw new ReportNotFoundException("No Report Found", Constants.STATUS_CODE_NO_DATA_FOUND);
		}

		if (reportid > 0) {
			if (reportType.equals(Constants.femaleEcolife)) {
				report = microBiomesRepository.getReport(reportid, reportType);
				if (!Objects.isNull(report)) {
					pdf = htmlFile(report);
				} else {
					throw new ReportNotFoundException("No Report Found", Constants.STATUS_CODE_NO_DATA_FOUND);
				}
			} else if (reportType.equals(Constants.oralEcolife)) {
				oralReport = oralEcologixRepository.getReport(reportid, reportType);
				if (!Objects.isNull(oralReport)) {
					pdf = htmlFileOralEco(oralReport);
				} else {
					throw new ReportNotFoundException("No Report Found", Constants.STATUS_CODE_NO_DATA_FOUND);
				}
			} else {
				throw new NotFoundException("Report Type is not found");
			}

		}
		return pdf;
	}

	private String htmlFileOralEco(OralEcologixReportEntity oralReport) {

		VelocityEngine velocity = new VelocityEngine();
		velocity.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
		velocity.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		velocity.init();

		Template template = null;

		if (oralReport.getReportType().equals(Constants.oralEcolife)) {
			template = velocity.getTemplate(oralEcoPath);
		} else {
			throw new NotFoundException("Report Type is not found in db");
		}

		VelocityContext context = new VelocityContext();
		context = writeHtmlOral(context, oralReport);

		StringWriter writer = new StringWriter();
		template.merge(context, writer);

		return writer.toString();
	}

	private VelocityContext writeHtmlOral(VelocityContext context, OralEcologixReportEntity oralReport) {
/*
		context.put("sampleId", oralReport.getSampleid());
		context.put("testReported", oralReport.getTestReported());
		context.put("testReceived", oralReport.getTestReceived());
		context.put("sampleType", oralReport.getSampleType());
		context.put("gender", oralReport.getGender());
		context.put("reportStatus", oralReport.getReportStatus());
		context.put("clinicianName", oralReport.getClinicianName());
		context.put("accessionNo", oralReport.getAccessionNo());
		context.put("user_name", oralReport.getPatientName());
		context.put("user_dob", oralReport.getPatientdob());
*/
		if(oralReport.getSampleid() != null) {
			context.put("sampleId", oralReport.getSampleid());
		}else{
			context.put("sampleId", "");
		}
		if(oralReport.getTestReported() != null) {
			context.put("testReported", oralReport.getTestReported());
		}else{
			context.put("testReported", "");
		}
		if(oralReport.getTestReceived() != null) {
			context.put("testReceived", oralReport.getTestReceived());
		}else{
			context.put("testReceived", "");
		}
		if(oralReport.getSampleType() != null) {
			context.put("sampleType", HtmlUtils.htmlEscape(oralReport.getSampleType()));
		}else{
			context.put("sampleType", "");
		}
		if(oralReport.getGender() != null) {
			context.put("gender", oralReport.getGender());
		}else{
			context.put("gender", "");
		}
		if(oralReport.getReportStatus() != null) {
			context.put("reportStatus", oralReport.getReportStatus());
		}else{
			context.put("reportStatus", "");
		}
		if(oralReport.getClinicianName() != null) {
			context.put("clinicianName", oralReport.getClinicianName());
		}else{
			context.put("clinicianName", "");
		}
		if(oralReport.getAccessionNo() != null) {
			context.put("accessionNo", oralReport.getAccessionNo());
		}else{
			context.put("accessionNo", "");
		}
		if(oralReport.getPatientName() != null) {
			context.put("user_name", oralReport.getPatientName());
		}else{
			context.put("user_name", "");
		}
		if(oralReport.getPatientdob() != null) {
			context.put("user_dob", oralReport.getPatientdob().toString());
		}else{
			context.put("user_dob", "");
		}

		context.put("aggregatibacterActinomycetemcomitans", oralReport.getAggregatibacterActinomycetemcomitans());
		context.put("porphyromonasGingivalis", oralReport.getPorphyromonasGingivalis());
		context.put("tannerellaForsythia", oralReport.getTannerellaForsythia());
		context.put("treponemaDenticola", oralReport.getTreponemaDenticola());
		context.put("candidaAlbicans", oralReport.getCandidaAlbicans());
		context.put("campylobacterRectus", oralReport.getCampylobacterRectus());
		context.put("eubacteriumNodatum", oralReport.getEubacteriumNodatum());
		context.put("fusobacteriumNucleatum", oralReport.getFusobacteriumNucleatum());
		context.put("lactobacillusSpp", oralReport.getLactobacillusSpp());
		context.put("parvimonasMicra", oralReport.getParvimonasMicra());
		context.put("peptostreptococcusAnaerobius", oralReport.getPeptostreptococcusAnaerobius());
		context.put("prevotellaIntermedia", oralReport.getPrevotellaIntermedia());
		context.put("prevotellaNigrescens", oralReport.getPrevotellaNigrescens());
		context.put("streptococcusMutans", oralReport.getStreptococcusMutans());

		return context;
	}

	public String htmlFile(MicroBiomesEntity latestReport) {

		VelocityEngine velocity = new VelocityEngine();
		velocity.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
		velocity.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		velocity.init();

		Template template = null;

		if (latestReport.getReportType().equals(Constants.femaleEcolife)) {
			template = velocity.getTemplate(femaleEcoPath);
		} else if (latestReport.getReportType().equals(Constants.oralEcolife)) {
			template = velocity.getTemplate(oralEcoPath);
		} else {
			throw new NotFoundException("Report Type is not found in db");
		}

		VelocityContext context = new VelocityContext();
		context = writeHtml(context, latestReport);

		StringWriter writer = new StringWriter();
		template.merge(context, writer);

		return writer.toString();
	}

	@Override
	public ReportListResponse findAllReports(ReportListRequest reportListRequest, int page, String sortField, String sortDirection, int recordPerPage) {
		ReportListResponse reportList =  new ReportListResponse();
		List<ReportListContent> reportContent = new ArrayList<ReportListContent>();

		Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();
		PageRequest pageRequest = PageRequest.of(page - 1, recordPerPage, sort);

		if (reportListRequest.getEmailid() == "" || reportListRequest.getEmailid() == null) {
			throw new NotFoundException("Please enter emailid");
		} else {

			boolean result = reportFilterValidation(reportListRequest, pageRequest);

			if (result == false) {
				throw new NotFoundException("Please check start/end date or number of months or report type");
			}
			int size =0;
			int sizeOral = 0;

			if(reportListRequest.getReportType().equals(Constants.femaleEcolife)) size = reportList1.getContent().size();
			if(reportListRequest.getReportType().equals(Constants.oralEcolife)) sizeOral = reportListOral.getContent().size();

			if (size > 0) {

				for (int i = 0; i < size; i++) {
					ReportListContent rlc = new ReportListContent();
					MicroBiomesEntity mbe = new MicroBiomesEntity();
					mbe = reportList1.getContent().get(i);
					rlc.setReportid(mbe.getReportId());
					rlc.setPatientName(mbe.getPatientName());
					rlc.setTestReportDate(mbe.getTestReported());

					reportContent.add(rlc);
					reportList.setContent(reportContent);
				}
				setPaginationData(reportList1, reportList);

			}
			if (sizeOral > 0) {

				for (int i = 0; i < sizeOral; i++) {
					ReportListContent rlc = new ReportListContent();
					OralEcologixReportEntity obe = new OralEcologixReportEntity();
					obe = reportListOral.getContent().get(i);
					rlc.setReportid(obe.getReportId());
					rlc.setPatientName(obe.getPatientName());
					rlc.setTestReportDate(obe.getTestReported());
					reportContent.add(rlc);
					reportList.setContent(reportContent);
				}
				setPaginationDataOral(reportListOral, reportList);
			}

			if (size < 1 && sizeOral < 1) {
				throw new NotFoundException("No Reports Found");
			}
		}
		return reportList;
	}

	@Override
	public ReportListResponse findAllReportsLab(ReportListRequest reportListRequest, int page, String sortField, String sortDirection, int recordPerPage) {

		ReportListResponse reportList =  new ReportListResponse();
		List<ReportListContent> reportContent = new ArrayList<ReportListContent>();

		Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();
		PageRequest pageRequest = PageRequest.of(page - 1, recordPerPage, sort);

		boolean result = reportFilterValidation(reportListRequest, pageRequest);

		if (result == false) {
			throw new NotFoundException("Please check report type");
		}
		int size =0;
		int sizeOral = 0;

		if(reportListRequest.getReportType().equals(Constants.femaleEcolife)) size = reportList1.getContent().size();
		if(reportListRequest.getReportType().equals(Constants.oralEcolife)) sizeOral = reportListOral.getContent().size();

		if (size > 0) {

			for (int i = 0; i < size; i++) {
				ReportListContent rlc = new ReportListContent();
				MicroBiomesEntity mbe = new MicroBiomesEntity();
				mbe = reportList1.getContent().get(i);
				rlc.setReportid(mbe.getReportId());
				rlc.setPatientName(mbe.getPatientName());
				rlc.setTestReportDate(mbe.getTestReported());
				rlc.setReportType(mbe.getReportType());
				if(mbe.getReportStatus() != null)
					rlc.setReportStatus(mbe.getReportStatus());

				reportContent.add(rlc);
				reportList.setContent(reportContent);
			}
			setPaginationData(reportList1, reportList);

		}
		if (sizeOral > 0) {

			for (int i = 0; i < sizeOral; i++) {
				ReportListContent rlc = new ReportListContent();
				OralEcologixReportEntity obe = new OralEcologixReportEntity();
				obe = reportListOral.getContent().get(i);
				rlc.setReportid(obe.getReportId());
				rlc.setPatientName(obe.getPatientName());
				rlc.setTestReportDate(obe.getTestReported());
				rlc.setReportType(obe.getReportType());
				if(obe.getReportStatus() != null)
					rlc.setReportStatus(obe.getReportStatus());

				reportContent.add(rlc);
				reportList.setContent(reportContent);
			}
			setPaginationDataOral(reportListOral, reportList);
		}

		if (size < 1 && sizeOral < 1) {
			throw new NotFoundException("No Reports Found");
		}

		return reportList;
	}

	@Override
	public String reportStatus(ReportStatusBean reportBean) {
		String status = "";
		if(reportBean.getReportType() == null || reportBean.getReportStatus() == null){
			throw new NotFoundException("Please check reportType/ReportStatus");
		}
		if(reportBean.getReportid() > 0 && reportBean.getReportStatus() != null) {

			if (reportBean.getReportType().equals(Constants.femaleEcolife)) {
				MicroBiomesEntity mbe = new MicroBiomesEntity();
				Optional<MicroBiomesEntity> mb = microBiomesRepository.findById(reportBean.getReportid());
				if (mb.isPresent()) {
					mbe = mb.get();
					mbe.setReportStatus(reportBean.getReportStatus());
					microBiomesRepository.save(mbe);
					status = "Report status updated successfully";
				} else {
					throw new NotFoundException("Given report id is not found");
				}
			}else if(reportBean.getReportType().equals(Constants.oralEcolife)){
				OralEcologixReportEntity oralBean = new OralEcologixReportEntity();
				Optional<OralEcologixReportEntity> oral = oralEcologixRepository.findById(reportBean.getReportid());
				if (oral.isPresent()) {
					oralBean = oral.get();
					oralBean.setReportStatus(reportBean.getReportStatus());
					oralEcologixRepository.save(oralBean);
					status = "Report status updated successfully";
				} else {
					throw new NotFoundException("Given report id is not found");
				}
			}else{
				throw new NotFoundException("Given reportType is not found");
			}
		}else{
			throw new NotFoundException("Please check report Id");
		}

		return status;
	}

	@Override
	public ReportListContent getReportDetails(long reportid, String reportType) {
		ReportListContent reportListContent = null;
		if(reportid < 1 || reportType.equals("")){
			throw new NotFoundException("Please verify reportid and reportType");
		}

		if(reportType.equals(Constants.femaleEcolife)){
			reportListContent = new ReportListContent();
			MicroBiomesEntity mbe;
			mbe = microBiomesRepository.getReport(reportid, reportType);
			if(!Objects.isNull(mbe)) {
				reportListContent.setReportid(mbe.getReportId());
				reportListContent.setReportType(mbe.getReportType());
				if (mbe.getPatientName() != null)
					reportListContent.setPatientName(mbe.getPatientName());
				if (mbe.getTestReported() != null)
					reportListContent.setTestReportDate(mbe.getTestReported());
				if (mbe.getReportStatus() != null)
					reportListContent.setReportStatus(mbe.getReportStatus());
			}else{
				throw new ReportNotFoundException("No Report Found", Constants.STATUS_CODE_NO_DATA_FOUND);
			}

		}else if(reportType.equals(Constants.oralEcolife)){
			reportListContent = new ReportListContent();
			OralEcologixReportEntity obe;
			obe = oralEcologixRepository.getReport(reportid, reportType);
			if(!Objects.isNull(obe)) {
				reportListContent.setReportid(obe.getReportId());
				reportListContent.setReportType(obe.getReportType());

				if (obe.getPatientName() != null)
					reportListContent.setPatientName(obe.getPatientName());
				if (obe.getTestReported() != null)
				    reportListContent.setTestReportDate(obe.getTestReported());
				if (obe.getReportStatus() != null)
					reportListContent.setReportStatus(obe.getReportStatus());
			}else{
				throw new ReportNotFoundException("No Report Found", Constants.STATUS_CODE_NO_DATA_FOUND);
			}
		}else{
			throw new ReportNotFoundException("No Report Found", Constants.STATUS_CODE_NO_DATA_FOUND);
		}
		return reportListContent;
	}


	private void setPaginationData(Page<MicroBiomesEntity> reportList1, ReportListResponse reportList) {
		Pageable pg = reportList1.getPageable();
		reportList.setPageable(pg);
		reportList.setNumber(reportList1.getNumber());
		reportList.setSize(reportList1.getSize());
		reportList.setTotalElements(reportList1.getTotalElements());
		reportList.setNumberOfElements(reportList1.getNumberOfElements());
		reportList.setTotalPages(reportList1.getTotalPages());

	}

	private void setPaginationDataOral(Page<OralEcologixReportEntity> reportListoral, ReportListResponse reportList) {
		Pageable pg = reportListoral.getPageable();
		reportList.setPageable(pg);
		reportList.setNumber(reportListoral.getNumber());
		reportList.setSize(reportListoral.getSize());
		reportList.setTotalElements(reportListoral.getTotalElements());
		reportList.setNumberOfElements(reportListoral.getNumberOfElements());
		reportList.setTotalPages(reportListoral.getTotalPages());

	}

	private boolean reportFilterValidation(ReportListRequest reportListRequest, Pageable pageRequest) {

		if(reportListRequest.getEmailid() != null) {
			if (!(reportListRequest.getStartDate() == null) && !(reportListRequest.getEndDate() == null)
					&& reportListRequest.getNoOfMonths() > 0) {
				return false;
			}
		}
		if(reportListRequest.getReportType() == null) {
			return false;
		}
		if (!reportListRequest.getReportType().equals(Constants.femaleEcolife) && !reportListRequest.getReportType().equals(Constants.oralEcolife)) {
			return false;
		}

		if(reportListRequest.getNoOfMonths() > 0 || reportListRequest.getStartDate() != null || reportListRequest.getEndDate() != null) {
			if (reportListRequest.getNoOfMonths() > 0 && reportListRequest.getNoOfMonths() < 7) {
				currentDate = LocalDate.now();
				currentDateMinusMonths = currentDate.minusMonths(reportListRequest.getNoOfMonths());
				currentDate = currentDate.plusDays(1);
				log.debug("currentDate: " + currentDate);

				log.debug("currentDateMinus6Months : " + currentDateMinusMonths);

				ZoneId defaultZoneId = ZoneId.systemDefault();
				Date currdate = Date.from(currentDate.atStartOfDay(defaultZoneId).toInstant());
				Date currminusdate = Date.from(currentDateMinusMonths.atStartOfDay(defaultZoneId).toInstant());
				if (reportListRequest.getReportType().equals(Constants.femaleEcolife)) {
					reportList1 = null;
					if(reportListRequest.getEmailid() != null && reportListRequest.getEmailid() != "") {
						reportList1 = microBiomesRepository.getReportList(reportListRequest.getReportType(),
								reportListRequest.getEmailid(), currminusdate, currdate, pageRequest);
					}else{
						reportList1 = microBiomesRepository.getReportListData(reportListRequest.getReportType(),
								currminusdate, currdate, pageRequest);
					}

				} else if (reportListRequest.getReportType().equals(Constants.oralEcolife)) {
					reportListOral = null;
					if(reportListRequest.getEmailid() != null && reportListRequest.getEmailid() != "") {
						reportListOral = oralEcologixRepository.getReportList(reportListRequest.getReportType(),
								reportListRequest.getEmailid(), currminusdate, currdate, pageRequest);
					}else{
						reportListOral = oralEcologixRepository.getReportListData(reportListRequest.getReportType(), currminusdate, currdate, pageRequest);
					}

				}

				return true;
			}
			if (!(reportListRequest.getStartDate() == null) && !(reportListRequest.getEndDate() == null)) {

				try {
					DateFormat formatter = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy");
					Date fd1 = formatter.parse(reportListRequest.getStartDate().toString());
					Date fd2 = formatter.parse(reportListRequest.getEndDate().toString());

					LocalDate datestart = LocalDate.parse(new SimpleDateFormat("yyyy-MM-dd").format(fd1));
					LocalDate dateend = LocalDate.parse(new SimpleDateFormat("yyyy-MM-dd").format(fd2));

					LocalDate currentDate = LocalDate.now();
					LocalDate currentDateMinus6Months = currentDate.minusMonths(6);

					if (datestart.isAfter(currentDate) || dateend.isAfter(currentDate)) {
						throw new NotFoundException("Start/end date should not be future date!");
					}

					if (datestart.isBefore(currentDateMinus6Months) || dateend.isBefore(currentDateMinus6Months)) {
						throw new NotFoundException("6 months older than current date!");
					}

					log.debug("startdate...." + fd1.toString() + "........enddate" + fd2.toString());
					if (reportListRequest.getReportType().equals(Constants.femaleEcolife)) {
						reportList1 = null;
						if(reportListRequest.getEmailid() != null && reportListRequest.getEmailid() != "") {
							reportList1 = microBiomesRepository.getReportList(reportListRequest.getReportType(),
									reportListRequest.getEmailid(), fd1, fd2, pageRequest);
						}else{
							reportList1 = microBiomesRepository.getReportListData(reportListRequest.getReportType(),
									 fd1, fd2, pageRequest);
						}

					} else if (reportListRequest.getReportType().equals(Constants.oralEcolife)) {
						reportListOral = null;
						if(reportListRequest.getEmailid() != null && reportListRequest.getEmailid() != "") {
							reportListOral = oralEcologixRepository.getReportList(reportListRequest.getReportType(),
									reportListRequest.getEmailid(), fd1, fd2, pageRequest);
						}else{
							reportListOral = oralEcologixRepository.getReportListData(reportListRequest.getReportType(),
									 fd1, fd2, pageRequest);
						}

					}
				} catch (ParseException e) {
					e.printStackTrace();
				}
				return true;
			}
		}else{
			if (reportListRequest.getReportType().equals(Constants.femaleEcolife)) {
				reportList1 = null;
				reportList1 = microBiomesRepository.findAll(pageRequest);
				return true;
			}
			if (reportListRequest.getReportType().equals(Constants.oralEcolife)) {
				reportListOral = null;
				reportListOral = oralEcologixRepository.findAll(pageRequest);
				return true;
			}
		}
		return false;
	}

	@Override
	public String getUserTypeDetails(String userid) {
		int usertype = 0;
		String usertypevalue = "";
		if (userid != "") {
			usertypevalue = restCall(userid);
		} else {
			throw new NotFoundException("user not found in db");
		}

		return usertypevalue;
	}

	private String restCall(String userId) {

		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers.setBearerAuth(AccessToken.getToken(username, password, tokenurl));
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		// String uri = "http://44.230.213.166:30010/common/user/v1/api/user/" + userId;
		String uri = userapi + userId;

		String userTypeValue = "";
		try {
			ResponseEntity<String> res = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);
			log.debug(res.getBody());

			if (200 == res.getStatusCodeValue()) {
				String json = res.getBody().toString();
				JSONObject jsonObj = new JSONObject(json);
				if (jsonObj.getInt("statusCode") == 200) {
					try {
						userTypeValue = jsonObj.getJSONObject("data").getJSONObject("userTypeId").getString("name");
					} catch (Exception ex) {
						userTypeValue = "userType not found in db";
						// throw new NotFoundException(userTypeValue);
					}
				} else {
					userTypeValue = jsonObj.getString("message");
				}
			} else {
				userTypeValue = "Failed to fetch user details";
			}

		} catch (HttpClientErrorException exception) {
			log.debug("callToRestService Error :" + exception.getResponseBodyAsString());

		} catch (HttpStatusCodeException exception) {
			log.debug("callToRestService Error :" + exception.getResponseBodyAsString());

		} catch (Exception exception) {
			log.debug(exception.toString());
		}

		return userTypeValue;
	}

	@Override
	public List<ReportTypeEntity> getReportTypeAll() {
		return reportTypeRepository.findAll();
	}


	@Override
	public GlobalSearchResponse globalSearch(String searchtype, String searchName, int page, String sortField, String sortDirection, int recordPerPage) {
		GlobalSearchResponse gsr = new GlobalSearchResponse();

		Page<SampleEntity> samplelist = null;
		Page<PlatesEntity> platelist = null;
		Page<ProtocolEntity> protocollist = null;
		if(searchtype.isEmpty() || searchName.isEmpty()){
			log.debug("SearchType/SearchName should not be empty");
			throw new NotFoundException("SearchType/SearchName should not be empty");
		}else{
			if(searchtype.equals(sample)){
				if(sortField == null|| sortField.equals("")){
					sortField = samplefield;
				}
				pagination(page, recordPerPage, sortField, sortDirection);
				samplelist = samplerepo.findSampleSearch(searchName,pageRequest);
				if(!samplelist.isEmpty()){
					gsr.setSampleData(samplelist);
				}
			}else if(searchtype.equals(plate)){
				if(sortField == null || sortField.equals("")){
					sortField = platefield;
				}
				pagination(page, recordPerPage, sortField, sortDirection);
				platelist = microBiomesRepository.findPlateSearch(searchName,pageRequest);
				if(!platelist.isEmpty()) {
					gsr.setPlatesData(platelist);
				}
			}else if(searchtype.equals(protocol)){
				if(sortField == null || sortField.equals("")){
					sortField = protocolfield;
				}
				pagination(page, recordPerPage, sortField, sortDirection);
				protocollist = microBiomesRepository.findProtocolSearch(searchName,pageRequest);

				if(!protocollist.isEmpty()) {
					gsr.setProtocolData(protocollist);
				}
			}else if(searchtype.equals(all)){

				PageRequest pageRequest1 = PageRequest.of(page - 1, recordPerPage);

				samplelist = samplerepo.findSampleSearch(searchName,pageRequest1);
				if(!samplelist.isEmpty()){
					gsr.setSampleData(samplelist);
				}
				platelist = microBiomesRepository.findPlateSearch(searchName,pageRequest1);
				if(!platelist.isEmpty()) {
					gsr.setPlatesData(platelist);
				}
				protocollist = microBiomesRepository.findProtocolSearch(searchName,pageRequest1);
				if(!protocollist.isEmpty()) {
					gsr.setProtocolData(protocollist);
				}
			}else{
				log.debug("SearchType is invalid");
				throw new NotFoundException("SearchType is invalid");
			}
		}

		return gsr;
	}

	private void pagination(int page, int recordPerPage, String sortField, String sortDirection) {
		if(page < 1 || recordPerPage < 1){
			log.debug("page/recordPerPage is invalid");
			throw new NotFoundException("page/recordPerPage is invalid");
		}
		if(sortDirection == null || sortDirection.equals("")){
			sortDirection = "asc";
		}
		sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();
		pageRequest = PageRequest.of(page - 1, recordPerPage, sort);
	}

	@Override
	public String saveUpload(String jsonData) {

		JSONObject jsnobject = new JSONObject(jsonData);
		JSONArray jsonArray = jsnobject.getJSONArray("uploadData");
		ObjectMapper mapper = new ObjectMapper();
		String msg ="";
		String found = "" ;
		String notFound ="";

		try {

			//BacteriaReportInfo[] pp1 = mapper.readValue(jsonArray, BacteriaReportInfo[].class);
			List<BacteriaReportInfo> ppl2 = Arrays.asList(mapper.readValue(jsonArray.toString(), BacteriaReportInfo[].class));
			log.debug(ppl2.toString());

			Map<String,List<BacteriaReportInfo>> bactbysampid = new HashMap<>();

			bactbysampid = ppl2.stream() .collect(Collectors.groupingBy(BacteriaReportInfo::getSampleId));

			log.debug("grouped by sampleId: " + new JSONObject(bactbysampid));

			for(int i=0; i < bactbysampid.size(); i++ ){

				MicroBiomesEntity mbe = new MicroBiomesEntity();
				OralEcologixReportEntity oe = new OralEcologixReportEntity();
				Set<String> keys = bactbysampid.keySet();
				log.debug("Key set values are: " + keys.toArray()[i]);
				String key = keys.toArray()[i].toString();

				int btsize = bactbysampid.get(key).size();
				log.debug("size : " + btsize);

				SampleEntity se = new SampleEntity();

				se = sampleRepository.findBySampleId(key);
                if(Objects.isNull(se) || se.getTestOrdered() == null){
					notFound = notFound.concat(key).concat(" ");
					continue;
				}

				if(se.getTestOrdered().equals(Constants.femaleEcolife)) {
					mbe = microBiomesRepository.findBySampleId(key);
					if (Objects.isNull(mbe)) {
						mbe = new MicroBiomesEntity();
						mbe.setSampleid(key);
					}
					sampleDetails(mbe, key, se);

					for (int j = 0; j < btsize; j++) {
						BacteriaReportInfo bt = new BacteriaReportInfo();
						bt = bactbysampid.get(key).get(j);
						String target = bt.getTargetName();
						String lowerTarget = target.replaceAll("[^a-zA-Z0-9]", "").toLowerCase(Locale.ROOT);

						log.debug("lowerTarget values are: " + lowerTarget);

						String value = bt.getDeltaCtMean();

						saveUploadDate(mbe, lowerTarget, value);

					}

					microBiomesRepository.save(mbe);
					found = found.concat(key).concat(" ");
					log.debug(mbe.toString());
				}
				else if(se.getTestOrdered().equals(Constants.oralEcolife)){
					oe = oralEcologixRepository.findBySampleId(key);
					if (Objects.isNull(oe)) {
						oe = new OralEcologixReportEntity();
						oe.setSampleid(key);
					}
					sampleDetailsOral(oe, key, se);

					for (int j = 0; j < btsize; j++) {
						BacteriaReportInfo bt = new BacteriaReportInfo();
						bt = bactbysampid.get(key).get(j);
						String target = bt.getTargetName();
						String lowerTarget = target.replaceAll("[^a-zA-Z0-9]", "").toLowerCase(Locale.ROOT);

						log.debug("lowerTarget values are: " + lowerTarget);

						String value = bt.getDeltaCtMean();

						oe = saveUploadDateOral(oe, lowerTarget, value);

					}

					oralEcologixRepository.save(oe);
					found = found.concat(key).concat(" ");
					log.debug(oe.toString());
				}else {
					notFound = notFound.concat(key).concat(" ");
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		if(!found.isEmpty())
			msg = "Successfully uploaded sampleId: " + found;
		if(!notFound.isEmpty())
			msg += "| Not Uploaded sampleId: "+ notFound;

		return msg;
	}

	private OralEcologixReportEntity saveUploadDateOral(OralEcologixReportEntity oe, String target, String value) {

		String defaultValue = "DL";
		if(value == null || !value.matches("^\\d+\\.\\d+"))
			value = "DL";

		if(target.equalsIgnoreCase(Constants.candidaAlbicans))
			oe.setCandidaAlbicans(value);
		else if(oe.getCandidaAlbicans() == "" || oe.getCandidaAlbicans() == null)
			oe.setCandidaAlbicans(defaultValue);
		if (target.equalsIgnoreCase(Constants.aggregatibacterActinomycetemcomitans))
			oe.setAggregatibacterActinomycetemcomitans(value);
		else if(oe.getAggregatibacterActinomycetemcomitans() == "" || oe.getAggregatibacterActinomycetemcomitans() == null)
			oe.setAggregatibacterActinomycetemcomitans(defaultValue);
		if (target.equalsIgnoreCase(Constants.porphyromonasGingivalis))
			oe.setPorphyromonasGingivalis(value);
		else if(oe.getPorphyromonasGingivalis() == "" || oe.getPorphyromonasGingivalis() == null)
			oe.setPorphyromonasGingivalis(defaultValue);
		if (target.equalsIgnoreCase(Constants.tannerellaForsythia))
			oe.setTannerellaForsythia(value);
		else if(oe.getTannerellaForsythia() == "" || oe.getTannerellaForsythia() == null)
			oe.setTannerellaForsythia(defaultValue);
		if (target.equalsIgnoreCase(Constants.treponemaDenticola))
			oe.setTreponemaDenticola(value);
		else if(oe.getTreponemaDenticola() == "" || oe.getTreponemaDenticola() == null)
			oe.setTreponemaDenticola(defaultValue);
		if (target.equalsIgnoreCase(Constants.campylobacterRectus))
			oe.setCampylobacterRectus(value);
		else if(oe.getCampylobacterRectus() == "" || oe.getCampylobacterRectus() == null)
			oe.setCampylobacterRectus(defaultValue);
		if (target.equalsIgnoreCase(Constants.eubacteriumNodatum))
			oe.setEubacteriumNodatum(value);
		else if(oe.getEubacteriumNodatum() == "" || oe.getEubacteriumNodatum() == null)
			oe.setEubacteriumNodatum(defaultValue);
		if (target.equalsIgnoreCase(Constants.fusobacteriumNucleatum))
			oe.setFusobacteriumNucleatum(value);
		else if(oe.getFusobacteriumNucleatum() == "" || oe.getFusobacteriumNucleatum() == null)
			oe.setFusobacteriumNucleatum(defaultValue);
		if (target.equalsIgnoreCase(Constants.lactobacillusSpp))
			oe.setLactobacillusSpp(value);
		else if(oe.getLactobacillusSpp() == "" || oe.getLactobacillusSpp() == null)
			oe.setLactobacillusSpp(defaultValue);
		if (target.equalsIgnoreCase(Constants.parvimonasMicra))
			oe.setParvimonasMicra(value);
		else if(oe.getParvimonasMicra() == "" || oe.getParvimonasMicra() == null)
			oe.setParvimonasMicra(defaultValue);
		if (target.equalsIgnoreCase(Constants.peptostreptococcusAnaerobius))
			oe.setPeptostreptococcusAnaerobius(value);
		else if(oe.getPeptostreptococcusAnaerobius() == "" || oe.getPeptostreptococcusAnaerobius() == null)
			oe.setPeptostreptococcusAnaerobius(defaultValue);
		if (target.equalsIgnoreCase(Constants.prevotellaIntermedia))
			oe.setPrevotellaIntermedia(value);
		else if(oe.getPrevotellaIntermedia() == "" || oe.getPrevotellaIntermedia() == null)
			oe.setPrevotellaIntermedia(defaultValue);
		if (target.equalsIgnoreCase(Constants.prevotellaNigrescens))
			oe.setPrevotellaNigrescens(value);
		else if(oe.getPrevotellaNigrescens() == "" || oe.getPrevotellaNigrescens() == null)
			oe.setPrevotellaNigrescens(defaultValue);
		if (target.equalsIgnoreCase(Constants.streptococcusMutans))
			oe.setStreptococcusMutans(value);
		else if(oe.getStreptococcusMutans() == "" || oe.getStreptococcusMutans() == null)
			oe.setStreptococcusMutans(defaultValue);
		if (target.equalsIgnoreCase(Constants.enterococcusFaecalis))
			oe.setEnterococcusFaecalis(value);
		else if(oe.getEnterococcusFaecalis() == "" || oe.getEnterococcusFaecalis() == null)
			oe.setEnterococcusFaecalis(defaultValue);

     return oe;
	}

	private MicroBiomesEntity sampleDetails(MicroBiomesEntity mbe, String key, SampleEntity se) {

		PatientEntity pe = new  PatientEntity();
		//SampleEntity se = new SampleEntity();
		// se = sampleRepository.findBySampleId(key);
		String reportStatus = "DRAFT";
		mbe.setReportStatus(reportStatus);
		 if(!Objects.isNull(se)) {
			 mbe.setTestReceived(se.getTestReceiveDate());
			 mbe.setTestReported(se.getTestReportDate());
			 mbe.setReportType(se.getTestOrdered());
			 mbe.setVaginalPh(se.getPH());
			 //mbe.setReportStatus(se.getSampleStatus());
			 pe = se.getPatientId();
			 if(!Objects.isNull(pe)) {
				 mbe.setClinicianName(pe.getClinicianName());
				 mbe.setPatientName(pe.getPatientFirstName());
				 mbe.setPatientDob(pe.getPatientDOB());
				 mbe.setGender(pe.getPatientSex());
				 mbe.setEmailid(pe.getPatientEmail());
			 }
		 }

		return mbe;
	}

	private OralEcologixReportEntity sampleDetailsOral(OralEcologixReportEntity oe, String key, SampleEntity se) {

		PatientEntity pe = new  PatientEntity();
		//SampleEntity se = new SampleEntity();
		// se = sampleRepository.findBySampleId(key);
		String reportStatus = "DRAFT";
		oe.setReportStatus(reportStatus);
		if(!Objects.isNull(se)) {
			oe.setTestReceived(se.getTestReceiveDate());
			oe.setTestReported(se.getTestReportDate());
			oe.setReportType(se.getTestOrdered());
			//oe.setUserid(se.getUserId());
			pe = se.getPatientId();
			if(!Objects.isNull(pe)) {
				oe.setClinicianName(pe.getClinicianName());
				oe.setPatientName(pe.getPatientFirstName());
				oe.setPatientdob(pe.getPatientDOB());
				oe.setGender(pe.getPatientSex());
				oe.setEmailid(pe.getPatientEmail());
			}
		}

		return oe;
	}

	private MicroBiomesEntity saveUploadDate(MicroBiomesEntity mbe, String str1, String value) {
		String defaultValue = "DL";

		if(value == null || !value.matches("^\\d+\\.\\d+"))
			value = "DL";
        if(str1.equalsIgnoreCase(Constants.candidaAlbicans))
            mbe.setCandidaAlbicans(value);
        else if(mbe.getCandidaAlbicans() == "" || mbe.getCandidaAlbicans() == null)
            mbe.setCandidaAlbicans(defaultValue);
        if (str1.equalsIgnoreCase(Constants.lactobacillusCrispatus))
            mbe.setLactobacillusCrispatus(value);
        else if(mbe.getLactobacillusCrispatus() == "" || mbe.getLactobacillusCrispatus() == null)
            mbe.setLactobacillusCrispatus(defaultValue);
        if (str1.equalsIgnoreCase(Constants.lactobacillusGasseri))
            mbe.setLactobacillusGasseri(value);
        else if(mbe.getLactobacillusGasseri() == "" || mbe.getLactobacillusGasseri() == null)
            mbe.setLactobacillusGasseri(defaultValue);
        if (str1.equalsIgnoreCase(Constants.lactobacillusIners))
            mbe.setLactobacillusIners(value);
        else if(mbe.getLactobacillusIners() == "" || mbe.getLactobacillusIners() == null)
            mbe.setLactobacillusIners(defaultValue);
        if (str1.equalsIgnoreCase(Constants.lactobacillusJensenii))
            mbe.setLactobacillusJensenii(value);
        else if(mbe.getLactobacillusJensenii() == "" || mbe.getLactobacillusJensenii() == null)
            mbe.setLactobacillusJensenii(defaultValue);
        if (str1.equalsIgnoreCase(Constants.atopobiumVaginae))
            mbe.setAtopobiumVaginae(value);
        else if(mbe.getAtopobiumVaginae() == "" || mbe.getAtopobiumVaginae() == null)
            mbe.setAtopobiumVaginae(defaultValue);
        if (str1.equalsIgnoreCase(Constants.bVAB2))
            mbe.setBVAB2(value);
        else if(mbe.getBVAB2() == "" || mbe.getBVAB2() == null)
            mbe.setBVAB2(defaultValue);
        if (str1.equalsIgnoreCase(Constants.gardnerellaVaginalis))
            mbe.setGardnerellaVaginalis(value);
        else if(mbe.getGardnerellaVaginalis() == "" || mbe.getGardnerellaVaginalis() == null)
            mbe.setGardnerellaVaginalis(defaultValue);
        if (str1.equalsIgnoreCase(Constants.megasphera1))
            mbe.setMegasphera1(value);
        else if(mbe.getMegasphera1() == "" || mbe.getMegasphera1() == null)
            mbe.setMegasphera1(defaultValue);
        if (str1.equalsIgnoreCase(Constants.megasphera2))
            mbe.setMegasphera2(value);
        else if(mbe.getMegasphera2() == "" || mbe.getMegasphera2() == null)
            mbe.setMegasphera2(defaultValue);
        if (str1.equalsIgnoreCase(Constants.mobiluncusCurtisii))
            mbe.setMobiluncusCurtisii(value);
        else if(mbe.getMobiluncusCurtisii() == "" || mbe.getMobiluncusCurtisii() == null)
            mbe.setMobiluncusCurtisii(defaultValue);
        if (str1.equalsIgnoreCase(Constants.mobiluncusMulieris))
            mbe.setMobiluncusMulieris(value);
        else if(mbe.getMobiluncusMulieris() == "" || mbe.getMobiluncusMulieris() == null)
            mbe.setMobiluncusMulieris(defaultValue);
        if (str1.equalsIgnoreCase(Constants.prevotellaBivia))
            mbe.setPrevotellaBivia(value);
        else if(mbe.getPrevotellaBivia() == "" || mbe.getPrevotellaBivia() == null)
            mbe.setPrevotellaBivia(defaultValue);
        if (str1.equalsIgnoreCase(Constants.ureaplasmaUrealyticum))
            mbe.setUreaplasmaUrealyticum(value);
        else if(mbe.getUreaplasmaUrealyticum() == "" || mbe.getUreaplasmaUrealyticum() == null)
            mbe.setUreaplasmaUrealyticum(defaultValue);
        if (str1.equalsIgnoreCase(Constants.enterococcusFaecalis))
            mbe.setEnterococcusFaecalis(value);
        else if(mbe.getEnterococcusFaecalis() == "" || mbe.getEnterococcusFaecalis() == null)
            mbe.setEnterococcusFaecalis(defaultValue);
        if (str1.equalsIgnoreCase(Constants.escherichiaColi))
            mbe.setEscherichiaColi(value);
        else if(mbe.getEscherichiaColi() == "" || mbe.getEscherichiaColi() == null)
            mbe.setEscherichiaColi(defaultValue);
        if (str1.equalsIgnoreCase(Constants.staphylococcusAureus))
            mbe.setStaphylococcusAureus(value);
        else if(mbe.getStaphylococcusAureus() == "" || mbe.getStaphylococcusAureus() == null)
            mbe.setStaphylococcusAureus(defaultValue);
        if (str1.equalsIgnoreCase(Constants.streptococcusAgalactiae))
            mbe.setStreptococcusAgalactiae(value);
        else if(mbe.getStreptococcusAgalactiae() == "" || mbe.getStreptococcusAgalactiae() == null)
            mbe.setStreptococcusAgalactiae(defaultValue);
        if (str1.equalsIgnoreCase(Constants.candidaGlabrata))
            mbe.setCandidaGlabrata(value);
        else if(mbe.getCandidaGlabrata() == "" || mbe.getCandidaGlabrata() == null)
            mbe.setCandidaGlabrata(defaultValue);
        if (str1.equalsIgnoreCase(Constants.candidaKrusei))
            mbe.setCandidaKrusei(value);
        else if(mbe.getCandidaKrusei() == "" || mbe.getCandidaKrusei() == null)
            mbe.setCandidaKrusei(defaultValue);
        if (str1.equalsIgnoreCase(Constants.candidaParapsilosis))
            mbe.setCandidaParapsilosis(value);
        else if(mbe.getCandidaParapsilosis() == "" || mbe.getCandidaParapsilosis() == null)
            mbe.setCandidaParapsilosis(defaultValue);
        if (str1.equalsIgnoreCase(Constants.candidaTropicalis))
            mbe.setCandidaTropicalis(value);
        else if(mbe.getCandidaTropicalis() == "" || mbe.getCandidaTropicalis() == null)
            mbe.setCandidaTropicalis(defaultValue);
        if (str1.equalsIgnoreCase(Constants.vaginalHealthMarkersRating))
            mbe.setVaginalHealthMarkersRating(value);
        else if(mbe.getVaginalHealthMarkersRating() == "" || mbe.getVaginalHealthMarkersRating() == null)
            mbe.setVaginalHealthMarkersRating(defaultValue);
        if (str1.equalsIgnoreCase(Constants.lactobacillusCrispatusAmbudance))
            mbe.setLactobacillusCrispatusAmbudance(value);
        else if(mbe.getLactobacillusCrispatusAmbudance() == "" || mbe.getLactobacillusCrispatusAmbudance() == null)
            mbe.setLactobacillusCrispatusAmbudance(defaultValue);
        if (str1.equalsIgnoreCase(Constants.lactobacillusGasseriAmbudance))
            mbe.setLactobacillusGasseriAmbudance(value);
        else if(mbe.getLactobacillusGasseriAmbudance() == "" || mbe.getLactobacillusGasseriAmbudance() == null)
            mbe.setLactobacillusGasseriAmbudance(defaultValue);
        if (str1.equalsIgnoreCase(Constants.lactobacillusInersAmbudance))
            mbe.setLactobacillusInersAmbudance(value);
        else if(mbe.getLactobacillusInersAmbudance() == "" || mbe.getLactobacillusInersAmbudance() == null)
            mbe.setLactobacillusInersAmbudance(defaultValue);
        if (str1.equalsIgnoreCase(Constants.lactobacillusJenseniiAmbudance))
            mbe.setLactobacillusJenseniiAmbudance(value);
        else if(mbe.getLactobacillusJenseniiAmbudance() == "" || mbe.getLactobacillusJenseniiAmbudance() == null)
            mbe.setLactobacillusJenseniiAmbudance(defaultValue);
        if (str1.equalsIgnoreCase(Constants.atopobiumVaginaeAmbudance))
            mbe.setAtopobiumVaginaeAmbudance(value);
        else if(mbe.getAtopobiumVaginaeAmbudance() == "" || mbe.getAtopobiumVaginaeAmbudance() == null)
            mbe.setAtopobiumVaginaeAmbudance(defaultValue);
        if (str1.equalsIgnoreCase(Constants.bVAB2Ambudance))
            mbe.setBVAB2Ambudance(value);
        else if(mbe.getBVAB2Ambudance() == "" || mbe.getBVAB2Ambudance() == null)
            mbe.setBVAB2Ambudance(defaultValue);
        if (str1.equalsIgnoreCase(Constants.gardnerellaVaginalisAmbudance))
            mbe.setGardnerellaVaginalisAmbudance(value);
        else if(mbe.getGardnerellaVaginalisAmbudance() == "" || mbe.getGardnerellaVaginalisAmbudance() == null)
            mbe.setGardnerellaVaginalisAmbudance(defaultValue);
        if (str1.equalsIgnoreCase(Constants.megasphera1Ambudance))
            mbe.setMegasphera1Ambudance(value);
        else if(mbe.getMegasphera1Ambudance() == "" || mbe.getMegasphera1Ambudance() == null)
            mbe.setMegasphera1Ambudance(defaultValue);
        if (str1.equalsIgnoreCase(Constants.megasphera2Ambudance))
            mbe.setMegasphera2Ambudance(value);
        else if(mbe.getMegasphera2Ambudance() == "" || mbe.getMegasphera2Ambudance() == null)
            mbe.setMegasphera2Ambudance(defaultValue);
        if (str1.equalsIgnoreCase(Constants.mobiluncusCurtisiiAmbudance))
            mbe.setMobiluncusCurtisiiAmbudance(value);
        else if(mbe.getMobiluncusCurtisiiAmbudance() == "" || mbe.getMobiluncusCurtisiiAmbudance() == null)
            mbe.setMobiluncusCurtisiiAmbudance(defaultValue);
        if (str1.equalsIgnoreCase(Constants.mobiluncusMulierisAmbudance))
            mbe.setMobiluncusMulierisAmbudance(value);
        else if(mbe.getMobiluncusMulierisAmbudance() == "" || mbe.getMobiluncusMulierisAmbudance() == null)
            mbe.setMobiluncusMulierisAmbudance(defaultValue);
        if (str1.equalsIgnoreCase(Constants.prevotellaBiviaAmbudance))
            mbe.setPrevotellaBiviaAmbudance(value);
        else if(mbe.getPrevotellaBiviaAmbudance() == "" || mbe.getPrevotellaBiviaAmbudance() == null)
            mbe.setPrevotellaBiviaAmbudance(defaultValue);
        if (str1.equalsIgnoreCase(Constants.ureaplasmaUrealyticumAmbudance))
            mbe.setUreaplasmaUrealyticumAmbudance(value);
        else if(mbe.getUreaplasmaUrealyticumAmbudance() == "" || mbe.getUreaplasmaUrealyticumAmbudance() == null)
            mbe.setUreaplasmaUrealyticumAmbudance(defaultValue);
        if (str1.equalsIgnoreCase(Constants.enterococcusFaecalisAmbudance))
            mbe.setEnterococcusFaecalisAmbudance(value);
        else if(mbe.getEnterococcusFaecalisAmbudance() == "" || mbe.getEnterococcusFaecalisAmbudance() == null)
            mbe.setEnterococcusFaecalisAmbudance(defaultValue);
        if (str1.equalsIgnoreCase(Constants.escherichiaColiAmbudance))
            mbe.setEscherichiaColiAmbudance(value);
        else if(mbe.getEscherichiaColiAmbudance() == "" || mbe.getEscherichiaColiAmbudance() == null)
            mbe.setEscherichiaColiAmbudance(defaultValue);
        if (str1.equalsIgnoreCase(Constants.staphylococcusAureusAmbudance))
            mbe.setStaphylococcusAureusAmbudance(value);
        else if(mbe.getStaphylococcusAureusAmbudance() == "" || mbe.getStaphylococcusAureusAmbudance() == null)
            mbe.setStaphylococcusAureusAmbudance(defaultValue);
        if (str1.equalsIgnoreCase(Constants.streptococcusAgalactiaeAmbudance))
            mbe.setStreptococcusAgalactiaeAmbudance(value);
        else if(mbe.getStreptococcusAgalactiaeAmbudance() == "" || mbe.getStreptococcusAgalactiaeAmbudance() == null)
            mbe.setStreptococcusAgalactiaeAmbudance(defaultValue);
        if (str1.equalsIgnoreCase(Constants.candidaAlbicansAmbudance))
            mbe.setCandidaAlbicansAmbudance(value);
        else if(mbe.getCandidaAlbicansAmbudance() == "" || mbe.getCandidaAlbicansAmbudance() == null)
            mbe.setCandidaAlbicansAmbudance(defaultValue);
        if (str1.equalsIgnoreCase(Constants.candidaGlabrataAmbudance))
            mbe.setCandidaGlabrataAmbudance(value);
        else if(mbe.getCandidaGlabrataAmbudance() == "" || mbe.getCandidaGlabrataAmbudance() == null)
            mbe.setCandidaGlabrataAmbudance(defaultValue);
        if (str1.equalsIgnoreCase(Constants.candidaKruseiAmbudance))
            mbe.setCandidaKruseiAmbudance(value);
        else if(mbe.getCandidaKruseiAmbudance() == "" || mbe.getCandidaKruseiAmbudance() == null)
            mbe.setCandidaKruseiAmbudance(defaultValue);
        if (str1.equalsIgnoreCase(Constants.candidaParapsilosisAmbudance))
            mbe.setCandidaParapsilosisAmbudance(value);
        else if(mbe.getCandidaParapsilosisAmbudance() == "" || mbe.getCandidaParapsilosisAmbudance() == null)
            mbe.setCandidaParapsilosisAmbudance(defaultValue);
        if (str1.equalsIgnoreCase(Constants.candidaTropicalisAmbudance))
            mbe.setCandidaTropicalisAmbudance(value);
        else if(mbe.getCandidaTropicalisAmbudance() == "" || mbe.getCandidaTropicalisAmbudance() == null)
            mbe.setCandidaTropicalisAmbudance(defaultValue);
        if (str1.equalsIgnoreCase(Constants.megasphaeraSpp))
            mbe.setMegasphaeraSpp(value);
        else if(mbe.getMegasphaeraSpp() == "" || mbe.getMegasphaeraSpp() == null)
            mbe.setMegasphaeraSpp(defaultValue);
        if (str1.equalsIgnoreCase(Constants.veillonellaSpp))
            mbe.setVeillonellaSpp(value);
        else if(mbe.getVeillonellaSpp() == "" || mbe.getVeillonellaSpp() == null)
            mbe.setVeillonellaSpp(defaultValue);
        if (str1.equalsIgnoreCase(Constants.peptostreptococcusAnaerobius))
        	mbe.setPeptostreptococcusAnaerobius(value);
        else if(mbe.getPeptostreptococcusAnaerobius() == "" || mbe.getPeptostreptococcusAnaerobius() == null)
        	mbe.setPeptostreptococcusAnaerobius(defaultValue);
        

		return mbe;
	}

	@Override
	public List<BacteriaReportInfo> validateUpload(String jsonReport) {

		JSONObject jsnobject = new JSONObject(jsonReport);
		JSONArray jsonArray = jsnobject.getJSONArray("uploadData");
		ObjectMapper mapper = new ObjectMapper();
		String found = "" ;
		String notFound ="";
		List<BacteriaReportInfo> returnData = new ArrayList<>();
		try {

			//BacteriaReportInfo[] pp1 = mapper.readValue(jsonArray, BacteriaReportInfo[].class);
			List<BacteriaReportInfo> ppl2 = Arrays.asList(mapper.readValue(jsonArray.toString(), BacteriaReportInfo[].class));
			log.debug(ppl2.toString());

			Map<String,List<BacteriaReportInfo>> bactbysampid = new HashMap<>();

			bactbysampid = ppl2.stream() .collect(Collectors.groupingBy(BacteriaReportInfo::getSampleId));

			log.debug("grouped by sampleId: " + new JSONObject(bactbysampid));

			for(int i=0; i < bactbysampid.size(); i++ ){

				MicroBiomesEntity mbe = new MicroBiomesEntity();
				OralEcologixReportEntity oe = new OralEcologixReportEntity();

				Set<String> keys = bactbysampid.keySet();
				log.debug("Key set values are: " + keys.toArray()[i]);
				String key = keys.toArray()[i].toString();

				int btsize = bactbysampid.get(key).size();
				log.debug("size : " + btsize);

				SampleEntity se = new SampleEntity();

				se = sampleRepository.findBySampleId(key);
				if(Objects.isNull(se) || se.getTestOrdered() == null){
					notFound = notFound.concat(key).concat(" ");
					continue;
				}

				if(se.getTestOrdered().equals(Constants.femaleEcolife)) {
					mbe = microBiomesRepository.findBySampleId(key);
					if (Objects.isNull(mbe)) {
						continue;
					}

					for (int j = 0; j < btsize; j++) {
						BacteriaReportInfo bt = new BacteriaReportInfo();
						bt = bactbysampid.get(key).get(j);
						String target = bt.getTargetName();
						String lowerTarget = target.replaceAll("[^a-zA-Z0-9]", "").toLowerCase(Locale.ROOT);

						log.debug("lowerTarget values are: " + lowerTarget);

						String value = bt.getDeltaCtMean();

						validateUploadData(mbe, lowerTarget, bt, returnData);

					}
					log.debug(returnData.toString());
				}
				else if(se.getTestOrdered().equals(Constants.oralEcolife)){
					oe = oralEcologixRepository.findBySampleId(key);
					if (Objects.isNull(oe)) {
						continue;
					}

					for (int j = 0; j < btsize; j++) {
						BacteriaReportInfo bt = new BacteriaReportInfo();
						bt = bactbysampid.get(key).get(j);
						String target = bt.getTargetName();
						String lowerTarget = target.replaceAll("[^a-zA-Z0-9]", "").toLowerCase(Locale.ROOT);

						log.debug("lowerTarget values are: " + lowerTarget);

						String value = bt.getDeltaCtMean();

						validateUploadDataOral(oe, lowerTarget, bt, returnData);

					}
					log.debug(returnData.toString());
				}else {
					notFound = notFound.concat(key).concat(" ");
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		return returnData;
	}


	private List<BacteriaReportInfo> validateUploadDataOral(OralEcologixReportEntity oe, String target, BacteriaReportInfo br, List<BacteriaReportInfo> returnData) {
		String regex = "\\d*\\.?\\d*";

		if (target.equalsIgnoreCase(Constants.candidaAlbicans)) {
			if (oe.getCandidaAlbicans() != null) {
				if (oe.getCandidaAlbicans().matches(regex)) {
					br.setValidation(true);
					returnData.add(br);
				}
			}
		}
		else if (target.equalsIgnoreCase(Constants.aggregatibacterActinomycetemcomitans)) {
			if (oe.getAggregatibacterActinomycetemcomitans() != null) {
				if (oe.getAggregatibacterActinomycetemcomitans().matches(regex)) {
					br.setValidation(true);
					returnData.add(br);
				}
			}
		}
		else if (target.equalsIgnoreCase(Constants.porphyromonasGingivalis)) {
			if (oe.getPorphyromonasGingivalis() != null) {
				if (oe.getPorphyromonasGingivalis().matches(regex)) {
					br.setValidation(true);
					returnData.add(br);
				}
			}
		}
		else if (target.equalsIgnoreCase(Constants.tannerellaForsythia)) {
			if (oe.getTannerellaForsythia() != null) {
				if (oe.getTannerellaForsythia().matches(regex)) {
					br.setValidation(true);
					returnData.add(br);
				}
			}
		}
		else if (target.equalsIgnoreCase(Constants.treponemaDenticola)) {
			if (oe.getTreponemaDenticola() != null) {
				if (oe.getTreponemaDenticola().matches(regex)) {
					br.setValidation(true);
					returnData.add(br);
				}
			}
		}
		else if (target.equalsIgnoreCase(Constants.campylobacterRectus)) {
			if (oe.getCampylobacterRectus() != null) {
				if (oe.getCampylobacterRectus().matches(regex)) {
					br.setValidation(true);
					returnData.add(br);
				}
			}
		}
		else if (target.equalsIgnoreCase(Constants.enterococcusFaecalis)) {
			if (oe.getEnterococcusFaecalis() != null) {
				if (oe.getEnterococcusFaecalis().matches(regex)) {
					br.setValidation(true);
					returnData.add(br);
				}
			}
		}
		else if (target.equalsIgnoreCase(Constants.eubacteriumNodatum)) {
			if (oe.getEubacteriumNodatum() != null) {
				if (oe.getEubacteriumNodatum().matches(regex)) {
					br.setValidation(true);
					returnData.add(br);
				}
			}
		}
		else if (target.equalsIgnoreCase(Constants.fusobacteriumNucleatum)) {
			if (oe.getFusobacteriumNucleatum() != null) {
				if (oe.getFusobacteriumNucleatum().matches(regex)) {
					br.setValidation(true);
					returnData.add(br);
				}
			}
		}
		else if (target.equalsIgnoreCase(Constants.lactobacillusSpp)) {
			if (oe.getLactobacillusSpp() != null) {
				if (oe.getLactobacillusSpp().matches(regex)) {
					br.setValidation(true);
					returnData.add(br);
				}
			}
		}
		else if (target.equalsIgnoreCase(Constants.parvimonasMicra)) {
			if (oe.getParvimonasMicra() != null) {
				if (oe.getParvimonasMicra().matches(regex)) {
					br.setValidation(true);
					returnData.add(br);
				}
			}
		}
		else if (target.equalsIgnoreCase(Constants.peptostreptococcusAnaerobius)) {
			if (oe.getPeptostreptococcusAnaerobius() != null) {
				if (oe.getPeptostreptococcusAnaerobius().matches(regex)) {
					br.setValidation(true);
					returnData.add(br);
				}
			}
		}
		else if (target.equalsIgnoreCase(Constants.prevotellaIntermedia)) {
			if (oe.getPrevotellaIntermedia() != null) {
				if (oe.getPrevotellaIntermedia().matches(regex)) {
					br.setValidation(true);
					returnData.add(br);
				}
			}
		}
		else if (target.equalsIgnoreCase(Constants.prevotellaNigrescens)) {
			if (oe.getPrevotellaNigrescens() != null) {
				if (oe.getPrevotellaNigrescens().matches(regex)) {
					br.setValidation(true);
					returnData.add(br);
				}
			}
		}
		else if (target.equalsIgnoreCase(Constants.streptococcusMutans)) {
			if (oe.getStreptococcusMutans() != null) {
				if (oe.getStreptococcusMutans().matches(regex)) {
					br.setValidation(true);
					returnData.add(br);
				}
			}
		}
		else if (target.equalsIgnoreCase(Constants.enterococcusFaecalis)) {
			if (oe.getEnterococcusFaecalis() != null) {
				if (oe.getEnterococcusFaecalis().matches(regex)) {
					br.setValidation(true);
					returnData.add(br);
				}
			}
		}
		else{
			br.setNoBacteria(Constants.bacteriaErrorMsg);
			returnData.add(br);
		}


		return returnData;
	}

	private List<BacteriaReportInfo> validateUploadData(MicroBiomesEntity mbe, String str1, BacteriaReportInfo br, List<BacteriaReportInfo> returnData) {
		String regex = "\\d*\\.?\\d*";

		if (str1.equalsIgnoreCase(Constants.candidaAlbicans)) {
			if (mbe.getCandidaAlbicans() != null) {
				if (mbe.getCandidaAlbicans().matches(regex)) {
					br.setValidation(true);
					returnData.add(br);
				}
			}
		}
		else if(str1.equalsIgnoreCase(Constants.lactobacillusCrispatus)){
			if (mbe.getCandidaAlbicans() != null) {
				if (mbe.getLactobacillusCrispatus().matches(regex)) {
					br.setValidation(true);
					returnData.add(br);
				}
			}
		}
		else if(str1.equalsIgnoreCase(Constants.lactobacillusGasseri)) {
			if (mbe.getLactobacillusGasseri() != null) {
				if (mbe.getLactobacillusGasseri().matches(regex)) {
					br.setValidation(true);
					returnData.add(br);
				}
			}
		}
		else if(str1.equalsIgnoreCase(Constants.lactobacillusIners)) {
			if (mbe.getLactobacillusIners() != null) {
				if (mbe.getLactobacillusIners().matches(regex)) {
					br.setValidation(true);
					returnData.add(br);
				}
			}
		}
		else if(str1.equalsIgnoreCase(Constants.lactobacillusJensenii)) {
			if (mbe.getLactobacillusJensenii() != null) {
				if (mbe.getLactobacillusJensenii().matches(regex)) {
					br.setValidation(true);
					returnData.add(br);
				}
			}
		}
		else if(str1.equalsIgnoreCase(Constants.atopobiumVaginae)) {
			if (mbe.getAtopobiumVaginae() != null) {
				if (mbe.getAtopobiumVaginae().matches(regex)) {
					br.setValidation(true);
					returnData.add(br);
				}
			}
		}
		else if(str1.equalsIgnoreCase(Constants.bVAB2)) {
			if (mbe.getBVAB2() != null) {
				if (mbe.getBVAB2().matches(regex)) {
					br.setValidation(true);
					returnData.add(br);
				}
			}
		}
		else if(str1.equalsIgnoreCase(Constants.gardnerellaVaginalis)) {
			if (mbe.getGardnerellaVaginalis() != null) {
				if (mbe.getGardnerellaVaginalis().matches(regex)) {
					br.setValidation(true);
					returnData.add(br);
				}
			}
		}
		else if(str1.equalsIgnoreCase(Constants.megasphera1)) {
			if (mbe.getMegasphera1() != null) {
				if (mbe.getMegasphera1().matches(regex)) {
					br.setValidation(true);
					returnData.add(br);
				}
			}
		}
		else if(str1.equalsIgnoreCase(Constants.megasphera2)) {
			if (mbe.getMegasphera2() != null) {
				if (mbe.getMegasphera2().matches(regex)) {
					br.setValidation(true);
					returnData.add(br);
				}
			}
		}
		else if(str1.equalsIgnoreCase(Constants.mobiluncusCurtisii)) {
			if (mbe.getMobiluncusCurtisii() != null) {
				if (mbe.getMobiluncusCurtisii().matches(regex)) {
					br.setValidation(true);
					returnData.add(br);
				}
			}
		}
		else if(str1.equalsIgnoreCase(Constants.mobiluncusMulieris)) {
			if (mbe.getMobiluncusMulieris() != null) {
				if (mbe.getMobiluncusMulieris().matches(regex)) {
					br.setValidation(true);
					returnData.add(br);
				}
			}
		}
		else if(str1.equalsIgnoreCase(Constants.prevotellaBivia)) {
			if (mbe.getPrevotellaBivia() != null) {
				if (mbe.getPrevotellaBivia().matches(regex)) {
					br.setValidation(true);
					returnData.add(br);
				}
			}
		}
		else if(str1.equalsIgnoreCase(Constants.ureaplasmaUrealyticum)) {
			if (mbe.getUreaplasmaUrealyticum() != null) {
				if (mbe.getUreaplasmaUrealyticum().matches(regex)) {
					br.setValidation(true);
					returnData.add(br);
				}
			}
		}
		else if(str1.equalsIgnoreCase(Constants.enterococcusFaecalis)) {
			if (mbe.getEnterococcusFaecalis() != null) {
				if (mbe.getEnterococcusFaecalis().matches(regex)) {
					br.setValidation(true);
					returnData.add(br);
				}
			}
		}
		else if(str1.equalsIgnoreCase(Constants.escherichiaColi)) {
			if (mbe.getEscherichiaColi() != null) {
				if (mbe.getEscherichiaColi().matches(regex)) {
					br.setValidation(true);
					returnData.add(br);
				}
			}
		}
		else if(str1.equalsIgnoreCase(Constants.staphylococcusAureus)) {
			if (mbe.getStaphylococcusAureus() != null) {
				if (mbe.getStaphylococcusAureus().matches(regex)) {
					br.setValidation(true);
					returnData.add(br);
				}
			}
		}
		else if(str1.equalsIgnoreCase(Constants.streptococcusAgalactiae)) {
			if (mbe.getStreptococcusAgalactiae() != null) {
				if (mbe.getStreptococcusAgalactiae().matches(regex)) {
					br.setValidation(true);
					returnData.add(br);
				}
			}
		}
		else if(str1.equalsIgnoreCase(Constants.candidaGlabrata)) {
			if (mbe.getCandidaGlabrata() != null) {
				if (mbe.getCandidaGlabrata().matches(regex)) {
					br.setValidation(true);
					returnData.add(br);
				}
			}
		}
		else if(str1.equalsIgnoreCase(Constants.candidaKrusei)) {
			if (mbe.getCandidaKrusei() != null) {
				if (mbe.getCandidaKrusei().matches(regex)) {
					br.setValidation(true);
					returnData.add(br);
				}
			}
		}
		else if(str1.equalsIgnoreCase(Constants.candidaParapsilosis)) {
			if (mbe.getCandidaParapsilosis() != null) {
				if (mbe.getCandidaParapsilosis().matches(regex)) {
					br.setValidation(true);
					returnData.add(br);
				}
			}
		}
		else if(str1.equalsIgnoreCase(Constants.candidaTropicalis)) {
			if (mbe.getCandidaTropicalis() != null) {
				if (mbe.getCandidaTropicalis().matches(regex)) {
					br.setValidation(true);
					returnData.add(br);
				}
			}
		}
		else if(str1.equalsIgnoreCase(Constants.vaginalHealthMarkersRating)) {
			if (mbe.getVaginalHealthMarkersRating() != null) {
				if (mbe.getVaginalHealthMarkersRating().matches(regex)) {
					br.setValidation(true);
					returnData.add(br);
				}
			}
		}
		else if(str1.equalsIgnoreCase(Constants.lactobacillusCrispatusAmbudance)) {
			if (mbe.getLactobacillusCrispatusAmbudance() != null) {
				if (mbe.getLactobacillusCrispatusAmbudance().matches(regex)) {
					br.setValidation(true);
					returnData.add(br);
				}
			}
		}
		else if(str1.equalsIgnoreCase(Constants.lactobacillusGasseriAmbudance)) {
			if (mbe.getLactobacillusGasseriAmbudance() != null) {
				if (mbe.getLactobacillusGasseriAmbudance().matches(regex)) {
					br.setValidation(true);
					returnData.add(br);
				}
			}
		}
		else if(str1.equalsIgnoreCase(Constants.lactobacillusInersAmbudance)) {
			if (mbe.getLactobacillusInersAmbudance() != null) {
				if (mbe.getLactobacillusInersAmbudance().matches(regex)) {
					br.setValidation(true);
					returnData.add(br);
				}
			}
		}
		else if(str1.equalsIgnoreCase(Constants.lactobacillusJenseniiAmbudance)) {
			if (mbe.getLactobacillusJenseniiAmbudance() != null) {
				if (mbe.getLactobacillusJenseniiAmbudance().matches(regex)) {
					br.setValidation(true);
					returnData.add(br);
				}
			}
		}

		else if(str1.equalsIgnoreCase(Constants.atopobiumVaginaeAmbudance)) {
			if (mbe.getAtopobiumVaginaeAmbudance() != null) {
				if (mbe.getAtopobiumVaginaeAmbudance().matches(regex)) {
					br.setValidation(true);
					returnData.add(br);
				}
			}
		}
		else if(str1.equalsIgnoreCase(Constants.bVAB2Ambudance)) {
			if (mbe.getBVAB2Ambudance() != null) {
				if (mbe.getBVAB2Ambudance().matches(regex)) {
					br.setValidation(true);
					returnData.add(br);
				}
			}
		}
		else if(str1.equalsIgnoreCase(Constants.gardnerellaVaginalisAmbudance)) {
			if (mbe.getGardnerellaVaginalisAmbudance() != null) {
				if (mbe.getGardnerellaVaginalisAmbudance().matches(regex)) {
					br.setValidation(true);
					returnData.add(br);
				}
			}
		}
		else if(str1.equalsIgnoreCase(Constants.megasphera1Ambudance)) {
			if (mbe.getMegasphera1Ambudance() != null) {
				if (mbe.getMegasphera1Ambudance().matches(regex)) {
					br.setValidation(true);
					returnData.add(br);
				}
			}
		}
		else if(str1.equalsIgnoreCase(Constants.megasphera2Ambudance)) {
			if (mbe.getMegasphera2Ambudance() != null) {
				if (mbe.getMegasphera2Ambudance().matches(regex)) {
					br.setValidation(true);
					returnData.add(br);
				}
			}
		}
		else if(str1.equalsIgnoreCase(Constants.mobiluncusCurtisiiAmbudance)) {
			if (mbe.getMobiluncusCurtisiiAmbudance() != null) {
				if (mbe.getMobiluncusCurtisiiAmbudance().matches(regex)) {
					br.setValidation(true);
					returnData.add(br);
				}
			}
		}
		else if(str1.equalsIgnoreCase(Constants.mobiluncusMulierisAmbudance)) {
			if (mbe.getMobiluncusMulierisAmbudance() != null) {
				if (mbe.getMobiluncusMulierisAmbudance().matches(regex)) {
					br.setValidation(true);
					returnData.add(br);
				}
			}
		}
		else if(str1.equalsIgnoreCase(Constants.prevotellaBiviaAmbudance)) {
			if (mbe.getPrevotellaBiviaAmbudance() != null) {
				if (mbe.getPrevotellaBiviaAmbudance().matches(regex)) {
					br.setValidation(true);
					returnData.add(br);
				}
			}
		}
		else if(str1.equalsIgnoreCase(Constants.ureaplasmaUrealyticumAmbudance)) {
			if (mbe.getUreaplasmaUrealyticumAmbudance() != null) {
				if (mbe.getUreaplasmaUrealyticumAmbudance().matches(regex)) {
					br.setValidation(true);
					returnData.add(br);
				}
			}
		}

		else if(str1.equalsIgnoreCase(Constants.enterococcusFaecalisAmbudance)) {
			if (mbe.getEnterococcusFaecalisAmbudance() != null) {
				if (mbe.getEnterococcusFaecalisAmbudance().matches(regex)) {
					br.setValidation(true);
					returnData.add(br);
				}
			}
		}
		else if(str1.equalsIgnoreCase(Constants.escherichiaColiAmbudance)) {
			if (mbe.getEscherichiaColiAmbudance() != null) {
				if (mbe.getEscherichiaColiAmbudance().matches(regex)) {
					br.setValidation(true);
					returnData.add(br);
				}
			}
		}
		else if(str1.equalsIgnoreCase(Constants.staphylococcusAureusAmbudance)) {
			if (mbe.getStaphylococcusAureusAmbudance() != null) {
				if (mbe.getStaphylococcusAureusAmbudance().matches(regex)) {
					br.setValidation(true);
					returnData.add(br);
				}
			}
		}
		else if(str1.equalsIgnoreCase(Constants.streptococcusAgalactiaeAmbudance)) {
			if (mbe.getStreptococcusAgalactiaeAmbudance() != null) {
				if (mbe.getStreptococcusAgalactiaeAmbudance().matches(regex)) {
					br.setValidation(true);
					returnData.add(br);
				}
			}
		}
		else if(str1.equalsIgnoreCase(Constants.candidaAlbicansAmbudance)) {
			if (mbe.getCandidaAlbicansAmbudance() != null) {
				if (mbe.getCandidaAlbicansAmbudance().matches(regex)) {
					br.setValidation(true);
					returnData.add(br);
				}
			}
		}
		else if(str1.equalsIgnoreCase(Constants.candidaGlabrataAmbudance)) {
			if (mbe.getCandidaGlabrataAmbudance() != null) {
				if (mbe.getCandidaGlabrataAmbudance().matches(regex)) {
					br.setValidation(true);
					returnData.add(br);
				}
			}
		}
		else if(str1.equalsIgnoreCase(Constants.candidaKruseiAmbudance)) {
			if (mbe.getCandidaKruseiAmbudance() != null) {
				if (mbe.getCandidaKruseiAmbudance().matches(regex)) {
					br.setValidation(true);
					returnData.add(br);
				}
			}
		}
		else if(str1.equalsIgnoreCase(Constants.candidaParapsilosisAmbudance)) {
			if (mbe.getCandidaParapsilosisAmbudance() != null) {
				if (mbe.getCandidaParapsilosisAmbudance().matches(regex)) {
					br.setValidation(true);
					returnData.add(br);
				}
			}
		}
		else if(str1.equalsIgnoreCase(Constants.candidaTropicalisAmbudance)) {
			if (mbe.getCandidaTropicalisAmbudance() != null) {
				if (mbe.getCandidaTropicalisAmbudance().matches(regex)) {
					br.setValidation(true);
					returnData.add(br);
				}
			}
		} 
		else if(str1.equalsIgnoreCase(Constants.megasphaeraSpp)) {
			if (mbe.getMegasphaeraSpp() != null) {
				if (mbe.getMegasphaeraSpp().matches(regex)) {
					br.setValidation(true);
					returnData.add(br);
				}
			}
		} 
		else if(str1.equalsIgnoreCase(Constants.veillonellaSpp)) {
			if (mbe.getVeillonellaSpp() != null) {
				if (mbe.getVeillonellaSpp().matches(regex)) {
					br.setValidation(true);
					returnData.add(br);
				}
			}
		} 
		else if(str1.equalsIgnoreCase(Constants.peptostreptococcusAnaerobius)) {
			if (mbe.getPeptostreptococcusAnaerobius() != null) {
				if (mbe.getPeptostreptococcusAnaerobius().matches(regex)) {
					br.setValidation(true);
					returnData.add(br);
				}
			}
		} else{
			br.setNoBacteria(Constants.bacteriaErrorMsg);
			returnData.add(br);
		}

		return  returnData;
	}

}
