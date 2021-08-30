package io.basc.framework.mvc.jaxrs2;

import io.basc.framework.context.annotation.Provider;
import io.basc.framework.core.annotation.AnnotatedElementUtils;
import io.basc.framework.mvc.HttpPatternResolver;
import io.basc.framework.net.MimeTypeUtils;
import io.basc.framework.net.MimeTypes;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.placeholder.PropertyResolver;
import io.basc.framework.util.placeholder.PropertyResolverAware;
import io.basc.framework.web.pattern.HttpPattern;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.Path;

@Provider
public class Jaxrs2HttpPatternResolver implements HttpPatternResolver,
		PropertyResolverAware {
	private PropertyResolver propertyResolver;

	public PropertyResolver getPropertyResolver() {
		return propertyResolver;
	}

	public void setPropertyResolver(PropertyResolver propertyResolver) {
		this.propertyResolver = propertyResolver;
	}

	@Override
	public boolean canResolveHttpPattern(Class<?> clazz) {
		return true;
	}

	@Override
	public boolean canResolveHttpPattern(Class<?> clazz, Method method) {
		return AnnotatedElementUtils.isAnnotated(method, Path.class)
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
		String path = StringUtils.mergePath("/", clazzPath == null? "/":clazzPath.value(),
				methodPath.value());
		if (propertyResolver != null) {
			path = propertyResolver.resolvePlaceholders(path);
		}

		MimeTypes mimeTypes = null;
		Consumes clazzConsumes = clazz.getAnnotation(Consumes.class);
		Consumes methodConsumes = method.getAnnotation(Consumes.class);
		if (clazzConsumes != null || methodConsumes != null) {
			mimeTypes = new MimeTypes();
			if (clazzConsumes != null) {
				for (String name : clazzConsumes.value()) {
					mimeTypes.add(MimeTypeUtils.parseMimeType(name));
				}
			}

			if (methodConsumes != null) {
				for (String name : methodConsumes.value()) {
					mimeTypes.add(MimeTypeUtils.parseMimeType(name));
				}
			}
		}

		Set<HttpPattern> httpPatterns = new LinkedHashSet<HttpPattern>();
		for (HttpMethod httpMethod : httpMethods) {
			httpPatterns.add(new HttpPattern(path, httpMethod.value(), null));
		}
		return httpPatterns;
	}

}
