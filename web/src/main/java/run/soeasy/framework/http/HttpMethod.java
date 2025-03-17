package run.soeasy.framework.http;

public enum HttpMethod {
	CONNECT, DELETE, GET, HEAD, OPTIONS, PATCH, POST, PUT, TRACE;

	public boolean hasRequestBody() {
		switch (this) {
		case DELETE:
		case POST:
		case PUT:
		case PATCH:
			return true;
		default:
			return false;
		}
	}

	public boolean hasResponseBody() {
		switch (this) {
		case CONNECT:
		case HEAD:
		case OPTIONS:
			return false;
		default:
			return true;
		}
	}

	public boolean matches(String method) {
		return matches(resolve(method));
	}

	public boolean matches(HttpMethod method) {
		return (this == method);
	}

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
		return HttpMethod.valueOf(method.toUpperCase());
	}

	public static boolean hasRequestBody(String method) {
		HttpMethod httpMethod = resolve(method);
		if (httpMethod == null) {
			return false;
		}
		return httpMethod.hasRequestBody();
	}

	public static boolean hasResponseBody(String method) {
		HttpMethod httpMethod = resolve(method);
		if (httpMethod == null) {
			return false;
		}
		return httpMethod.hasResponseBody();
	}
}
