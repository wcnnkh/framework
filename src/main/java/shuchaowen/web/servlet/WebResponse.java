package shuchaowen.web.servlet;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import shuchaowen.core.http.enums.ContentType;
import shuchaowen.core.http.server.Response;

public class WebResponse extends HttpServletResponseWrapper implements Response{
	private WebRequest request;

	public WebResponse(WebRequest request, HttpServletResponse httpServletResponse) {
		super(httpServletResponse);
		this.request = request;
	}

	public WebRequest getRequest() {
		return request;
	}

	public void write(Object obj) throws IOException{
		if (obj != null) {
			if (obj instanceof View) {
				((View) obj).render(this);
			} else {
				if (getContentType() == null) {
					setContentType(ContentType.TEXT_HTML.getValue());
				}

				getWriter().write(obj.toString());
			}
		}
	}
}
