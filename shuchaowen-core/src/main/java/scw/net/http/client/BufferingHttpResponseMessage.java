package scw.net.http.client;

import scw.net.http.HttpHeaders;
import scw.net.http.HttpStatus;
import scw.net.message.BufferingResponseMessage;

public class BufferingHttpResponseMessage extends BufferingResponseMessage {
	private int statuCode;
	private String statusText;

	public BufferingHttpResponseMessage(byte[] body, HttpHeaders headers, int statuCode, String statusText) {
		super(body, headers);
	}

	public int getStatuCode() {
		return statuCode;
	}

	public String getStatusText() {
		return statusText;
	}

	public HttpStatus getHttpStatus() {
		return HttpStatus.valueOf(getStatuCode());
	}

	@Override
	public HttpHeaders getHeaders() {
		return (HttpHeaders) super.getHeaders();
	}
}
