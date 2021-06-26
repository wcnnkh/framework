package scw.swagger.beans;

import java.io.IOException;

import io.swagger.v3.oas.integration.api.OpenApiContext;
import io.swagger.v3.oas.models.OpenAPI;
import scw.beans.annotation.Autowired;
import scw.context.annotation.Provider;
import scw.http.MediaType;
import scw.json.JSONUtils;
import scw.web.HttpService;
import scw.web.ServerHttpRequest;
import scw.web.ServerHttpResponse;
import scw.web.pattern.ServerHttpRequestAccept;

@Provider
public class SwaggerUiHttpService implements HttpService, ServerHttpRequestAccept {
	@Autowired
	private OpenApiContext openApiContext;

	@Override
	public boolean accept(ServerHttpRequest request) {
		return request.getPath().equals("/swagger-ui/swagger.json");
	}

	@Override
	public void service(ServerHttpRequest request, ServerHttpResponse response) throws IOException {
		OpenAPI openAPI = openApiContext.read();
		String json = JSONUtils.getJsonSupport().toJSONString(openAPI);
		response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
		response.getWriter().write(json);
	}

}
