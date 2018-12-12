package shuchaowen.tencent.weixin.miniprogram.bean;

import java.io.Serializable;

public final class Session implements Serializable{
	private static final long serialVersionUID = 1L;
	private String openid;
	private String session_key;
	private String unionid;
	
	public Session(){};
	
	public Session(String openid, String session_key, String unionid){
		this.session_key = session_key;
		this.unionid = unionid;
		this.openid = openid;
	}
	
	/**
	 * @return the openid
	 */
	public String getOpenid() {
		return openid;
	}
	/**
	 * @param openid the openid to set
	 */
	public void setOpenid(String openid) {
		this.openid = openid;
	}
	/**
	 * @return the session_key
	 */
	public String getSession_key() {
		return session_key;
	}
	/**
	 * @param session_key the session_key to set
	 */
	public void setSession_key(String session_key) {
		this.session_key = session_key;
	}
	/**
	 * @return the unionid
	 */
	public String getUnionid() {
		return unionid;
	}
	/**
	 * @param unionid the unionid to set
	 */
	public void setUnionid(String unionid) {
		this.unionid = unionid;
	}
}
