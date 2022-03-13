package io.basc.framework.freemarker;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.Version;
import io.basc.framework.env.Environment;
import io.basc.framework.env.Sys;
import io.basc.framework.factory.Configurable;
import io.basc.framework.factory.ServiceLoaderFactory;
import io.basc.framework.util.StringUtils;

public class EnvConfiguration extends Configuration implements Configurable {
	public static final String CONFIG_PROPERTY_PREFIX = "io.basc.freemarker.";

	private final Environment env;

	public EnvConfiguration() {
		this(Sys.env);
	}

	public EnvConfiguration(Environment env) {
		super(getDefaultVersion(env));
		this.env = env;
		setDefaultEncoding(env.getCharsetName());
		setTemplateLoader(new DefaultTemplateLoader(env));
		setObjectWrapper(new DefaultObjectWrapper(getDefaultVersion(env)));
	}

	public Environment getEnv() {
		return env;
	}

	@Override
	public void configure(ServiceLoaderFactory serviceLoaderFactory) {
		if (serviceLoaderFactory.isInstance(TemplateLoader.class)) {
			setTemplateLoader(serviceLoaderFactory.getInstance(TemplateLoader.class));
		}
		if (serviceLoaderFactory.isInstance(TemplateExceptionHandler.class)) {
			setTemplateExceptionHandler(serviceLoaderFactory.getInstance(TemplateExceptionHandler.class));
		}
	}

	public static Version getDefaultVersion(Environment environment) {
		String versionString = environment.getString(CONFIG_PROPERTY_PREFIX + "version");
		if (StringUtils.hasText(versionString)) {
			try {
				Date buildDate;
				{
					String buildDateStr = environment.getString(CONFIG_PROPERTY_PREFIX + "buildTimestamp");
					if (buildDateStr.endsWith("Z")) {
						buildDateStr = buildDateStr.substring(0, buildDateStr.length() - 1) + "+0000";
					}
					try {
						buildDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.US).parse(buildDateStr);
					} catch (java.text.ParseException e) {
						buildDate = null;
					}
				}

				final Boolean gaeCompliant = environment.getBoolean(CONFIG_PROPERTY_PREFIX + "isGAECompliant");
				return new Version(versionString, gaeCompliant, buildDate);
			} catch (Exception e) {
				throw new RuntimeException("Failed to load and parse version", e);
			}
		}
		return VERSION_2_3_31;
	}
}
