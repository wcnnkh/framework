package io.basc.framework.http.server.jsonp;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import io.basc.framework.http.MediaType;
import io.basc.framework.http.server.ServerHttpResponse;
import io.basc.framework.http.server.ServerHttpResponse.ServerHttpResponseWrapper;
import io.basc.framework.util.Assert;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.function.Pipeline;
import io.basc.framework.util.function.Wrapped;
import lombok.NonNull;

/**
 * 使用结束后必须要调用close方法
 * 
 * @author wcnnkh
 *
 */
public class JsonpServerHttpResponse<W extends ServerHttpResponse> extends Wrapped<W>
		implements ServerHttpResponseWrapper<W> {
	private final String jsonp;
	private boolean writeJsonp = false;
	private boolean close = false;

	public JsonpServerHttpResponse(String jsonp, W targetResponse) {
		super(targetResponse);
		Assert.requiredArgument(StringUtils.isNotEmpty(jsonp), "jsonp");
		this.jsonp = jsonp;
	}

	@Override
	public @NonNull Pipeline<OutputStream, IOException> getOutputStream() {
		return ServerHttpResponseWrapper.super.getOutputStream().map((e) -> {
			writeJsonp();
			return e;
		});
	}

	@Override
	public @NonNull Pipeline<Writer, IOException> getWriter() {
		return ServerHttpResponseWrapper.super.getWriter().map((e) -> {
			writeJsonp();
			return e;
		});
	}

	private void writeJsonp() throws IOException {
		if (writeJsonp) {
			return;
		}
		getHeaders().setContentType(MediaType.TEXT_JAVASCRIPT);
		append(jsonp);
		append(JsonpUtils.JSONP_RESP_PREFIX);
		writeJsonp = true;
	}

	@Override
	public void close() throws IOException {
		if (close) {
			return;
		}

		close = true;
		if (writeJsonp) {
			append(JsonpUtils.JSONP_RESP_SUFFIX);
		}
		ServerHttpResponseWrapper.super.close();
	}
}
