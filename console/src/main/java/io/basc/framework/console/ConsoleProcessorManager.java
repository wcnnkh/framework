package io.basc.framework.console;

import java.util.Collection;

public interface ConsoleProcessorManager {
	ConsoleProcessor getProcessor(String id);

	Collection<ConsoleProcessor> getProcessors();
}
