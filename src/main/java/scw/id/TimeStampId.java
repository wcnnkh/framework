package scw.id;

import java.io.Serializable;

public class TimeStampId implements Serializable{
	private static final long serialVersionUID = 1L;
	private long cts;
	private String id;
	
	protected TimeStampId(){};
	
	protected TimeStampId(long cts, String id){
		this.cts = cts;
		this.id = id;
	}

	public long getCts() {
		return cts;
	}

	public String getId() {
		return id;
	}
}
