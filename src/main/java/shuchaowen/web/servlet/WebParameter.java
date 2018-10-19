package shuchaowen.web.servlet;

public abstract class WebParameter{
	private WebRequest request;
	
	public WebParameter(WebRequest request){
		this.request = request;
	}

	public WebRequest getRequest() {
		return request;
	}
}
