package scw.security.authority.annotation;

import java.lang.reflect.Method;
import java.util.Map;

import scw.security.authority.Authority;
import scw.security.authority.DefaultAuthority;

public class DefaultAnnotationAuthorityManager extends AnnotationAuthorityManager<Authority> {

	@Override
	protected Authority createAuthority(Class<?> clazz, Method method, String id, String parentId, String name,
			Map<String, String> attributeMap, boolean isMenu) {
		return new DefaultAuthority(id, parentId, name, attributeMap, isMenu);
	}
}
