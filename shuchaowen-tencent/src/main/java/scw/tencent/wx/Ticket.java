package scw.tencent.wx;

import scw.json.JsonObject;

public final class Ticket extends BaseResponse {
	private static final long serialVersionUID = 1L;
	private String ticket;
	private int expires_in;
	private long cts;// 创建时间

	/**
	 * 序列化用的
	 */
	public Ticket() {
		super(null);
	}

	public Ticket(JsonObject json) {
		super(json);
		this.cts = System.currentTimeMillis();
		this.ticket = json.getString("ticket");
		this.expires_in = json.getIntValue("expires_in");
	}

	public String getTicket() {
		return ticket;
	}

	public int getExpires_in() {
		return expires_in;
	}

	public long getCts() {
		return cts;
	}

	// 判断是否已经过期 提前5分钟过期
	public boolean isExpires() {
		return (System.currentTimeMillis() - cts) > (expires_in - 300) * 1000L;
	}
}
