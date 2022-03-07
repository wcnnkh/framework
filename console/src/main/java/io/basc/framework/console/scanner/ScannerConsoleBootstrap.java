package io.basc.framework.console.scanner;

import java.util.Collection;
import java.util.Scanner;

import io.basc.framework.console.ConsoleNavigation;
import io.basc.framework.console.ConsoleProcessor;
import io.basc.framework.console.ConsoleWindow;
import io.basc.framework.console.DefaultConsoleWindow;
import io.basc.framework.util.Assert;

public class ScannerConsoleBootstrap extends Thread implements ConsoleWindow<Scanner> {
	private final ConsoleNavigation<Scanner> navigation;
	private final Scanner scanner;

	public ScannerConsoleBootstrap() {
		this(new DefaultConsoleWindow<Scanner>().clear());
	}

	public ScannerConsoleBootstrap(ConsoleWindow<Scanner> window) {
		this(new ConsoleNavigation<>(window));
	}

	public ScannerConsoleBootstrap(ConsoleWindow<Scanner> window, Scanner scanner) {
		this(new ConsoleNavigation<Scanner>(null, window), scanner);
	}

	public ScannerConsoleBootstrap(ConsoleNavigation<Scanner> navigation) {
		this(navigation, new Scanner(System.in));
	}

	public ScannerConsoleBootstrap(ConsoleNavigation<Scanner> navigation, Scanner scanner) {
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
				reset(navigation, null);
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

	@Override
	public ConsoleProcessor<Scanner> getProcessor(String pattern) {
		return navigation.getProcessor(pattern);
	}

	@Override
	public Collection<ConsoleProcessor<Scanner>> getProcessors() {
		return navigation.getProcessors();
	}

	@Override
	public void addProcess(ConsoleProcessor<Scanner> processor) {
		navigation.addProcess(processor);
		;
	}
}
