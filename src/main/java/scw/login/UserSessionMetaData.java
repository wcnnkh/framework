package scw.login;

import java.io.Serializable;

public final class UserSessionMetaData implements Serializable {
	private static final long serialVersionUID = 1L;
	private String id;
	private Object uid;

	@SuppressWarnings("unused")
	private UserSessionMetaData() {
	}

	public UserSessionMetaData(String id, String uid) {
		this.id = id;
		this.uid = uid;
	}

	public String getId() {
		return id;
	}

	public String getUid() {
		return (String) uid;
	}

	public long getLongUid() {
		return Long.parseLong(getUid());
	}

	public int getIntegerUid() {
		return Integer.parseInt(getUid());
	}
}
