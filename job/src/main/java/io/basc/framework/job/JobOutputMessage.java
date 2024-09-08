package io.basc.framework.job;

import io.basc.framework.io.OutputStreamSource;

public interface JobOutputMessage extends JobMessage, OutputStreamSource {
	void setJobName(String jobName);

	void setTimestamp(long timestamp);
}
