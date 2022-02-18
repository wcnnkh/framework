package io.basc.framework.redis;

import io.basc.framework.data.domain.HostAndPort;

public class FailoverParams extends HostAndPort {
	private static final long serialVersionUID = 1L;
	private boolean force;
	private Long timeout;

	public boolean isForce() {
		return force;
	}

	public void setForce(boolean force) {
		this.force = force;
	}

	public Long getTimeout() {
		return timeout;
	}

	public void setTimeout(Long timeout) {
		this.timeout = timeout;
	}
}
