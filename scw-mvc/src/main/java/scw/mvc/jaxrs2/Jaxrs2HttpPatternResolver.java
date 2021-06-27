package scw.mvc.jaxrs2;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.Path;

import scw.context.annotation.Provider;
import scw.core.annotation.AnnotatedElementUtils;
import scw.core.utils.StringUtils;
import scw.mvc.HttpPatternResolver;
import scw.util.placeholder.PropertyResolver;
import scw.util.placeholder.PropertyResolverAware;
import scw.web.pattern.HttpPattern;

@Provider
public class Jaxrs2HttpPatternResolver implements HttpPatternResolver, PropertyResolverAware{
	private PropertyResolver propertyResolver;

	public PropertyResolver getPropertyResolver() {
		return propertyResolver;
	}

	public void setPropertyResolver(PropertyResolver propertyResolver) {
		this.propertyResolver = propertyResolver;
	}
	
	@Override
	public boolean canResolveHttpPattern(Class<?> clazz) {
		return AnnotatedElementUtils.isAnnotated(clazz, Path.class);
	}

	@Override
	public boolean canResolveHttpPattern(Class<?> clazz, Method method) {
		return AnnotatedElementUtils.isAnnotated(clazz, Path.class)
				&& AnnotatedElementUtils.isAnnotated(method, Path.class)
				&& AnnotatedElementUtils.isAnnotated(method, HttpMethod.class);
	}

	@Override
	public Collection<HttpPattern> resolveHttpPattern(Class<?> clazz,
			Method method) {
		Set<HttpMethod> httpMethods = AnnotatedElementUtils
				.getAllMergedAnnotations(method, HttpMethod.class);
		Path clazzPath = AnnotatedElementUtils.getMergedAnnotation(clazz,
				Path.class);
		Path methodPath = AnnotatedElementUtils.getMergedAnnotation(method,
				Path.class);
		String path = StringUtils.mergePath("/", clazzPath.value(),
				methodPath.value());
		if (propertyResolver != null) {
			path = propertyResolver.resolvePlaceholders(path);
		}

		Set<HttpPattern> httpPatterns = new LinkedHashSet<HttpPattern>();
		for (HttpMethod httpMethod : httpMethods) {
			httpPatterns.add(new HttpPattern(path, httpMethod.value(), null));
		}
		return httpPatterns;
	}

}
