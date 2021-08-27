package io.basc.framework.web.servlet.http;

import io.basc.framework.beans.BeanFactory;
import io.basc.framework.event.Observable;
import io.basc.framework.http.MediaType;
import io.basc.framework.web.servlet.ServletService;
import io.basc.framework.web.support.DefaultHttpService;

import java.io.IOException;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DefaultHttpServletService extends DefaultHttpService implements ServletService {
	private final Observable<String> charsetName;

	public DefaultHttpServletService(BeanFactory beanFactory) {
		super(beanFactory);
		charsetName = beanFactory.getEnvironment().getObservableCharsetName();
	}

	public String getCharsetName() {
		return charsetName.get();
	}

	public void service(ServletRequest request, ServletResponse response) throws IOException {
		if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
			service((HttpServletRequest) request, (HttpServletResponse) response);
		}
	}

	public void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String charsetName = getCharsetName();
		request.setCharacterEncoding(charsetName);
		response.setCharacterEncoding(charsetName);
		ServletServerHttpRequest serverHttpRequest;
		String contentType = request.getContentType();
		if (contentType != null && contentType.contains(MediaType.MULTIPART_FORM_DATA_VALUE)) {
			serverHttpRequest = new ServletMultiPartServerHttpRequest(request);
		} else {
			serverHttpRequest = new ServletServerHttpRequest(request);
		}
		ServletServerHttpResponse serverHttpResponse = new ServletServerHttpResponse(response);
		service(serverHttpRequest, serverHttpResponse);
	}

}
