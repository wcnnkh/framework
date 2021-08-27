package scw.test;

import io.basc.framework.core.type.scanner.ClassPathClassScanner;
import io.basc.framework.core.utils.ClassUtils;

import java.util.Set;

import org.junit.runner.JUnitCore;

public class TestRunner {
	public static void main(String[] args) {
		Set<Class<?>> classes = ClassPathClassScanner.INSTANCE.getClasses(ClassUtils.getPackageName(TestRunner.class), TestRunner.class.getClassLoader(), null);
		JUnitCore.runClasses(classes.toArray(new Class<?>[0]));
	}
}
