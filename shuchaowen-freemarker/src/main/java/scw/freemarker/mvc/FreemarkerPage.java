package scw.freemarker.mvc;

import java.util.Enumeration;

import freemarker.template.Configuration;
import freemarker.template.Template;
import scw.mvc.Channel;
import scw.mvc.Request;
import scw.mvc.Response;
import scw.mvc.http.HttpRequest;
import scw.mvc.page.AbstractPage;
import scw.net.MimeType;
import scw.net.MimeTypeUtils;

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

	public void render(Channel channel) throws Throwable {
		Request request = channel.getRequest();
		Response response = channel.getResponse();

		if(getMimeType() != null){
			response.setMimeType(getMimeType());
		}else{
			response.setMimeType(MimeTypeUtils.TEXT_HTML);
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
