package io.basc.framework.console;

import java.util.Collection;

public interface ConsoleWindow<T> {
	ConsoleProcessor<T> getProcessor(String pattern);

	Collection<ConsoleProcessor<T>> getProcessors();

	void addProcess(ConsoleProcessor<T> processor);
}
