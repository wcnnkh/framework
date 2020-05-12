package scw.freemarker.mvc;

import java.util.Enumeration;

import scw.mvc.Channel;
import scw.mvc.ServerRequest;
import scw.mvc.ServerResponse;
import scw.mvc.http.ServerHttpRequest;
import scw.mvc.page.AbstractPage;
import scw.net.MimeType;
import scw.net.MimeTypeUtils;
import freemarker.template.Configuration;
import freemarker.template.Template;

public class FreemarkerPage extends AbstractPage {
	private static final long serialVersionUID = 1L;
	private transient Configuration configuration;
	private MimeType mimeType;

	FreemarkerPage(Configuration configuration, String page) {
		this(configuration, page, null);
	}

	public FreemarkerPage(Configuration configuration, String page,
			MimeType mimeType) {
		super(page);
		this.configuration = configuration;
		this.mimeType = mimeType;
	}

	public MimeType getMimeType() {
		return mimeType;
	}

	public void setMimeType(MimeType mimeType) {
		this.mimeType = mimeType;
	}

	public void render(Channel channel) throws Throwable {
		ServerRequest serverRequest = channel.getRequest();
		ServerResponse serverResponse = channel.getResponse();

		if (getMimeType() != null) {
			serverResponse.setContentType(getMimeType());
		} else {
			serverResponse.setContentType(MimeTypeUtils.TEXT_HTML);
		}

		Enumeration<String> enumeration = channel.getAttributeNames();
		while (enumeration.hasMoreElements()) {
			String key = enumeration.nextElement();
			if (key == null || containsKey(key)) {
				continue;
			}

			put(key, channel.getAttribute(key));
		}

		if (serverRequest instanceof ServerHttpRequest) {
			ServerHttpRequest serverHttpRequest = (ServerHttpRequest) serverRequest;
			for (Entry<String, String[]> entry : serverHttpRequest.getParameterMap()
					.entrySet()) {
				String key = entry.getKey();
				if (key == null || containsKey(key)) {
					continue;
				}

				put(key, entry.getValue());
			}
		}

		String page = getPage();
		Template template = configuration.getTemplate(page,
				serverRequest.getCharacterEncoding());
		template.process(this, serverResponse.getWriter());

		if (channel.isLogEnabled()) {
			channel.log("freemarker:{}", page);
		}
	}
}
