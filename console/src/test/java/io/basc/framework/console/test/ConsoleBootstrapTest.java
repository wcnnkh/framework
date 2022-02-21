package io.basc.framework.console.test;

import java.util.Scanner;

import io.basc.framework.console.AbstractConsoleProcessor;
import io.basc.framework.console.ConsoleBootstrap;
import io.basc.framework.console.ConsoleNavigation;

public class ConsoleBootstrapTest {
	public static void main(String[] args) {
		ConsoleNavigation<Scanner> next = new ConsoleNavigation<>();
		next.addProcess(new TestProcessor("下一页测试1"));
		next.addProcess(new TestProcessor("下一页测试2"));
		next.addProcess(new TestProcessor("下一页测试3"));
		next.addProcess(new TestProcessor("下一页测试4"));
		next.setName("下一页");

		ConsoleBootstrap bootstrap = new ConsoleBootstrap();
		bootstrap.addProcess(new TestProcessor("测试1"));
		bootstrap.addProcess(new TestProcessor("测试2"));
		bootstrap.addProcess(new TestProcessor("测试3"));
		bootstrap.addProcess(new TestProcessor("测试4"));
		bootstrap.addProcess(next);
		bootstrap.start();
	}

	private static class TestProcessor extends AbstractConsoleProcessor<Scanner> {

		public TestProcessor(String name) {
			super(name);
		}

		@Override
		public ConsoleNavigation<Scanner> process(ConsoleNavigation<Scanner> navigation, Scanner message) {
			return navigation;
		}
	}
}
