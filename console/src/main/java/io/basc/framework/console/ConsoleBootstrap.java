package io.basc.framework.console;

import java.util.Scanner;

import io.basc.framework.util.Assert;

public class ConsoleBootstrap extends Thread {
	private final ConsoleNavigation<Scanner> navigation;
	private final Scanner scanner;

	public ConsoleBootstrap(ConsoleWindow<Scanner> window) {
		this(new ConsoleNavigation<>(window));
	}

	public ConsoleBootstrap(ConsoleWindow<Scanner> window, Scanner scanner) {
		this(new ConsoleNavigation<Scanner>(null, window), scanner);
	}

	public ConsoleBootstrap(ConsoleNavigation<Scanner> navigation) {
		this(navigation, new Scanner(System.in));
	}

	public ConsoleBootstrap(ConsoleNavigation<Scanner> navigation, Scanner scanner) {
		Assert.requiredArgument(navigation != null, "navigation");
		Assert.requiredArgument(scanner != null, "scanner");
		this.navigation = navigation;
		this.scanner = scanner;
	}

	public ConsoleNavigation<Scanner> getNavigation() {
		return navigation;
	}

	@Override
	public void run() {
		process(getNavigation());
	}

	protected void process(ConsoleNavigation<Scanner> navigation) {
		System.out.println("---------------------------控制台---------------------------");
		for (ConsoleProcessor<Scanner> processor : navigation.getProcessors()) {
			// 不做非空验证
			System.out.println(processor.getPattern() + ": " + processor.getName());
		}
		System.out.println("请输入要执行的匹配符: ");
		String pattern = this.scanner.next();
		ConsoleProcessor<Scanner> processor = navigation.getProcessor(pattern);
		if (processor == null) {
			reset(navigation, null);
			return;
		}

		try {
			ConsoleNavigation<Scanner> next = processor.process(navigation, this.scanner);
			if (next == null) {
				process(navigation);
				return;
			}
			process(next);
		} catch (Throwable e) {
			reset(navigation, e);
		}
	}

	protected void reset(ConsoleNavigation<Scanner> navigation, Throwable e) {
		System.out.println("执行失败!");
		if (e != null) {
			e.printStackTrace();
		}
		process(navigation);
	}
}
