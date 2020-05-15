package scw.freemarker.mvc;

import java.io.IOException;
import java.util.Enumeration;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import scw.mvc.page.AbstractPage;
import scw.net.MimeType;
import scw.net.MimeTypeUtils;
import scw.net.http.server.ServerHttpRequest;
import scw.net.http.server.ServerHttpResponse;
import scw.net.http.server.mvc.HttpChannel;

public class FreemarkerPage extends AbstractPage {
	private static final long serialVersionUID = 1L;
	private transient Configuration configuration;
	private MimeType mimeType;

	FreemarkerPage(Configuration configuration, String page) {
		this(configuration, page, null);
	}

	public FreemarkerPage(Configuration configuration, String page, MimeType mimeType) {
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

	public void render(HttpChannel httpChannel) throws IOException {
		ServerHttpRequest serverRequest = httpChannel.getRequest();
		ServerHttpResponse serverResponse = httpChannel.getResponse();

		if (getMimeType() != null) {
			serverResponse.setContentType(getMimeType());
		} else {
			serverResponse.setContentType(MimeTypeUtils.TEXT_HTML);
		}

		Enumeration<String> enumeration = httpChannel.getRequest().getAttributeNames();
		while (enumeration.hasMoreElements()) {
			String key = enumeration.nextElement();
			if (key == null || containsKey(key)) {
				continue;
			}

			put(key, httpChannel.getRequest().getAttribute(key));
		}

		ServerHttpRequest serverHttpRequest = (ServerHttpRequest) serverRequest;
		for (java.util.Map.Entry<String, String[]> entry : serverHttpRequest.getParameterMap().entrySet()) {
			String key = entry.getKey();
			if (key == null || containsKey(key)) {
				continue;
			}

			put(key, entry.getValue());
		}

		String page = getPage();
		Template template = configuration.getTemplate(page, serverRequest.getCharacterEncoding());
		try {
			template.process(this, serverResponse.getWriter());
			if (httpChannel.isLogEnabled()) {
				httpChannel.log("freemarker:{}", page);
			}
		} catch (TemplateException e) {
			httpChannel.getLogger().error(e, "freemarker:{}", page);
		}
	}
}
