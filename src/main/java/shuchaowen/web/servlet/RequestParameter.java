package shuchaowen.web.servlet;

public abstract class RequestParameter{
	private Request request;
	
	public RequestParameter(Request request){
		this.request = request;
	}

	public Request getRequest() {
		return request;
	}
}
