package scw.mvc.view;

import java.io.IOException;

import scw.mvc.HttpChannel;

public interface View {
	void render(HttpChannel httpChannel) throws IOException;
}
