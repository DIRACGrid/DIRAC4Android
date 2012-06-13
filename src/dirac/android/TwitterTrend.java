package dirac.android;
public class TwitterTrend {
	private String name;
	private String url;
	private String query;
	private String events;
	private String promoted_content;

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getUrl() {
		return url;
	}
	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}
	public String getEvents() {
		return events;
	}
	public void setEvents(String events) {
		this.events = events;
	}
	public String getPromoted_content() {
		return promoted_content;
	}
	public void setPromoted_content(String promoted_content) {
		this.promoted_content = promoted_content;
	}
}