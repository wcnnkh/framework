package scw.mvc.servlet.page;

import java.util.Enumeration;
import java.util.Map;

import scw.mvc.Channel;
import scw.mvc.page.AbstractPage;
import scw.mvc.servlet.ServletChannel;
import scw.mvc.servlet.ServletRequest;
import scw.mvc.servlet.ServletResponse;
import scw.servlet.ServletUtils;

public class Jsp extends AbstractPage {
	private static final long serialVersionUID = 1L;

	protected Jsp() {
		super(null);
	};

	public Jsp(String page) {
		super(page);
	}	

	@SuppressWarnings("rawtypes")
	public void reader(Channel channel) throws Throwable {
		ServletChannel httpChannel = (ServletChannel) channel; 
		scw.mvc.servlet.ServletRequest request = (ServletRequest) httpChannel.getRequest();
		scw.mvc.servlet.ServletResponse response = (ServletResponse)httpChannel.getResponse();
		
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
		
		if(channel.isLogEnabled()){
			channel.log("jsp:{}", getPage());
		}
		ServletUtils.jsp(request, response, getPage());
	}
}
