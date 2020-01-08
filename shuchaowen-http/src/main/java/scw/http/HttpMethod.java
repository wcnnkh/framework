package scw.http;

public enum HttpMethod {
	CONNECT,
	DELETE,
	GET,
	HEAD,
	OPTIONS,
	PATCH,
	POST,
	PUT,
	TRACE
	;
	
	public static HttpMethod resolve(String method){
		return HttpMethod.valueOf(method);
	}
	
	public boolean matches(String method) {
		return matches(resolve(method));
	}
	
	public boolean matches(HttpMethod method) {
		return (this == method);
	}
}
