package io.basc.framework.swagger.beans;

import java.io.IOException;

import io.basc.framework.http.MediaType;
import io.basc.framework.json.JSONUtils;
import io.basc.framework.web.HttpService;
import io.basc.framework.web.ServerHttpRequest;
import io.basc.framework.web.ServerHttpResponse;
import io.swagger.v3.oas.integration.api.OpenApiContext;
import io.swagger.v3.oas.models.OpenAPI;

public class SwaggerUiHttpService implements HttpService {
	private final OpenApiContext openApiContext;

	public SwaggerUiHttpService(OpenApiContext openApiContext) {
		this.openApiContext = openApiContext;
	}

	public OpenApiContext getOpenApiContext() {
		return openApiContext;
	}

	@Override
	public void service(ServerHttpRequest request, ServerHttpResponse response) throws IOException {
		OpenAPI openAPI = openApiContext.read();
		String json = JSONUtils.getJsonSupport().toJSONString(openAPI);
		response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
		response.getWriter().write(json);
	}

}
