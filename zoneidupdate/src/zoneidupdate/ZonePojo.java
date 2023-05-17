package zoneidupdate;

import java.io.Serializable;

public class ZonePojo implements  Serializable  {
	private String nextFeatureId;
	private int sequenceNumber;
	private String outletId;
	private String featureId;
	private String label;
	public String getNextFeatureId() {
		return nextFeatureId;
	}
	public void setNextFeatureId(String nextFeatureId) {
		this.nextFeatureId = nextFeatureId;
	}
	public int getSequenceNumber() {
		return sequenceNumber;
	}
	public void setSequenceNumber(int sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}
	public String getOutletId() {
		return outletId;
	}
	public void setOutletId(String outletId) {
		this.outletId = outletId;
	}
	public String getFeatureId() {
		return featureId;
	}
	public void setFeatureId(String featureId) {
		this.featureId = featureId;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	@Override
	public String toString() {
		return "ZonePojo [nextFeatureId=" + nextFeatureId + ", sequenceNumber=" + sequenceNumber + ", outletId="
				+ outletId + ", featureId=" + featureId + ", label=" + label + "]";
	}
	
	
}
