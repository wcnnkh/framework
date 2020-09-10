package scw.mvc.view;

import java.io.IOException;

import scw.mapper.MapperUtils;
import scw.mvc.HttpChannel;

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
		return MapperUtils.getMapper().toString(this);
	}
}
