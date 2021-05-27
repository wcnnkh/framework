package scw.web.servlet.http;

import java.io.IOException;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import scw.beans.BeanFactory;
import scw.event.Observable;
import scw.web.DefaultHttpService;
import scw.web.servlet.ServletService;

public class DefaultHttpServletService extends DefaultHttpService implements ServletService {
	private final Observable<String> charsetName;
	
	public DefaultHttpServletService(BeanFactory beanFactory) {
		super(beanFactory);
		charsetName = beanFactory.getEnvironment().getObservableCharsetName();
	}

	public String getCharsetName() {
		return charsetName.get();
	}
	
	public void service(ServletRequest request, ServletResponse response)
			throws IOException {
		if(request instanceof HttpServletRequest && response instanceof HttpServletResponse){
			service((HttpServletRequest)request, (HttpServletResponse)response);
		}
	}

	public void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String charsetName = getCharsetName();
		request.setCharacterEncoding(charsetName);
		response.setCharacterEncoding(charsetName);
		ServletServerHttpRequest serverHttpRequest = new ServletServerHttpRequest(request);
		ServletServerHttpResponse serverHttpResponse = new ServletServerHttpResponse(response);
		service(serverHttpRequest, serverHttpResponse);
	}

}
