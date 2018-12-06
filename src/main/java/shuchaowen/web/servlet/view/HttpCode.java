package shuchaowen.web.servlet.view;

import java.io.IOException;

import shuchaowen.core.util.Logger;
import shuchaowen.web.servlet.View;
import shuchaowen.web.servlet.Request;
import shuchaowen.web.servlet.Response;

public class HttpCode implements View {
	private int status;
	private String msg;

	public HttpCode(int status, String msg) {
		this.status = status;
		this.msg = msg;
	}

	public void render(Request request, Response response) throws IOException {
		if (response.getContentType() == null) {
			response.setContentType("text/html;charset=" + response.getCharacterEncoding());
		}

		if(response.getRequest().isDebug()){
			StringBuilder sb = new StringBuilder();
			sb.append("status=");
			sb.append(status);
			sb.append(",msg=");
			sb.append(msg);
			Logger.debug(this.getClass().getName(), sb.toString());
		}
		response.sendError(status, msg);
	}
}
