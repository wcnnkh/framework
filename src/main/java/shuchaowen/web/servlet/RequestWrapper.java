package shuchaowen.web.servlet;

public abstract class RequestWrapper{
	private WebRequest request;
	
	public RequestWrapper(WebRequest request){
		this.request = request;
	}

	public WebRequest getRequest() {
		return request;
	}
}
