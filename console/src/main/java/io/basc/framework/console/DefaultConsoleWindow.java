package io.basc.framework.console;

import java.util.Collection;
import java.util.Comparator;
import java.util.TreeMap;

import io.basc.framework.util.Assert;

public class DefaultConsoleWindow<T> implements ConsoleWindow<T> {
	private int incrId = 1;
	private final TreeMap<String, ConsoleProcessor<T>> processorMap;

	public DefaultConsoleWindow() {
		this.processorMap = new TreeMap<>();
	}

	public DefaultConsoleWindow(Comparator<String> comparator) {
		this.processorMap = new TreeMap<>();
	}

	@Override
	public ConsoleProcessor<T> getProcessor(String pattern) {
		return processorMap.get(pattern);
	}

	@Override
	public Collection<ConsoleProcessor<T>> getProcessors() {
		return processorMap.values();
	}

	@Override
	public void addProcess(ConsoleProcessor<T> processor) {
		Assert.requiredArgument(processor != null, "processor");
		if (processor instanceof AbstractConsoleProcessor && processor.getPattern() == null) {
			String id = Integer.toString(incrId++);
			while (processorMap.containsKey(id)) {
				id = Integer.toString(incrId++);
			}
			((AbstractConsoleProcessor<T>) processor).setPattern(id);
		}

		Assert.requiredArgument(processor.getPattern() != null, "ConsoleProcessor#getPattern()");
		processorMap.putIfAbsent(processor.getPattern(), processor);
	}
}
