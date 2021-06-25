package scw.swagger;

import java.util.HashSet;
import java.util.Set;

import io.swagger.v3.jaxrs2.integration.JaxrsOpenApiContextBuilder;
import io.swagger.v3.oas.integration.OpenApiConfigurationException;
import io.swagger.v3.oas.integration.SwaggerConfiguration;
import io.swagger.v3.oas.integration.api.OpenApiContext;
import io.swagger.v3.oas.models.OpenAPI;

public class SwaggerMain {
	public static void main(String[] args) throws OpenApiConfigurationException {
		SwaggerConfiguration configuration = new SwaggerConfiguration();
		Set<String> classes = new HashSet<String>();
		classes.add(ApiTest.class.getName());
		configuration = configuration.resourceClasses(classes);
		
		OpenApiContext context = new JaxrsOpenApiContextBuilder()
        .openApiConfiguration(configuration)
        .buildContext(true);
		OpenAPI openAPI = context.read();
		System.out.println(openAPI);
	}
}
