package run.soeasy.framework.http.server.jsonp;

import java.io.IOException;
import java.io.OutputStream;

import run.soeasy.framework.http.server.ServerHttpResponse;
import run.soeasy.framework.http.server.ServerHttpResponse.ServerHttpResponseWrapper;
import run.soeasy.framework.net.MediaType;
import run.soeasy.framework.util.Assert;
import run.soeasy.framework.util.StringUtils;
import run.soeasy.framework.util.function.Wrapped;

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

	public JsonpServerHttpResponse(W targetResponse, String jsonp) {
		super(targetResponse);
		Assert.requiredArgument(StringUtils.isNotEmpty(jsonp), "jsonp");
		this.jsonp = jsonp;
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		writeJsonp();
		return source.getOutputStream();
	}

	private void writeJsonp() throws IOException {
		if (writeJsonp) {
			return;
		}
		getHeaders().setContentType(MediaType.TEXT_JAVASCRIPT);
		toWriterFactory().getWriterPipeline().optional().ifPresent((writer) -> {
			writer.append(jsonp);
			writer.append(JsonpUtils.JSONP_RESP_PREFIX);
		});
		writeJsonp = true;
	}

	@Override
	public void close() throws IOException {
		if (close) {
			return;
		}

		close = true;
		if (writeJsonp) {
			toWriterFactory().getWriterPipeline().optional().ifPresent((writer) -> {
				writer.append(JsonpUtils.JSONP_RESP_SUFFIX);
			});
		}
		ServerHttpResponseWrapper.super.close();
	}
}
