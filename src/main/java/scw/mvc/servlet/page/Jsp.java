package scw.mvc.servlet.page;

import java.util.Enumeration;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import scw.mvc.Request;
import scw.mvc.RequestResponseModelChannel;
import scw.mvc.Response;
import scw.mvc.page.AbstractPage;
import scw.mvc.servlet.ServletUtils;

public class Jsp extends AbstractPage {
	private static final long serialVersionUID = 1L;

	protected Jsp() {
		super(null);
	};

	public Jsp(String page) {
		super(page);
	}

	@Override
	protected void render(RequestResponseModelChannel<? extends Request, ? extends Response> channel) throws Throwable {
		ServletRequest request = (ServletRequest) channel.getRequest();
		ServletResponse response = (ServletResponse) channel.getResponse();

		if (response.getContentType() == null) {
			response.setContentType("text/html;charset=" + response.getCharacterEncoding());
		}

		@SuppressWarnings("unchecked")
		Map<String, Object> attributeMap = (Map<String, Object>) clone();
		Enumeration<String> enumeration = request.getAttributeNames();
		while (enumeration.hasMoreElements()) {
			attributeMap.remove(enumeration.nextElement());
		}

		for (Entry<String, Object> entry : attributeMap.entrySet()) {
			request.setAttribute(entry.getKey(), entry.getValue());
		}

		if (channel.isLogEnabled()) {
			channel.log("jsp:{}", getPage());
		}
		ServletUtils.jsp(request, response, getPage());
	}
}
