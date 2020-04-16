package scw.freemarker;

import freemarker.cache.TemplateLoader;

public abstract class AbstractConfigurationTemplateLoader extends
		AbstractTemplateLoaderWrapper {
	protected volatile TemplateLoader templateLoader;

	@Override
	public TemplateLoader getTemplateLoader() {
		if (templateLoader == null) {
			synchronized (this) {
				if (templateLoader == null) {
					templateLoader = createTemplateLoader();
				}
			}
		}
		return templateLoader;
	}

	protected abstract TemplateLoader createTemplateLoader();
}
