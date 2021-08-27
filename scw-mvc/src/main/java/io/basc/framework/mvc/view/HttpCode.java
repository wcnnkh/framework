package io.basc.framework.mvc.view;

import io.basc.framework.mapper.MapperUtils;
import io.basc.framework.mvc.HttpChannel;

import java.io.IOException;

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

	@Override
	public String toString() {
		return MapperUtils.toString(this);
	}
}
