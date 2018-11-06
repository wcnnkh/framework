package shuchaowen.web.util;

import java.io.Serializable;

import shuchaowen.core.util.XUtils;

public final class UserToken implements Serializable{
	private static final long serialVersionUID = 1L;
	private String token;
	private long uid;
	
	//用于序列化
	public UserToken(){};
	
	/**
	 * 会自动生成token
	 * @param uid
	 */
	public UserToken(long uid){
		this.uid = uid;
		this.token = XUtils.getUUID() + uid;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public long getUid() {
		return uid;
	}

	public void setUid(long uid) {
		this.uid = uid;
	}
}
