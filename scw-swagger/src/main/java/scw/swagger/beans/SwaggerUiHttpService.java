package scw.swagger.beans;

import java.io.IOException;

import io.swagger.v3.oas.integration.api.OpenApiContext;
import io.swagger.v3.oas.models.OpenAPI;
import scw.http.MediaType;
import scw.json.JSONUtils;
import scw.web.HttpService;
import scw.web.ServerHttpRequest;
import scw.web.ServerHttpResponse;

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
