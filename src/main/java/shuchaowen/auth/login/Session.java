package shuchaowen.auth.login;

import java.io.Serializable;

public class Session implements Serializable{
	private static final long serialVersionUID = 1L;
	private String id;
	private String uid;
	
	public Session(){}
	
	public Session(String id, String uid){
		this.id = id;
		this.uid = uid;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}
	
	public long getUidToLong(){
		return Long.parseLong(uid);
	}
}
