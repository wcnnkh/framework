package io.basc.framework.freemarker;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.Version;
import io.basc.framework.beans.factory.ServiceLoaderFactory;
import io.basc.framework.beans.factory.config.Configurable;
import io.basc.framework.env.Environment;
import io.basc.framework.env.PropertyFactory;
import io.basc.framework.env.Sys;
import io.basc.framework.util.StringUtils;

public class EnvConfiguration extends Configuration implements Configurable {
	public static final String CONFIG_PROPERTY_PREFIX = "io.basc.freemarker.";
	private final TemplateLoaders templateLoaders = new TemplateLoaders();

	private final Environment env;

	public EnvConfiguration() {
		this(Sys.getEnv());
	}

	public EnvConfiguration(Environment env) {
		super(getDefaultVersion(env.getProperties()));
		this.env = env;
		setDefaultEncoding(env.getCharsetName());
		setTemplateLoader(new DefaultTemplateLoader(env.getResourceLoader()));
		setObjectWrapper(new DefaultObjectWrapper(getDefaultVersion(env.getProperties())));
	}

	public Environment getEnv() {
		return env;
	}

	private boolean configured;

	@Override
	public void configure(ServiceLoaderFactory serviceLoaderFactory) {
		templateLoaders.configure(serviceLoaderFactory);
		serviceLoaderFactory.getBeanProvider(TemplateExceptionHandler.class).getUnique()
				.ifPresent(this::setTemplateExceptionHandler);
		this.configured = true;
	}

	public static Version getDefaultVersion(PropertyFactory environment) {
		String versionString = environment.getAsString(CONFIG_PROPERTY_PREFIX + "version");
		if (StringUtils.hasText(versionString)) {
			try {
				Date buildDate;
				{
					String buildDateStr = environment.getAsString(CONFIG_PROPERTY_PREFIX + "buildTimestamp");
					if (buildDateStr.endsWith("Z")) {
						buildDateStr = buildDateStr.substring(0, buildDateStr.length() - 1) + "+0000";
					}
					try {
						buildDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.US).parse(buildDateStr);
					} catch (java.text.ParseException e) {
						buildDate = null;
					}
				}

				final Boolean gaeCompliant = environment.get(CONFIG_PROPERTY_PREFIX + "isGAECompliant")
						.getAsObject(Boolean.class);
				return new Version(versionString, gaeCompliant, buildDate);
			} catch (Exception e) {
				throw new RuntimeException("Failed to load and parse version", e);
			}
		}
		return VERSION_2_3_31;
	}

	@Override
	public boolean isConfigured() {
		return configured;
	}
}
