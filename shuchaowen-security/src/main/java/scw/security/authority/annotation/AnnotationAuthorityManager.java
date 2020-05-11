package scw.security.authority.annotation;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import scw.compatible.CompatibleUtils;
import scw.core.Constants;
import scw.core.annotation.KeyValuePair;
import scw.core.utils.StringUtils;
import scw.json.JSONUtils;
import scw.security.authority.Authority;
import scw.security.authority.DefaultAuthorityManager;
import scw.util.Base64;

public abstract class AnnotationAuthorityManager<T extends Authority> extends DefaultAuthorityManager<T> {
	public void register(Collection<Class<?>> classList, Set<Class<?>> alreadyRegisterSet) {
		for (Class<?> clazz : classList) {
			if (alreadyRegisterSet.contains(clazz)) {
				// 已经注册过了
				logger.warn("already register: {}", clazz);
				continue;
			}

			AuthorityConfig config = clazz.getAnnotation(AuthorityConfig.class);
			if (config == null) {
				continue;
			}

			String classId;
			if (StringUtils.isEmpty(config.id())) {
				classId = getAuthorityId(clazz, null);
			} else {
				classId = config.id();
			}

			T oldAuthority = getAuthority(classId);
			if (oldAuthority != null) {
				// 存在相同的id
				logger.warn("already authroity id: {}", clazz, JSONUtils.toJSONString(oldAuthority));
				continue;
			}

			alreadyRegisterSet.add(clazz);

			T authority = createAuthority(clazz, null, classId, null, config.value(), getAttributeMap(config),
					config.menu());
			register(authority);
			for (Class<?> childrenClass : config.children()) {
				alreadyRegisterSet.add(childrenClass);
				register(childrenClass, classId, config, alreadyRegisterSet);
			}
		}

		for (Class<?> clazz : classList) {
			if (alreadyRegisterSet.contains(clazz)) {
				// 已经注册过了
				continue;
			}

			alreadyRegisterSet.add(clazz);
			register(clazz, null, null, alreadyRegisterSet);
		}
	}

	protected String getAuthorityId(Class<?> clazz, Method method) {
		String id = clazz.getName();
		if (method != null) {
			id = method.toString();
		}
		return Base64.encode(CompatibleUtils.getStringOperations().getBytes(id, Constants.ISO_8859_1));
	}

	protected void register(Class<?> clazz, String parentId, AuthorityConfig parent, Set<Class<?>> alreadyRegisterSet) {
		for (Method method : clazz.getDeclaredMethods()) {
			AuthorityConfig config = method.getAnnotation(AuthorityConfig.class);
			if (config == null) {
				continue;
			}

			String id;
			if (StringUtils.isEmpty(config.id())) {
				id = getAuthorityId(clazz, method);
			} else {
				id = config.id();
			}

			T authority = createAuthority(clazz, method, id, parentId, config.value(), getAttributeMap(parent, config),
					config.menu());
			register(authority);

			for (Class<?> childrenClass : config.children()) {
				register(childrenClass, id, config, alreadyRegisterSet);
			}
		}
	}

	protected Map<String, String> getAttributeMap(AuthorityConfig... authoritys) {
		Map<String, String> attributeMap = new HashMap<String, String>();
		if (authoritys != null) {
			for (AuthorityConfig authority : authoritys) {
				if (authority == null) {
					continue;
				}

				for (KeyValuePair pair : authority.attributes()) {
					attributeMap.put(pair.key(), pair.value());
				}
			}
		}
		return attributeMap.isEmpty() ? null : attributeMap;
	}

	protected abstract T createAuthority(Class<?> clazz, Method method, String id, String parentId, String name,
			Map<String, String> attributeMap, boolean isMenu);
}
