package io.basc.framework.mvc.jaxrs;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.Path;

import io.basc.framework.context.annotation.Provider;
import io.basc.framework.core.annotation.AnnotatedElementUtils;
import io.basc.framework.mvc.HttpPatternResolver;
import io.basc.framework.net.MimeTypeUtils;
import io.basc.framework.net.MimeTypes;
import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.placeholder.PropertyResolver;
import io.basc.framework.util.placeholder.PropertyResolverAware;
import io.basc.framework.web.pattern.HttpPattern;

@Provider
public class Jaxrs2HttpPatternResolver implements HttpPatternResolver, PropertyResolverAware {
	private PropertyResolver propertyResolver;

	public PropertyResolver getPropertyResolver() {
		return propertyResolver;
	}

	public void setPropertyResolver(PropertyResolver propertyResolver) {
		this.propertyResolver = propertyResolver;
	}

	/**
	 * 允许类上不存在注解,全部通过
	 */
	@Override
	public boolean canResolve(Class<?> clazz) {
		return true;
	}

	@Override
	public boolean canResolve(Class<?> clazz, Method method) {
		return AnnotatedElementUtils.isAnnotated(method, Path.class);
	}

	@Override
	public Collection<HttpPattern> resolve(Class<?> clazz, Method method) {
		Path clazzPath = AnnotatedElementUtils.getMergedAnnotation(clazz, Path.class);
		Path methodPath = AnnotatedElementUtils.getMergedAnnotation(method, Path.class);
		String path = StringUtils.mergePaths(Arrays.asList("/", clazzPath == null ? "/" : clazzPath.value(),
				methodPath == null ? "/" : methodPath.value()), propertyResolver);
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

		Set<HttpMethod> httpMethods = AnnotatedElementUtils.getAllMergedAnnotations(method, HttpMethod.class);
		Set<HttpPattern> httpPatterns = new LinkedHashSet<HttpPattern>();
		if (!CollectionUtils.isEmpty(httpMethods)) {
			for (HttpMethod httpMethod : httpMethods) {
				httpPatterns.add(new HttpPattern(path, httpMethod.value(), null));
			}
		}

		if (httpMethods.isEmpty()) {
			httpPatterns.add(new HttpPattern(path, io.basc.framework.http.HttpMethod.GET.name()));
		}
		return httpPatterns;
	}

}
