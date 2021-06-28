package scw.swagger.test;

import io.swagger.v3.oas.integration.OpenApiConfigurationException;

import org.junit.Test;

import scw.boot.support.MainApplication;

public class SwaggerMain {
	@Test
	public void test() throws OpenApiConfigurationException, InterruptedException {
		MainApplication.run(SwaggerMain.class);
		Thread.sleep(Long.MAX_VALUE);
	}
}
