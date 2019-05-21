package scw.servlet.page;

import java.util.Enumeration;

import freemarker.template.Configuration;
import freemarker.template.Template;
import scw.core.logger.DebugLogger;
import scw.core.net.http.ContentType;
import scw.core.utils.StringUtils;
import scw.servlet.Request;
import scw.servlet.Response;

public class Freemarker extends AbstractPage {
	private static final long serialVersionUID = 1L;
	private transient Configuration configuration;
	private String contentType;

	protected Freemarker() {
		super(null);
	}

	Freemarker(Configuration configuration, String page) {
		this(configuration, page, null);
	}

	public Freemarker(Configuration configuration, String page, String contentType) {
		super(page);
		this.configuration = configuration;
		this.contentType = contentType;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public void render(Request request, Response response) throws Exception {
		if (!StringUtils.isEmpty(getContentType())) {
			response.setContentType(getContentType());
		}

		if (response.getContentType() == null) {
			response.setContentType(ContentType.TEXT_HTML);
		}

		Enumeration<String> enumeration = request.getAttributeNames();
		while (enumeration.hasMoreElements()) {
			String key = enumeration.nextElement();
			if (key == null || containsKey(key)) {
				continue;
			}

			put(key, request.getAttribute(key));
		}

		for (Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
			String key = entry.getKey();
			if (key == null || containsKey(key)) {
				continue;
			}

			put(key, entry.getValue());
		}

		if (response instanceof DebugLogger) {
			if (((DebugLogger) response).isDebugEnabled()) {
				((DebugLogger) response).debug("freemarker：" + getPage());
			}
		}
		
		Template template = configuration.getTemplate(getPage());
		template.process(this, response.getWriter());
	}
}
