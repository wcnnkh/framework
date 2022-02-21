package io.basc.framework.console.test;

import java.util.Scanner;

import io.basc.framework.console.AbstractConsoleProcessor;
import io.basc.framework.console.ConsoleBootstrap;
import io.basc.framework.console.ConsoleNavigation;
import io.basc.framework.console.ConsoleWindow;
import io.basc.framework.console.DefaultConsoleWindow;

public class ConsoleBootstrapTest {
	public static void main(String[] args) {
		ConsoleWindow<Scanner> window = new DefaultConsoleWindow<Scanner>();
		window.addProcess(new TestProcessor("测试1"));
		window.addProcess(new TestProcessor("测试2"));
		window.addProcess(new TestProcessor("测试3"));
		window.addProcess(new TestProcessor("测试4"));

		ConsoleBootstrap bootstrap = new ConsoleBootstrap(window);
		bootstrap.start();
	}

	private static class TestProcessor extends AbstractConsoleProcessor<Scanner> {

		public TestProcessor(String name) {
			super(name);
		}

		@Override
		public ConsoleNavigation<Scanner> process(ConsoleNavigation<Scanner> navigation, Scanner message) {
			return null;
		}
	}
}
