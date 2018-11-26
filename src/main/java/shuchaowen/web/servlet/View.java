package shuchaowen.web.servlet;

import java.io.IOException;

public interface View {
	public void render(WebRequest request, WebResponse response) throws IOException;
}
