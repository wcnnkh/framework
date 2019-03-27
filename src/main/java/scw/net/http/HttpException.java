package scw.net.http;

public class HttpException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public HttpException(String url, int code, String message, Throwable e) {
		super(new StringBuilder().append("request[").append(url)
				.append("] response code:").append(code)
				.append(",message:").append(message).toString(), e);
	}
}
