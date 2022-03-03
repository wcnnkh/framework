package io.basc.framework.db.data;

import java.io.Serializable;

import io.basc.framework.orm.annotation.PrimaryKey;

public class TemporaryData implements Serializable {
	private static final long serialVersionUID = 1L;
	@PrimaryKey
	private String key;
	private String value;
	private long cas;
	private long createTime;
	private long touchTime;
	// 有效期(毫秒)
	private long exp;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public long getCas() {
		return cas;
	}

	public void setCas(long cas) {
		this.cas = cas;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public long getTouchTime() {
		return touchTime;
	}

	public void setTouchTime(long touchTime) {
		this.touchTime = touchTime;
	}

	public long getExp() {
		return exp;
	}

	public void setExp(long exp) {
		this.exp = exp;
	}
}
