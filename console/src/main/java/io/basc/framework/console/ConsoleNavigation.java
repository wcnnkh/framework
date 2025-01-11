package io.basc.framework.console;

import java.util.Collection;

import io.basc.framework.util.Assert;

public class ConsoleNavigation<T> extends AbstractConsoleProcessor<T> implements ConsoleWindow<T> {
	private final ConsoleNavigation<T> parent;
	private final ConsoleWindow<T> window;

	public ConsoleNavigation() {
		this(new DefaultConsoleWindow<>());
	}

	public ConsoleNavigation(ConsoleWindow<T> window) {
		this(null, window);
	}

	public ConsoleNavigation(ConsoleNavigation<T> parent, ConsoleWindow<T> window) {
		super(null);
		Assert.requiredArgument(window != null, "window");
		this.parent = parent;
		this.window = window;
	}

	public boolean hasParent() {
		return parent != null;
	}

	public ConsoleNavigation<T> getParent() {
		return parent;
	}

	public ConsoleWindow<T> getWindow() {
		return window;
	}

	@Override
	public ConsoleProcessor<T> getProcessor(String pattern) {
		return window.getProcessor(pattern);
	}

	@Override
	public Collection<ConsoleProcessor<T>> getProcessors() {
		return window.getProcessors();
	}

	@Override
	public void addProcess(ConsoleProcessor<T> processor) {
		window.addProcess(processor);
	}

	@Override
	public ConsoleNavigation<T> process(ConsoleNavigation<T> navigation, T message) {
		return new ConsoleNavigation<>(navigation, this.window);
	}
}
