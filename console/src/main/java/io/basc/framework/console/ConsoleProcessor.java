package io.basc.framework.console;

public interface ConsoleProcessor<T> {
	String getPattern();

	String getName();

	ConsoleNavigation<T> process(ConsoleNavigation<T> navigation, T message);
}
