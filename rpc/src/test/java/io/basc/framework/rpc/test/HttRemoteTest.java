package io.basc.framework.rpc.test;

import org.junit.Test;

import io.basc.framework.beans.support.DefaultBeanFactory;

public class HttRemoteTest {
	@Test
	public void test() throws Throwable {
		DefaultBeanFactory beanFactory = new DefaultBeanFactory();
		beanFactory.init();
		
		TestRemoteInterface test = beanFactory.getInstance(TestRemoteInterface.class);
		System.out.println(test.index());
	}
}
