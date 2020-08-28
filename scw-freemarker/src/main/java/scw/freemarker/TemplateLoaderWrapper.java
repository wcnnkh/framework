package scw.freemarker;

import freemarker.cache.TemplateLoader;

public class TemplateLoaderWrapper extends AbstractTemplateLoaderWrapper {
	private final TemplateLoader templateLoader;

	public TemplateLoaderWrapper(TemplateLoader templateLoader) {
		this.templateLoader = templateLoader;
	}

	@Override
	public TemplateLoader getTemplateLoader() {
		return templateLoader;
	}

}
