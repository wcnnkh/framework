package scw.test;

import java.util.Set;

import org.junit.runner.JUnitCore;

import scw.core.type.scanner.ClassPathClassScanner;
import scw.core.utils.ClassUtils;

public class TestRunner {
	public static void main(String[] args) {
		Set<Class<?>> classes = ClassPathClassScanner.INSTANCE.getClasses(ClassUtils.getPackageName(TestRunner.class), TestRunner.class.getClassLoader(), null);
		JUnitCore.runClasses(classes.toArray(new Class<?>[0]));
	}
}
