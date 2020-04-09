package scw.net.http;

public enum HttpMethod {
	CONNECT, DELETE, GET, HEAD, OPTIONS, PATCH, POST, PUT, TRACE;

	public static StringBuilder merge(String connectionCharacter, HttpMethod... httpMethods) {
		StringBuilder sb = new StringBuilder();
		if (httpMethods != null) {
			for (int i = 0; i < httpMethods.length; i++) {
				if (i != 0) {
					sb.append(connectionCharacter);
				}
				sb.append(httpMethods[i].name());
			}
		}
		return sb;
	}

	public static HttpMethod resolve(String method) {
		return HttpMethod.valueOf(method);
	}

	public boolean matches(String method) {
		return matches(resolve(method));
	}

	public boolean matches(HttpMethod method) {
		return (this == method);
	}
}
