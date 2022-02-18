package io.basc.framework.redis;

import java.io.Serializable;
import java.util.List;

import io.basc.framework.data.domain.HostAndPort;

public class Slowlog implements Serializable {
	private static final long serialVersionUID = 1L;
	private final long id;
	private final long timeStamp;
	private final long executionTime;
	private final List<String> args;
	private final HostAndPort hostAndPort;
	private final String clientName;

	public Slowlog(long id, long timeStamp, long executionTime, List<String> args, HostAndPort hostAndPort,
			String clientName) {
		this.id = id;
		this.timeStamp = timeStamp;
		this.executionTime = executionTime;
		this.args = args;
		this.hostAndPort = hostAndPort;
		this.clientName = clientName;
	}

	public long getId() {
		return id;
	}

	public long getTimeStamp() {
		return timeStamp;
	}

	public long getExecutionTime() {
		return executionTime;
	}

	public List<String> getArgs() {
		return args;
	}

	public HostAndPort getHostAndPort() {
		return hostAndPort;
	}

	public String getClientName() {
		return clientName;
	}
}
