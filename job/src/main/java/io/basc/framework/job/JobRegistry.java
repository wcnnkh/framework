package io.basc.framework.job;

import io.basc.framework.execution.Function;
import io.basc.framework.util.Registration;

public interface JobRegistry {
	Function getJob(String name);

	Registration register(String name, Function job);
}
