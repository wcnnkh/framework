package scw.web.servlet.http;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;

import scw.core.utils.CollectionUtils;
import scw.net.message.multipart.MultipartMessage;
import scw.web.MultiPartServerHttpRequest;
import scw.web.WebException;

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
