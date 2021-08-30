package io.basc.framework.web.servlet.http;

import io.basc.framework.net.message.multipart.MultipartMessage;
import io.basc.framework.util.CollectionUtils;
import io.basc.framework.web.MultiPartServerHttpRequest;
import io.basc.framework.web.WebException;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;

public class ServletMultiPartServerHttpRequest extends ServletServerHttpRequest implements MultiPartServerHttpRequest {

	public ServletMultiPartServerHttpRequest(HttpServletRequest httpServletRequest) {
		super(httpServletRequest);
	}

	@Override
	public Collection<MultipartMessage> getMultipartMessages() {
		Collection<Part> parts;
		try {
			parts = getHttpServletRequest().getParts();
		} catch (IOException | ServletException e) {
			throw new WebException(e);
		}

		if (CollectionUtils.isEmpty(parts)) {
			return Collections.emptyList();
		}

		return parts.stream().map((p) -> new ServletMultipartMessage(p)).collect(Collectors.toList());
	}
}
