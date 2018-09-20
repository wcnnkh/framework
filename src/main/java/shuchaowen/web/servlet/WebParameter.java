package shuchaowen.web.servlet;

import shuchaowen.core.http.server.Parameter;

public abstract class WebParameter extends Parameter{
	private WebRequest request;
	
	public WebParameter(WebRequest request){
		super(request);
		this.request = request;
	}

	public WebRequest getRequest() {
		return request;
	}
}
