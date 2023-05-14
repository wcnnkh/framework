package io.basc.framework.mvc.view;

import java.io.IOException;

import io.basc.framework.mvc.HttpChannel;
import lombok.Data;

@Data
public class HttpCode implements View {
	private int status;
	private String msg;

	public HttpCode(int status, String msg) {
		this.status = status;
		this.msg = msg;
	}

	public void render(HttpChannel httpChannel) throws IOException {
		httpChannel.getResponse().sendError(this.status, msg);
	}
}
