package scw.swagger.test;

import io.swagger.v3.oas.integration.OpenApiConfigurationException;

import org.junit.Test;

import scw.boot.support.MainApplication;

public class SwaggerMain {
	@Test
	public void test() throws OpenApiConfigurationException, InterruptedException {
		MainApplication.run(SwaggerMain.class);
		/*
		 * SwaggerConfiguration configuration = new SwaggerConfiguration(); Set<String>
		 * classes = new HashSet<String>(); classes.add(ApiTest.class.getName());
		 * configuration = configuration.resourceClasses(classes);
		 * 
		 * OpenApiContext context = new
		 * JaxrsOpenApiContextBuilder().openApiConfiguration(configuration)
		 * .buildContext(true); OpenAPI openAPI = context.read();
		 * System.out.println(JSONUtils.getJsonSupport().toJSONString(openAPI));
		 */

		Thread.sleep(Long.MAX_VALUE);
	}
}
