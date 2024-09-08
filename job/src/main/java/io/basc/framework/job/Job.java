package io.basc.framework.job;

public interface Job {
	void execute(JobInputMessage input, JobOutputMessage output) throws Throwable;
}
