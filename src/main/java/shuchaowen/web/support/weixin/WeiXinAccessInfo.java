package shuchaowen.web.support.weixin;

import java.io.Serializable;

public class WeiXinAccessInfo implements Serializable{
	private static final long serialVersionUID = 1L;
	private String access;
	private int expires;
	private long refreshTime;
	
	public WeiXinAccessInfo(){
		this.refreshTime = System.currentTimeMillis();
	}
	
	public String getAccess() {
		return access;
	}
	public void setAccess(String access) {
		this.access = access;
	}
	public int getExpires() {
		return expires;
	}
	
	public void setExpires(int expires) {
		this.expires = expires;
	}
	
	public long getRefreshTime() {
		return refreshTime;
	}
	
	public void setRefreshTime(long refreshTime) {
		this.refreshTime = refreshTime;
	}

	public boolean isExpires(){
		return (System.currentTimeMillis() - refreshTime) > (expires - 100) * 1000L;
	}
}
