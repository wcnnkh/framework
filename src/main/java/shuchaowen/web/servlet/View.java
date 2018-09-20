package shuchaowen.web.servlet;

import java.io.IOException;

public interface View {
	public void render(WebResponse response) throws IOException;
}
