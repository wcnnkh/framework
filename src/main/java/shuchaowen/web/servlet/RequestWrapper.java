package shuchaowen.web.servlet;

public abstract class RequestWrapper{
	private Request request;
	
	public RequestWrapper(Request request){
		this.request = request;
	}

	public Request getRequest() {
		return request;
	}
}
