package scw.servlet.page;

import freemarker.template.Configuration;

public class FreemarkerPageFactory implements PageFactory {
	private final Configuration configuration;
	private final String contentType;

	public FreemarkerPageFactory(Configuration configuration, String contentType) {
		this.configuration = configuration;
		this.contentType = contentType;
	}

	public Page create(String page) {
		return new Freemarker(configuration, page, contentType);
	}

}
