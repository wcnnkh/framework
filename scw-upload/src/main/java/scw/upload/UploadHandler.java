package scw.upload;

import java.io.IOException;

import scw.context.annotation.Provider;
import scw.core.Ordered;
import scw.http.HttpMethod;
import scw.http.HttpStatus;
import scw.web.HttpService;
import scw.web.ServerHttpRequest;
import scw.web.ServerHttpResponse;
import scw.web.cors.Cors;
import scw.web.pattern.ServerHttpRequestAccept;

@Provider(order = Ordered.LOWEST_PRECEDENCE)
public class UploadHandler implements HttpService, ServerHttpRequestAccept {
	private final Uploader uploader;

	public UploadHandler(Uploader uploader) {
		this.uploader = uploader;
	}

	@Override
	public boolean accept(ServerHttpRequest request) {
		return request.getMethod() == HttpMethod.POST
				&& request.getPath().equals(uploader.getUploadPolicy().getController());
	}

	@Override
	public void service(ServerHttpRequest request, ServerHttpResponse response) throws IOException {
		boolean b = uploader.getUploadPolicy().upload(request, uploader);
		if (b) {
			Cors.DEFAULT.write(request, response.getHeaders());
			response.setStatusCode(HttpStatus.OK);
		} else {
			response.setStatusCode(HttpStatus.FORBIDDEN);
		}
	}

}
