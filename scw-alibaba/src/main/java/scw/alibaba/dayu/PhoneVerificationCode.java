package scw.alibaba.dayu;

import java.io.Serializable;

/**
 * 手机号验证码信息
 * @author shuchaowen
 *
 */
public final class PhoneVerificationCode implements Serializable {
	private static final long serialVersionUID = 1L;
	private long lastSendTime;
	private String code;
	private int count;

	public long getLastSendTime() {
		return lastSendTime;
	}

	public void setLastSendTime(long lastSendTime) {
		this.lastSendTime = lastSendTime;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
}
