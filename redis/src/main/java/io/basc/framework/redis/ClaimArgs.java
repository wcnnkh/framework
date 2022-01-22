package io.basc.framework.redis;

import java.io.Serializable;

public class ClaimArgs implements Serializable {
	private static final long serialVersionUID = 1L;
	private Long idle;
	private Long time;
	private Integer retryCount;
	private boolean force;
	private boolean justId;

	public ClaimArgs idel(long idelMs) {
		this.idle = idelMs;
		return this;
	}

	public ClaimArgs time(long msUnixTime) {
		this.time = msUnixTime;
		return this;
	}

	public ClaimArgs retryCount(Integer retryCount) {
		this.retryCount = retryCount;
		return this;
	}

	public ClaimArgs setForce(boolean force) {
		this.force = force;
		return this;
	}

	public ClaimArgs force() {
		return setForce(true);
	}

	public ClaimArgs justId(boolean justId) {
		this.justId = justId;
		return this;
	}

	public ClaimArgs justId() {
		return justId(true);
	}

	public Long getIdle() {
		return idle;
	}

	public Long getTime() {
		return time;
	}

	public Integer getRetryCount() {
		return retryCount;
	}

	public boolean isForce() {
		return force;
	}

	public boolean isJustId() {
		return justId;
	}
}