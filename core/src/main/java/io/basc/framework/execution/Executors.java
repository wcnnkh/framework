package io.basc.framework.execution;

import io.basc.framework.util.Elements;

public interface Executors extends Executable {
	Elements<? extends Executor> getElements();
}