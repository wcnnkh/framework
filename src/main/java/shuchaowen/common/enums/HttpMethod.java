package shuchaowen.common.enums;

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
	
	public static StringBuilder merge(String connectionCharacter, HttpMethod ...types){
		StringBuilder sb = new StringBuilder();
		if(types != null){
			for(int i=0; i<types.length; i++){
				if(i != 0){
					sb.append(connectionCharacter);
				}
				sb.append(types[i].name());
			}
		}
		return sb;
	}
}
