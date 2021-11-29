package io.basc.framework.test;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.runner.JUnitCore;

import io.basc.framework.io.ResourceUtils;
import io.basc.framework.io.support.PathMatchingResourcePatternResolver;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.util.ClassUtils;

public class TestRunner {
	private static Logger logger = LoggerFactory.getLogger(TestRunner.class);

	public static void main(String[] args) throws IOException {
		long t = System.currentTimeMillis();
		Set<Class<?>> classes = ResourceUtils
				.getClassesByPckageName(new PathMatchingResourcePatternResolver(),
						ClassUtils.getPackageName(TestRunner.class), TestRunner.class.getClassLoader(), null, null)
				.collect(Collectors.toSet());
		logger.info((System.currentTimeMillis() - t) + "ms");
		assertTrue(!classes.isEmpty());
		JUnitCore.runClasses(classes.toArray(new Class<?>[0]));
	}
}
