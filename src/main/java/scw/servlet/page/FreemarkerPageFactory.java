package scw.servlet.page;

import java.io.File;
import java.io.IOException;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Version;
import scw.core.Constants;
import scw.core.utils.ConfigUtils;

public class FreemarkerPageFactory {
	private final Configuration configuration;
	private final String contentType;

	public FreemarkerPageFactory() throws IOException {
		this(ConfigUtils.getWorkPath());
	}

	public FreemarkerPageFactory(String rootPath) throws IOException {
		this(rootPath, null);
	}

	public FreemarkerPageFactory(String rootPath, String contentType) throws IOException {
		this.configuration = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
		configuration.setDefaultEncoding(Constants.DEFAULT_CHARSET_NAME);
		configuration.setDirectoryForTemplateLoading(new File(rootPath));
		configuration.setObjectWrapper(new DefaultObjectWrapper(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS));
		this.contentType = contentType;
	}

	public FreemarkerPageFactory(String version, String encoding, String rootPath, String contentType)
			throws IOException {
		Version v = new Version(version);
		this.configuration = new Configuration(v);
		configuration.setDefaultEncoding(encoding);
		configuration.setDirectoryForTemplateLoading(new File(rootPath));
		configuration.setObjectWrapper(new DefaultObjectWrapper(v));
		this.contentType = contentType;
	}

	public FreemarkerPageFactory(Configuration configuration) {
		this(configuration, null);
	}

	public FreemarkerPageFactory(Configuration configuration, String contentType) {
		this.configuration = configuration;
		this.contentType = contentType;
	}

	public final Configuration getConfiguration() {
		return configuration;
	}

	public String getContentType() {
		return contentType;
	}

	public Page create(String page) {
		return new FreemarkerPage(getConfiguration(), page, getContentType());
	}

}
