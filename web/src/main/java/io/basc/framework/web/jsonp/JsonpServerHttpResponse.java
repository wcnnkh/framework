package io.basc.framework.web.jsonp;

import io.basc.framework.http.MediaType;
import io.basc.framework.util.Assert;
import io.basc.framework.util.StringUtils;
import io.basc.framework.web.ServerHttpResponse;
import io.basc.framework.web.ServerHttpResponseWrapper;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

/**
 * 使用结束后必须要调用close方法
 * @author shuchaowen
 *
 */
public class JsonpServerHttpResponse extends ServerHttpResponseWrapper {
	private final String jsonp;
	private boolean writeJsonp = false;
	private boolean close = false;

	public JsonpServerHttpResponse(String jsonp,
			ServerHttpResponse targetResponse) {
		super(targetResponse);
		Assert.requiredArgument(StringUtils.isNotEmpty(jsonp), "jsonp");
		this.jsonp = jsonp;
	}

	private void writeJsonp() throws IOException {
		if (writeJsonp) {
			return;
		}
		getHeaders().setContentType(MediaType.TEXT_JAVASCRIPT);
		super.getWriter().write(jsonp);
		super.getWriter().write(JsonpUtils.JSONP_RESP_PREFIX);
		writeJsonp = true;
	}

	@Override
	public PrintWriter getWriter() throws IOException {
		writeJsonp();
		return super.getWriter();
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		writeJsonp();
		return super.getOutputStream();
	}

	@Override
	public void close() throws IOException {
		if(close){
			return ;
		}
		
		close = true;
		if (writeJsonp) {
			super.getWriter().write(JsonpUtils.JSONP_RESP_SUFFIX);
		}
		super.close();
	}
}
