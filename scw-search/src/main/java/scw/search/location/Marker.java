package scw.search.location;

public class Marker extends Location {
	private static final long serialVersionUID = 1L;
	/**
	 * 标记的id
	 */
	private String id;
	private long timestamp;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
}
