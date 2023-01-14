package io.albot.lims.report.model.web;

public enum PlatesEnum {
	platesId("plates_id"),
	platesName("plates_name"),
	recordType("plates_record_type"),
	platesGeneratedId("plates_generated_id"),
	platesStatus("plates_status"),
	createdDate("createdDate"),
	recentViewDate("recent_view_date"),
	userId("user_id");

    public final String label;
    private PlatesEnum(String label) {
        this.label = label;
    }

}
