package io.basc.framework.mvc.annotation;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import io.basc.framework.context.annotation.ConditionalOnParameters;
import io.basc.framework.core.annotation.Annotations;
import io.basc.framework.core.annotation.KeyValuePair;
import io.basc.framework.core.annotation.MergedAnnotatedElement;
import io.basc.framework.http.HttpMethod;
import io.basc.framework.lang.UnsupportedException;
import io.basc.framework.mvc.ActionResolver;
import io.basc.framework.mvc.ActionResolverExtend;
import io.basc.framework.mvc.action.Action;
import io.basc.framework.mvc.action.ActionInterceptor;
import io.basc.framework.security.authority.http.DefaultHttpAuthority;
import io.basc.framework.security.authority.http.HttpAuthority;
import io.basc.framework.security.authority.http.HttpAuthorityManager;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.codec.Encoder;
import io.basc.framework.util.codec.support.CharsetCodec;
import io.basc.framework.util.collection.CollectionUtils;
import io.basc.framework.util.logging.Logger;
import io.basc.framework.util.logging.LogManager;
import io.basc.framework.util.register.Registration;
import io.basc.framework.web.pattern.HttpPattern;

@ConditionalOnParameters
public class AnnotationActionResolverExtend implements ActionResolverExtend {
	private static Logger logger = LogManager.getLogger(AnnotationActionResolverExtend.class);
	private static final Encoder<String, String> ID_ENCODER = CharsetCodec.UTF_8.toBase64();

	@Override
	public String getControllerId(Class<?> sourceClass, Method method, ActionResolver chain) {
		Controller controller = Annotations.getAnnotation(Controller.class, sourceClass, method);
		if (controller != null && StringUtils.isNotEmpty(controller.value())) {
			return controller.value();
		}
		return ActionResolverExtend.super.getControllerId(sourceClass, method, chain);
	}

	@Override
	public Collection<String> getActionInterceptorNames(Class<?> sourceClass, Method method, ActionResolver chain) {
		LinkedHashSet<String> sets = new LinkedHashSet<String>();
		ActionInterceptors actionInterceptors = sourceClass.getAnnotation(ActionInterceptors.class);
		if (actionInterceptors != null) {
			for (String name : actionInterceptors.name()) {
				sets.add(name);
			}
			for (Class<? extends ActionInterceptor> f : actionInterceptors.value()) {
				sets.add(f.getName());
			}
		}

		actionInterceptors = method.getAnnotation(ActionInterceptors.class);
		if (actionInterceptors != null) {
			sets.clear();
			for (String name : actionInterceptors.name()) {
				sets.add(name);
			}
			for (Class<? extends ActionInterceptor> f : actionInterceptors.value()) {
				sets.add(f.getName());
			}
		}
		Collection<String> names = ActionResolverExtend.super.getActionInterceptorNames(sourceClass, method, chain);
		if (!CollectionUtils.isEmpty(names)) {
			sets.addAll(names);
		}
		return sets;
	}

	private String getParentId(AnnotatedElement annotatedElement, String defaultId) {
		ActionAuthorityParent actionAuthorityParent = annotatedElement.getAnnotation(ActionAuthorityParent.class);
		String parentId = actionAuthorityParent == null ? defaultId : actionAuthorityParent.value().getName();
		if (parentId != null) {
			parentId = ID_ENCODER.encode(parentId);
		}
		return parentId;
	}

	@Override
	public Registration registerHttpAuthority(HttpAuthorityManager<? super HttpAuthority> httpAuthorityManager,
			Action action, ActionResolver chain) {
		Registration registration = Registration.EMPTY;
		ActionAuthority classAuthority = action.getSourceClass().getAnnotation(ActionAuthority.class);
		if (classAuthority != null) {// 如果在类上存在此注解说明这是一个菜单
			String id = action.getSourceClass().getName();
			id = ID_ENCODER.encode(id);
			HttpAuthority authority = httpAuthorityManager.getAuthority(id);
			if (authority == null) {
				String parentId = getParentId(action.getSourceClass(), null);
				boolean isMenu = classAuthority.menu();
				if (isMenu) {
					checkIsMenu(httpAuthorityManager, parentId, action);
				}

				try {
					registration = registration.and(httpAuthorityManager.register(new DefaultHttpAuthority(id, parentId,
							classAuthority.value(), getAttributeMap(classAuthority), isMenu, null, null)));
				} catch (Throwable e) {
					registration.unregister();
					throw e;
				}
			}
		}

		ActionAuthority methodAuthority = action.getAnnotation(ActionAuthority.class);
		if (methodAuthority == null) {
			try {
				return registration.and(chain.registerHttpAuthority(httpAuthorityManager, action));
			} catch (Throwable e) {
				registration.unregister();
				throw e;
			}
		}

		HttpPattern descriptor = getAuthorityHttpPattern(action);
		if (descriptor == null) {
			logger.warn("not found controller descriptor: {}", action);
			try {
				return registration.and(chain.registerHttpAuthority(httpAuthorityManager, action));
			} catch (Throwable e) {
				registration.unregister();
				throw e;
			}
		}

		String parentId = getParentId(new MergedAnnotatedElement(action.getSourceClass(), action.getMethod()),
				action.getSourceClass().getName());
		boolean isMenu = methodAuthority.menu();
		if (isMenu) {
			checkIsMenu(httpAuthorityManager, parentId, action);
		}

		String id = descriptor.getMethod() + "&" + descriptor.getPath();
		id = ID_ENCODER.encode(id);
		try {
			registration = registration.and(httpAuthorityManager.register(new DefaultHttpAuthority(id, parentId,
					methodAuthority.value(), getAttributeMap(classAuthority, methodAuthority), isMenu,
					descriptor.getPath(), descriptor.getMethod())));
			return registration.and(chain.registerHttpAuthority(httpAuthorityManager, action));
		} catch (Throwable e) {
			registration.unregister();
			throw e;
		}
	}

	private void checkIsMenu(HttpAuthorityManager<?> httpAuthorityManager, String parentId, Action action) {
		if (parentId != null) {
			HttpAuthority parent = httpAuthorityManager.getAuthority(parentId);
			if (parent != null && !parent.isMenu()) {
				throw new UnsupportedException("标注为一个菜单,但父级并不是一个菜单: " + action);
			}
		}
	}

	public static Map<String, String> getAttributeMap(ActionAuthority... authoritys) {
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

	public static HttpPattern getAuthorityHttpPattern(Action action) {
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
