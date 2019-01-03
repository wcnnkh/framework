package scw.id;

import java.io.Serializable;

public final class TimeStampId implements Serializable {
	private static final long serialVersionUID = 1L;
	private long timestamp;
	private String id;

	protected TimeStampId() {
	}

	public TimeStampId(long timestamp, String id) {
		this.timestamp = timestamp;
		this.id = id;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public String getId() {
		return id;
	}
}
