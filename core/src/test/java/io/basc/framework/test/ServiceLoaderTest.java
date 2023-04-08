package io.basc.framework.test;

import org.junit.Test;

import io.basc.framework.util.ConfigurableServiceLoader1;
import io.basc.framework.util.XUtils;

public class ServiceLoaderTest {
	@Test
	public void test() {
		ConfigurableServiceLoader1<String> serviceLoader = new ConfigurableServiceLoader1<>();
		serviceLoader.registerService(XUtils.getUUID());
		serviceLoader.registerService(XUtils.getUUID());
		serviceLoader.setLast(XUtils.getUUID());
		System.out.println(serviceLoader);
	}
}
