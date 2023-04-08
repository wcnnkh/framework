package io.basc.framework.test;

import org.junit.Test;

import io.basc.framework.util.CacheServiceLoader;
import io.basc.framework.util.XUtils;

public class ServiceLoaderTest {
	@Test
	public void test() {
		CacheServiceLoader<String> serviceLoader = new CacheServiceLoader<>();
		serviceLoader.registerService(XUtils.getUUID());
		serviceLoader.registerService(XUtils.getUUID());
		serviceLoader.setLast(XUtils.getUUID());
		System.out.println(serviceLoader);
	}
}
