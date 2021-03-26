package scw.freemarker.mvc;

import scw.context.annotation.Provider;
import scw.core.utils.StringUtils;
import scw.http.MediaType;
import scw.net.MimeType;
import freemarker.template.Configuration;

@Provider
public class FreemarkerPageFactory extends AbstractFreemarkerPageFactory {
	private final Configuration configuration;
	private final MimeType mimeType;

	public FreemarkerPageFactory(Configuration configuration) {
		this(configuration, MediaType.TEXT_HTML);
	}

	public FreemarkerPageFactory(Configuration configuration, MimeType mimeType) {
		this.configuration = configuration;
		this.mimeType = mimeType;
	}

	public final Configuration getConfiguration() {
		return configuration;
	}

	public MimeType getMimeType() {
		return mimeType;
	}

	public boolean isSupport(String page) {
		return StringUtils.endsWithIgnoreCase(page, ".ftl")
				|| StringUtils.endsWithIgnoreCase(page, ".html");
	}
}
