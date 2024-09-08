package io.basc.framework.job;

public interface JobMessage {
	/**
	 * job名称
	 * 
	 * @return
	 */
	String getJobName();

	/**
	 * 时间戳
	 * 
	 * @return
	 */
	long getTimestamp();
}
