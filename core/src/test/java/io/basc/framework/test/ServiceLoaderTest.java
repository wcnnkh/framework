package io.basc.framework.test;

import org.junit.Test;

import io.basc.framework.util.Services;
import io.basc.framework.util.XUtils;

public class ServiceLoaderTest {
	@Test
	public void test() {
		Services<String> serviceLoader = new Services<>();
		serviceLoader.registerService(XUtils.getUUID());
		serviceLoader.registerService(XUtils.getUUID());
		serviceLoader.setLast(XUtils.getUUID());
		System.out.println(serviceLoader);
	}
}
