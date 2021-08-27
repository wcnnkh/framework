package io.basc.framework.mvc.view;

import io.basc.framework.mvc.HttpChannel;

import java.io.IOException;

public interface View {
	void render(HttpChannel httpChannel) throws IOException;
}
