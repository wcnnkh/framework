package scw.net.http;

public enum Method {
	CONNECT, DELETE, GET, HEAD, OPTIONS, PATCH, POST, PUT, TRACE;

	public static StringBuilder merge(String connectionCharacter, Method... types) {
		StringBuilder sb = new StringBuilder();
		if (types != null) {
			for (int i = 0; i < types.length; i++) {
				if (i != 0) {
					sb.append(connectionCharacter);
				}
				sb.append(types[i].name());
			}
		}
		return sb;
	}

	public static Method resolve(String method) {
		return Method.valueOf(method);
	}

	public boolean matches(String method) {
		return matches(resolve(method));
	}

	public boolean matches(Method method) {
		return (this == method);
	}
}
