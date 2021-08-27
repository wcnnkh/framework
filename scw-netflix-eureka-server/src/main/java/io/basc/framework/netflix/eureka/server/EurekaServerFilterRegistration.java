package io.basc.framework.netflix.eureka.server;

import io.basc.framework.boot.servlet.FilterRegistration;
import io.basc.framework.context.annotation.Provider;
import io.basc.framework.core.utils.StringUtils;
import io.basc.framework.netflix.eureka.EurekaConstants;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.Filter;
import javax.ws.rs.Path;
import javax.ws.rs.core.Application;

import com.sun.jersey.api.core.DefaultResourceConfig;
import com.sun.jersey.spi.container.servlet.ServletContainer;

@Provider
public class EurekaServerFilterRegistration implements FilterRegistration {
	/**
	 * List of packages containing Jersey resources required by the Eureka
	 * server.
	 */
	private static final String[] EUREKA_PACKAGES = new String[] { "com.netflix.discovery", "com.netflix.eureka" };
	private final io.basc.framework.boot.Application application;
	
	public EurekaServerFilterRegistration(io.basc.framework.boot.Application application){
		this.application = application;
	}
	
	public static Application getApplication(io.basc.framework.boot.Application application) {
		Set<Class<?>> classes = new HashSet<>();
		for (Class<?> clazz : application.getBeanFactory().getClassesLoaderFactory().getClassesLoader(StringUtils.arrayToCommaDelimitedString(EUREKA_PACKAGES))) {
			if (clazz.getAnnotation(Path.class) != null
					|| clazz.getAnnotation(javax.ws.rs.ext.Provider.class) != null) {
				classes.add(clazz);
			}
		}

		// Construct the Jersey ResourceConfig
		Map<String, Object> propsAndFeatures = new HashMap<>();
		propsAndFeatures.put(
				// Skip static content used by the webapp
				ServletContainer.PROPERTY_WEB_PAGE_CONTENT_REGEX,
				EurekaConstants.DEFAULT_PREFIX + "/(fonts|images|css|js)/.*");
		DefaultResourceConfig rc = new DefaultResourceConfig(classes);
		rc.setPropertiesAndFeatures(propsAndFeatures);
		return rc;
	}

	@Override
	public Filter getFilter() {
		return new ServletContainer(getApplication(application));
	}

	@Override
	public Collection<String> getUrlPatterns() {
		return Collections.singletonList(EurekaConstants.DEFAULT_PREFIX + ALL);
	}

}
