package io.basc.framework.job;

import java.util.concurrent.Future;

public interface JobExecutor {
	Future<JobInputMessage> execute(JobOutputMessage request);
}
