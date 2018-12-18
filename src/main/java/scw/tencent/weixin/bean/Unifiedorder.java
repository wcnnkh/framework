package scw.tencent.weixin.bean;

import java.io.Serializable;

public final class Unifiedorder implements Serializable{
	private static final long serialVersionUID = 1L;
	private long timestamp;//秒
	private String nonce_str;
	private String paySign;
	private String prepay_id;
	
	public long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	public String getNonce_str() {
		return nonce_str;
	}
	public void setNonce_str(String nonce_str) {
		this.nonce_str = nonce_str;
	}
	public String getPaySign() {
		return paySign;
	}
	public void setPaySign(String paySign) {
		this.paySign = paySign;
	}
	public String getPrepay_id() {
		return prepay_id;
	}
	public void setPrepay_id(String prepay_id) {
		this.prepay_id = prepay_id;
	}
}
