package io.basc.framework.spring.context;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.PropertySources;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePropertySource;
import org.springframework.lang.Nullable;

public class SpringContextUtils {
	public static final String MAIN_CLASS_PROPERTY = "io.basc.framework.spring.context.mainClass";
	public static final String BASE_PACKAGE_PROPERTY = "io.basc.framework.spring.context.basePackage";
	public static final String SOURCE_PROPERTY_NAME = "io.basc.framework.spring.context.propertySource";
	private static Log logger = LogFactory.getLog(SpringContextUtils.class);

	public static EditablePropertySource getEditablePropertySource(PropertySources sources) {
		for (PropertySource<?> propertySource : sources) {
			if (propertySource.getName().equals(SOURCE_PROPERTY_NAME)
					&& propertySource instanceof EditablePropertySource) {
				return (EditablePropertySource) propertySource;
			}
		}
		return null;
	}

	public static void setBasePackage(ConfigurableEnvironment environment, String basePackage) {
		if (environment.containsProperty(BASE_PACKAGE_PROPERTY)) {
			return;
		}

		setProperty(environment.getPropertySources(), BASE_PACKAGE_PROPERTY, basePackage);
	}

	public static Optional<String> getBasePackage(Environment environment) {
		String basePackage = environment.getProperty(BASE_PACKAGE_PROPERTY, String.class);
		return Optional.ofNullable(basePackage);
	}

	public static Optional<Class<?>> getMainClass(Environment environment) {
		Class<?> mainClass = environment.getProperty(MAIN_CLASS_PROPERTY, Class.class);
		return Optional.ofNullable(mainClass);
	}

	public static void setMainClass(ConfigurableEnvironment environment, Class<?> mainClass) {
		setProperty(environment.getPropertySources(), MAIN_CLASS_PROPERTY, mainClass);
		setBasePackage(environment, mainClass.getPackage().getName());
	}

	public static void setProperty(MutablePropertySources propertySources, Map<String, ?> properties) {
		EditablePropertySource propertySource = getEditablePropertySource(propertySources);
		if (propertySource == null) {
			propertySource = new EditablePropertySource(SOURCE_PROPERTY_NAME);
			propertySources.addLast(propertySource);
		}

		propertySource.getSource().putAll(properties);
	}

	public static void setProperty(MutablePropertySources propertySources, String name, Object value) {
		setProperty(propertySources, Collections.singletonMap(name, value));
	}

	@Nullable
	public static PropertySource<?> addLastPropertySource(ConfigurableApplicationContext applicationContext,
			String location) {
		Resource resource = applicationContext.getResource(location);
		if (resource == null || !resource.exists()) {
			return null;
		}

		ResourcePropertySource propertySource;
		try {
			propertySource = new ResourcePropertySource(location, resource);
		} catch (IOException e) {
			logger.error("Exception reading the default " + location + " configuration file", e);
			return null;
		}
		applicationContext.getEnvironment().getPropertySources().addLast(propertySource);
		return propertySource;
	}
}
