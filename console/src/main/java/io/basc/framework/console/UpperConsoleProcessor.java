package io.basc.framework.console;

/**
 * 返回上一级
 * 
 * @author wcnnkh
 *
 * @param <T>
 */
public class UpperConsoleProcessor<T> extends AbstractConsoleProcessor<T> {

	public UpperConsoleProcessor() {
		this("返回上一级");
	}

	public UpperConsoleProcessor(String name) {
		super(name);
	}

	@Override
	public ConsoleNavigation<T> process(ConsoleNavigation<T> navigation, T message) {
		if (navigation.hasParent()) {
			return navigation.getParent();
		}
		return null;
	}
}
