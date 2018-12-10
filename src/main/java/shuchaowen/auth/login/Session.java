package shuchaowen.auth.login;

import java.io.Serializable;

public class Session implements Serializable{
	private static final long serialVersionUID = 1L;
	private String id;
	private long uid;
	
	public Session(){}
	
	public Session(String id, long uid){
		this.id = id;
		this.uid = uid;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public long getUid() {
		return uid;
	}

	public void setUid(long uid) {
		this.uid = uid;
	}
}
