package scw.servlet.mvc.page;

import java.util.Enumeration;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import scw.mvc.Channel;
import scw.mvc.page.AbstractPage;
import scw.servlet.ServletUtils;
import scw.servlet.mvc.http.HttpServletChannel;

public class Jsp extends AbstractPage {
	private static final long serialVersionUID = 1L;

	protected Jsp() {
		super(null);
	};

	public Jsp(String page) {
		super(page);
	}

	public void render(Channel channel) throws Throwable {
		HttpServletChannel httpServletChannel = (HttpServletChannel) channel;
		ServletRequest request = httpServletChannel.getHttpServletRequest();
		ServletResponse response = httpServletChannel.getHttpServletResponse();

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
