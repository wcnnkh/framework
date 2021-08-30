package io.basc.framework.swagger.test;

import io.basc.framework.boot.support.MainApplication;

public class SwaggerTest {

	public static void main(String[] args) throws InterruptedException {
		MainApplication.run(SwaggerTest.class);
		Thread.sleep(Long.MAX_VALUE);
	}
}
