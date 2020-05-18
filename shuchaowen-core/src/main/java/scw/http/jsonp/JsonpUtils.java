package scw.http.jsonp;

import java.io.IOException;
import java.io.PrintWriter;

import scw.core.utils.StringUtils;
import scw.http.server.ServerHttpRequest;
import scw.http.server.ServerHttpResponse;
import scw.net.MimeType;
import scw.net.MimeTypeUtils;

public class JsonpUtils {
	public static final MimeType JSONP_CONTENT_TYPE = MimeTypeUtils.TEXT_JAVASCRIPT;
	private static final String JSONP_RESP_PREFIX = "(";
	private static final String JSONP_RESP_SUFFIX = ");";
	private static final String JSONP_CALLBACK = "callback";

	public static String getCallback(ServerHttpRequest request) {
		// 非GET请求不支持jsonp
		if (scw.http.HttpMethod.GET != request.getMethod()) {
			return null;
		}

		return request.getParameter(JSONP_CALLBACK);
	}

	public static void writePrefix(ServerHttpResponse response, String callback) throws IOException {
		response.setContentType(JSONP_CONTENT_TYPE);
		PrintWriter writer = response.getWriter();
		writer.write(callback);
		writer.write(JSONP_RESP_PREFIX);
	}

	public static void writeSuffix(ServerHttpResponse response, String callback) throws IOException {
		response.getWriter().write(JSONP_RESP_SUFFIX);
	}

	public static void write(ServerHttpRequest request, ServerHttpResponse response, String body) throws IOException {
		String callback = getCallback(request);
		if (StringUtils.isEmpty(callback)) {
			response.getWriter().write(body);
		} else {
			writePrefix(response, callback);
			response.getWriter().write(body);
			writeSuffix(response, callback);
		}
	}
}
