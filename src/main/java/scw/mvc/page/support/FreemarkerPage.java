package scw.mvc.page.support;

import java.util.Enumeration;

import freemarker.template.Configuration;
import freemarker.template.Template;
import scw.core.utils.StringUtils;
import scw.mvc.Channel;
import scw.mvc.RequestResponseModelChannel;
import scw.mvc.http.HttpRequest;
import scw.mvc.http.HttpResponse;
import scw.mvc.page.AbstractPage;
import scw.net.ContentType;

public class FreemarkerPage extends AbstractPage {
	private static final long serialVersionUID = 1L;
	private transient Configuration configuration;
	private String contentType;

	protected FreemarkerPage() {
		super(null);
	}

	FreemarkerPage(Configuration configuration, String page) {
		this(configuration, page, null);
	}

	public FreemarkerPage(Configuration configuration, String page, String contentType) {
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

	@SuppressWarnings("rawtypes")
	public void reader(Channel channel) throws Throwable {
		RequestResponseModelChannel modelChannel = (RequestResponseModelChannel) channel;
		scw.mvc.Request request = (scw.mvc.Request) modelChannel.getRequest();
		scw.mvc.Response response = (scw.mvc.Response) modelChannel.getResponse();

		if (response instanceof HttpResponse) {
			HttpResponse httpResponse = (HttpResponse) response;
			if (!StringUtils.isEmpty(getContentType())) {
				httpResponse.setContentType(getContentType());
			}

			if (httpResponse.getContentType() == null) {
				httpResponse.setContentType(ContentType.TEXT_HTML);
			}
		}

		Enumeration<String> enumeration = channel.getAttributeNames();
		while (enumeration.hasMoreElements()) {
			String key = enumeration.nextElement();
			if (key == null || containsKey(key)) {
				continue;
			}

			put(key, channel.getAttribute(key));
		}

		if (request instanceof HttpRequest) {
			HttpRequest httpRequest = (HttpRequest) request;
			for (Entry<String, String[]> entry : httpRequest.getParameterMap().entrySet()) {
				String key = entry.getKey();
				if (key == null || containsKey(key)) {
					continue;
				}

				put(key, entry.getValue());
			}
		}

		String page = getPage();
		Template template = configuration.getTemplate(page, request.getCharacterEncoding());
		template.process(this, response.getWriter());

		if (channel.isLogEnabled()) {
			channel.log("freemarker:{}", page);
		}
	}
}
