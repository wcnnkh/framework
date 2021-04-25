package scw.upload;

import java.io.IOException;

import scw.context.annotation.Provider;
import scw.core.Ordered;
import scw.http.HttpMethod;
import scw.http.HttpStatus;
import scw.http.server.HttpServiceHandler;
import scw.http.server.HttpServiceHandlerAccept;
import scw.http.server.ServerHttpRequest;
import scw.http.server.ServerHttpResponse;
import scw.http.server.cors.Cors;
import scw.logger.Logger;
import scw.logger.LoggerFactory;

@Provider(order = Ordered.LOWEST_PRECEDENCE)
public class UploadHandler implements HttpServiceHandler, HttpServiceHandlerAccept {
	private static Logger logger = LoggerFactory.getLogger(UploadHandler.class);
	private final Uploader uploader;

	public UploadHandler(Uploader uploader) {
		this.uploader = uploader;
	}

	@Override
	public void doHandle(ServerHttpRequest request, ServerHttpResponse response) throws IOException {
		String key = request.getParameterMap().getFirst("key");
		String expiration = request.getParameterMap().getFirst("expiration");
		String sign = request.getParameterMap().getFirst("sign");
		if (!uploader.getUploadPolicy().check(key, expiration, sign)) {
			response.setStatusCode(HttpStatus.FORBIDDEN);
			return;
		}

		logger.info("upload request " + request);
		Cors.DEFAULT.write(request, response.getHeaders());
		uploader.put(key, request);
		response.setStatusCode(HttpStatus.OK);
	}

	@Override
	public boolean accept(ServerHttpRequest request) {
		return request.getMethod() == HttpMethod.POST
				&& request.getPath().equals(uploader.getUploadPolicy().getController());
	}

}
