package io.albot.lims.report.model.web;

public enum PatientEnum {
	sampleId("sample_id"),
	submittedSampleName("submitted_sample_name"),
	recordType("record_type"),
	sampleGenerateId("sample_genrate_id"),
	testOrdered("test_ordered"),
	testReceiveDate("test_receive_date"),
	testReportDate("test_report_date"),
	sampleType("sample_type"),
	sampleCollectionDate("sample_collection_date"),
	vaginalPH("vaginal_PH"),
	extractionType("extraction_type"),
	plates("plate_id"),
	sampleChose("sample_chose"),
	createdOn("created_on"),
	createdBy("created_by"),
	recentViewDate("recent_view_date"),
	userId("user_id");

    public final String label;
    private PatientEnum(String label) {
        this.label = label;
    }
}
