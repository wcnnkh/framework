package scw.servlet;

import java.io.IOException;

public interface View {
	public void render(Request request, Response response) throws IOException;
}
