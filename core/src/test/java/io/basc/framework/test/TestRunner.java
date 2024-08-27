package io.basc.framework.test;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.runner.JUnitCore;

import io.basc.framework.core.type.classreading.MetadataReader;
import io.basc.framework.io.scan.PackagePatternMetadataReaderScanner;
import io.basc.framework.io.scan.TypeScanner;
import io.basc.framework.util.ClassUtils;
import io.basc.framework.util.Elements;
import io.basc.framework.util.logging.Logger;
import io.basc.framework.util.logging.LoggerFactory;

public class TestRunner {
	private static Logger logger = LoggerFactory.getLogger(TestRunner.class);

	public static void main(String[] args) throws IOException {
		long t = System.currentTimeMillis();
		TypeScanner typeScanner = new PackagePatternMetadataReaderScanner();
		Elements<MetadataReader> metadataReaders = typeScanner.scan(ClassUtils.getPackageName(TestRunner.class), null)
				.toSet();
		logger.info((System.currentTimeMillis() - t) + "ms");
		assertTrue(!metadataReaders.isEmpty());
		JUnitCore.runClasses(metadataReaders.map((e) -> ClassUtils.getClass(e.getClassMetadata().getClassName(), null))
				.filter((e) -> e != null).toArray(new Class<?>[0]));
	}
}
