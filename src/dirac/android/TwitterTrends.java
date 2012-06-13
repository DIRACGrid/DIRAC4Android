package dirac.android;

import java.util.List;


public class TwitterTrends {

	private String as_of;
	private String created_at;
	private List<TwitterTrend> trends;
	private List<location> locations;


	public String getAs_of() {
		return as_of;
	}
	public void setAs_of(String asOf) {
		as_of = asOf;
	}
	public List<location> getLocations() {
		return locations;
	}
	public void setLocations(List<location> locations) {
		this.locations = locations;
	}
	public String getCreated_at() {
		return created_at;
	}
	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}


	public List<TwitterTrend> getTrends() {
		return trends;
	}
	public void setTrends(List<TwitterTrend> trends) {
		this.trends = trends;
	}




}
