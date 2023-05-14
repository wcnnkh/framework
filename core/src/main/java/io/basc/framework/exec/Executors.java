package io.basc.framework.exec;

import io.basc.framework.util.Elements;

public interface Executors extends Executable {
	Elements<? extends Executor> getElements();
}