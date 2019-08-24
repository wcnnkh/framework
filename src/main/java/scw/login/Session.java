package scw.login;

import java.io.Serializable;

public final class Session implements Serializable {
	private static final long serialVersionUID = 1L;
	private String id;
	private String uid;

	@SuppressWarnings("unused")
	private Session() {
	}

	protected Session(String id, String uid) {
		this.id = id;
		this.uid = uid;
	}

	public String getId() {
		return id;
	}

	public String getUid() {
		return uid;
	}

	public long getLongUid() {
		return Long.parseLong(getUid());
	}

	public int getIntegerUid() {
		return Integer.parseInt(getUid());
	}
}
