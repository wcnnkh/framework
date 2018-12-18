package scw.tencent.weixin.bean;

import java.io.Serializable;

public final class JsApiTicket implements Serializable {
	private static final long serialVersionUID = 1L;
	private String ticket;
	private int expires_in;
	private long cts;// 创建时间

	/**
	 * 序列化用的
	 */
	public JsApiTicket() {
	}
	
	public JsApiTicket(String ticket, int expires_in){
		this.ticket = ticket;
		this.expires_in = expires_in;
		this.cts = System.currentTimeMillis();
	}

	public String getTicket() {
		return ticket;
	}

	public void setTicket(String ticket) {
		this.ticket = ticket;
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

	// 判断是否已经过期 提前5分钟过期
	public boolean isExpires() {
		return (System.currentTimeMillis() - cts) > (expires_in - 300) * 1000L;
	}
}
