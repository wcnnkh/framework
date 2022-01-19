package io.basc.framework.redis;

import java.io.Serializable;

public class RestoreParams implements Serializable {
	private static final long serialVersionUID = 1L;

	private boolean replace;

	private boolean absTtl;

	private Long idleTime;

	private Long frequency;

	public RestoreParams() {
	}

	public RestoreParams(boolean replace, boolean absTtl, Long idleTime, Long frequency) {
		this.replace = replace;
		this.absTtl = absTtl;
		this.idleTime = idleTime;
		this.frequency = frequency;
	}

	public boolean isReplace() {
		return replace;
	}

	public void setReplace(boolean replace) {
		this.replace = replace;
	}

	public boolean isAbsTtl() {
		return absTtl;
	}

	public void setAbsTtl(boolean absTtl) {
		this.absTtl = absTtl;
	}

	public Long getIdleTime() {
		return idleTime;
	}

	public void setIdleTime(Long idleTime) {
		this.idleTime = idleTime;
	}

	public Long getFrequency() {
		return frequency;
	}

	public void setFrequency(Long frequency) {
		this.frequency = frequency;
	}
}
