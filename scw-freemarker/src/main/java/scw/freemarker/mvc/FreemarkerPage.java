package scw.freemarker.mvc;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import scw.http.server.ServerHttpRequest;
import scw.http.server.ServerHttpResponse;
import scw.logger.Logger;
import scw.logger.LoggerFactory;
import scw.mvc.HttpChannel;
import scw.mvc.page.AbstractPage;
import scw.net.MimeType;
import scw.net.MimeTypeUtils;

public class FreemarkerPage extends AbstractPage {
	private static final long serialVersionUID = 1L;
	private static Logger logger = LoggerFactory.getLogger(FreemarkerPage.class);

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

	@Override
	protected void renderInternal(HttpChannel httpChannel) throws IOException {
		ServerHttpRequest serverRequest = httpChannel.getRequest();
		ServerHttpResponse serverResponse = httpChannel.getResponse();

		if (getMimeType() != null) {
			serverResponse.setContentType(getMimeType());
		} else {
			serverResponse.setContentType(MimeTypeUtils.TEXT_HTML);
		}

		Map<String, Object> freemarkerMap = new HashMap<String, Object>(size());
		Enumeration<String> enumeration = httpChannel.getRequest().getAttributeNames();
		while (enumeration.hasMoreElements()) {
			String key = enumeration.nextElement();
			if (key == null || containsKey(key)) {
				continue;
			}

			freemarkerMap.put(key, httpChannel.getRequest().getAttribute(key));
		}

		freemarkerMap.putAll(this);
		String page = getPage();
		Template template = configuration.getTemplate(page, serverRequest.getCharacterEncoding());
		try {
			template.process(freemarkerMap, serverResponse.getWriter());
		} catch (TemplateException e) {
			logger.error(e, "freemarker:{}", page);
		}
	}
}
