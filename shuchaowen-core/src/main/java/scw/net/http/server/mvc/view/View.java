package scw.net.http.server.mvc.view;

import java.io.IOException;

import scw.net.http.server.mvc.HttpChannel;

public interface View {
	void render(HttpChannel httpChannel) throws IOException;
}
