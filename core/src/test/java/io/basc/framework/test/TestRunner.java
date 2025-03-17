package io.basc.framework.test;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.runner.JUnitCore;

import run.soeasy.framework.core.scan.PackagePatternMetadataReaderScanner;
import run.soeasy.framework.core.scan.TypeScanner;
import run.soeasy.framework.core.type.classreading.MetadataReader;
import run.soeasy.framework.util.ClassUtils;
import run.soeasy.framework.util.collections.Elements;
import run.soeasy.framework.util.logging.LogManager;
import run.soeasy.framework.util.logging.Logger;

public class TestRunner {
	private static Logger logger = LogManager.getLogger(TestRunner.class);

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
