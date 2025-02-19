package io.basc.framework.job;

import io.basc.framework.util.io.OutputStreamFactory;

public interface JobOutputMessage extends JobMessage, OutputStreamFactory {
	void setJobName(String jobName);

	void setTimestamp(long timestamp);
}
