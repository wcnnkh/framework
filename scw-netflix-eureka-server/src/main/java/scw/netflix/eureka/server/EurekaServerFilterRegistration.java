package scw.netflix.eureka.server;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.Filter;
import javax.ws.rs.Path;
import javax.ws.rs.core.Application;

import scw.boot.servlet.FilterRegistration;
import scw.context.ClassesLoaderFactory;
import scw.context.annotation.Provider;
import scw.core.utils.StringUtils;
import scw.netflix.eureka.EurekaConstants;

import com.sun.jersey.api.core.DefaultResourceConfig;
import com.sun.jersey.spi.container.servlet.ServletContainer;

@Provider(order = Integer.MIN_VALUE)
public class EurekaServerFilterRegistration implements FilterRegistration {
	/**
	 * List of packages containing Jersey resources required by the Eureka
	 * server.
	 */
	private static final String[] EUREKA_PACKAGES = new String[] { "com.netflix.discovery", "com.netflix.eureka" };
	private final scw.boot.Application application;
	
	public EurekaServerFilterRegistration(scw.boot.Application application){
		this.application = application;
	}
	
	public static Application getApplication(ClassesLoaderFactory classesLoaderFactory) {
		Set<Class<?>> classes = new HashSet<>();
		for (Class<?> clazz : classesLoaderFactory.getClassesLoader(StringUtils.arrayToCommaDelimitedString(EUREKA_PACKAGES))) {
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
		return new ServletContainer(getApplication(application.getBeanFactory()));
	}

	@Override
	public Collection<String> getUrlPatterns() {
		return Collections.singletonList(EurekaConstants.DEFAULT_PREFIX + ALL);
	}

}
