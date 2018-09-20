package shuchaowen.core.http.server;

public abstract class Parameter {
	private Request request;
	
	public Parameter(Request request){
		this.request = request;
	}

	public Request getRequest() {
		return request;
	}
}
