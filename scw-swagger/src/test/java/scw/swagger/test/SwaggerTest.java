package scw.swagger.test;

import io.swagger.v3.oas.integration.OpenApiConfigurationException;
import scw.boot.support.MainApplication;

public class SwaggerTest {
	
	public void test() throws OpenApiConfigurationException, InterruptedException {
		MainApplication.run(SwaggerTest.class);
		Thread.sleep(Long.MAX_VALUE);
	}
}
