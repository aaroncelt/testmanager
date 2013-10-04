package testmanager.reporting.dao.dto;

public class LabelDto {
	Integer testRunId;
	String label;

	public LabelDto() {
	}

	public LabelDto(Integer testRunId, String label) {
		this.testRunId = testRunId;
		this.label = label;
	}

	public Integer getTestRunId() {
		return testRunId;
	}

	public void setTestRunId(Integer testRunId) {
		this.testRunId = testRunId;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public String toString() {
		return "LabelDto [testRunId=" + testRunId + ", label=" + label + "]";
	}

}
