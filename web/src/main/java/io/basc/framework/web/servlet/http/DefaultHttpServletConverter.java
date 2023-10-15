package io.basc.framework.web.servlet.http;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.basc.framework.http.MediaType;
import io.basc.framework.web.ServerHttpRequest;
import io.basc.framework.web.ServerHttpResponse;

public class DefaultHttpServletConverter implements HttpServletConverter {

	@Override
	public boolean canConvert(HttpServletRequest httpServletRequest) {
		return true;
	}

	@Override
	public ServerHttpRequest convert(HttpServletRequest httpServletRequest) {
		String contentType = httpServletRequest.getContentType();
		if (contentType != null) {
			if (contentType.contains(MediaType.MULTIPART_FORM_DATA_VALUE)) {
				return new ServletMultiPartServerHttpRequest(httpServletRequest);
			}
		}
		return new ServletServerHttpRequest(httpServletRequest);
	}

	@Override
	public boolean canConvert(HttpServletResponse httpServletResponse) {
		return true;
	}

	@Override
	public ServerHttpResponse convert(HttpServletResponse httpServletResponse) {
		return new ServletServerHttpResponse(httpServletResponse);
	}

}
