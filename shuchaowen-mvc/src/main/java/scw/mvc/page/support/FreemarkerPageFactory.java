package scw.mvc.page.support;

import java.io.File;
import java.io.IOException;

import scw.core.Constants;
import scw.core.utils.SystemPropertyUtils;
import scw.mvc.page.Page;
import scw.util.MimeType;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Version;

public class FreemarkerPageFactory {
	private final Configuration configuration;
	private final MimeType mimeType;

	public FreemarkerPageFactory() throws IOException {
		this(SystemPropertyUtils.getWorkPath());
	}

	public FreemarkerPageFactory(String rootPath) throws IOException {
		this(rootPath, null);
	}

	public FreemarkerPageFactory(String rootPath, MimeType mimeType) throws IOException {
		this.configuration = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
		configuration.setDefaultEncoding(Constants.DEFAULT_CHARSET_NAME);
		configuration.setDirectoryForTemplateLoading(new File(rootPath));
		configuration.setObjectWrapper(new DefaultObjectWrapper(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS));
		this.mimeType = mimeType;
	}

	public FreemarkerPageFactory(String version, String encoding, String rootPath, MimeType mimeType)
			throws IOException {
		Version v = new Version(version);
		this.configuration = new Configuration(v);
		configuration.setDefaultEncoding(encoding);
		configuration.setDirectoryForTemplateLoading(new File(rootPath));
		configuration.setObjectWrapper(new DefaultObjectWrapper(v));
		this.mimeType = mimeType;
	}

	public FreemarkerPageFactory(Configuration configuration) {
		this(configuration, null);
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

	public Page create(String page) {
		return new FreemarkerPage(getConfiguration(), page, getMimeType());
	}

}
