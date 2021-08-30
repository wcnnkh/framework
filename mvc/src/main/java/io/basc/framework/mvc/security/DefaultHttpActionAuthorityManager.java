package io.basc.framework.mvc.security;

import io.basc.framework.annotation.KeyValuePair;
import io.basc.framework.annotation.MultiAnnotatedElement;
import io.basc.framework.codec.Encoder;
import io.basc.framework.codec.support.CharsetCodec;
import io.basc.framework.context.annotation.Provider;
import io.basc.framework.http.HttpMethod;
import io.basc.framework.lang.NotSupportedException;
import io.basc.framework.mvc.action.Action;
import io.basc.framework.mvc.annotation.ActionAuthority;
import io.basc.framework.mvc.annotation.ActionAuthorityParent;
import io.basc.framework.security.authority.http.DefaultHttpAuthority;
import io.basc.framework.security.authority.http.DefaultHttpAuthorityManager;
import io.basc.framework.security.authority.http.HttpAuthority;
import io.basc.framework.web.pattern.HttpPattern;

import java.lang.reflect.AnnotatedElement;
import java.util.HashMap;
import java.util.Map;

@Provider(value = HttpActionAuthorityManager.class)
public class DefaultHttpActionAuthorityManager extends
		DefaultHttpAuthorityManager<HttpAuthority> implements
		HttpActionAuthorityManager {
	private static final Encoder<String, String> ID_ENCODER = CharsetCodec.UTF_8
			.toBase64();

	private String getParentId(AnnotatedElement annotatedElement,
			String defaultId) {
		ActionAuthorityParent actionAuthorityParent = annotatedElement
				.getAnnotation(ActionAuthorityParent.class);
		String parentId = actionAuthorityParent == null ? defaultId
				: actionAuthorityParent.value().getName();
		if (parentId != null) {
			parentId = ID_ENCODER.encode(parentId);
		}
		return parentId;
	}

	public void register(Action action) {
		ActionAuthority classAuthority = action.getDeclaringClass()
				.getAnnotation(ActionAuthority.class);
		if (classAuthority != null) {// 如果在类上存在此注解说明这是一个菜单
			String id = action.getDeclaringClass().getName();
			id = ID_ENCODER.encode(id);
			HttpAuthority authority = getAuthority(id);
			if (authority == null) {
				String parentId = getParentId(action.getDeclaringClass(), null);
				boolean isMenu = classAuthority.menu();
				if (isMenu) {
					checkIsMenu(parentId, action);
				}

				register(new DefaultHttpAuthority(id, parentId,
						classAuthority.value(),
						getAttributeMap(classAuthority), isMenu, null, null));
			}
		}

		ActionAuthority methodAuthority = action
				.getAnnotation(ActionAuthority.class);
		if (methodAuthority == null) {
			return;
		}

		HttpPattern descriptor = getAuthorityHttpPattern(action);
		if (descriptor == null) {
			logger.warn("not found controller descriptor: {}", action);
			return;
		}

		String parentId = getParentId(
				new MultiAnnotatedElement(action.getDeclaringClass(),
						action.getMethod()), action.getDeclaringClass()
						.getName());
		boolean isMenu = methodAuthority.menu();
		if (isMenu) {
			checkIsMenu(parentId, action);
		}

		String id = descriptor.getMethod() + "&" + descriptor.getPath();
		id = ID_ENCODER.encode(id);
		register(new DefaultHttpAuthority(id, parentId,
				methodAuthority.value(), getAttributeMap(classAuthority,
						methodAuthority), isMenu, descriptor.getPath(),
				descriptor.getMethod()));
	}

	private void checkIsMenu(String parentId, Action action) {
		if (parentId != null) {
			HttpAuthority parent = getAuthority(parentId);
			if (parent != null && !parent.isMenu()) {
				throw new NotSupportedException("标注为一个菜单,但父级并不是一个菜单: " + action);
			}
		}
	}

	public HttpAuthority getAuthority(Action action) {
		for (HttpPattern descriptor : action.getPatternts()) {
			HttpAuthority authority = getAuthority(descriptor.getPath(),
					descriptor.getMethod());
			if (authority != null) {
				return authority;
			}
		}
		return null;
	}

	protected final Map<String, String> getAttributeMap(
			ActionAuthority... authoritys) {
		Map<String, String> attributeMap = new HashMap<String, String>();
		if (authoritys != null) {
			for (ActionAuthority actionAuthority : authoritys) {
				if (actionAuthority == null) {
					continue;
				}

				for (KeyValuePair pair : actionAuthority.attributes()) {
					attributeMap.put(pair.key(), pair.value());
				}
			}
		}
		return attributeMap.isEmpty() ? null : attributeMap;
	}

	protected HttpPattern getAuthorityHttpPattern(Action action) {
		for (HttpPattern descriptor : action.getPatternts()) {
			if (HttpMethod.GET.name().equals(descriptor.getMethod())) {
				return descriptor;
			}
		}

		for (HttpPattern descriptor : action.getPatternts()) {
			return descriptor;
		}
		return null;
	}
}
