package shuchaowen.tencent.weixin.bean;

import java.io.Serializable;

public class AccessToken implements Serializable{
	private static final long serialVersionUID = 1L;
	private String access_token;
	private int expires_in;
	private long cts;//创建时间
	
	/**
	 * 用于序列化
	 */
	public AccessToken(){};
	
	public AccessToken(String access_token, int expires_in){
		this.cts = System.currentTimeMillis();
		this.access_token = access_token;
		this.expires_in = expires_in;
	}
	
	public String getAccess_token() {
		return access_token;
	}
	public void setAccess_token(String access_token) {
		this.access_token = access_token;
	}
	public int getExpires_in() {
		return expires_in;
	}
	public void setExpires_in(int expires_in) {
		this.expires_in = expires_in;
	}

	public long getCts() {
		return cts;
	}

	public void setCts(long cts) {
		this.cts = cts;
	}
	
	//判断是否已经过期    提前5分钟过期
	public boolean isExpires(){
		return (System.currentTimeMillis() - cts) > (expires_in - 300) * 1000L;
	}
}
