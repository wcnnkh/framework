package scw.mvc.security;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import scw.compatible.CompatibleUtils;
import scw.core.Constants;
import scw.http.HttpMethod;
import scw.http.server.HttpControllerDescriptor;
import scw.mvc.action.Action;
import scw.mvc.action.ActionManager;
import scw.security.authority.annotation.AnnotationAuthorityManager;
import scw.security.authority.http.DefaultHttpAuthority;
import scw.security.authority.http.HttpAuthority;
import scw.util.Base64;

public class AnnotationHttpActionAuthorityManager extends AnnotationAuthorityManager<HttpAuthority>
		implements HttpActionAuthorityManager {
	private ActionManager actionManager;
	private Map<String, Map<HttpMethod, String>> pathMap = new HashMap<String, Map<HttpMethod, String>>();

	public HttpAuthority getAuthority(String path, HttpMethod method) {
		Map<HttpMethod, String> map = pathMap.get(path);
		if (map == null) {
			return null;
		}

		String id = map.get(method);
		if (id == null) {
			return null;
		}

		return getAuthority(id);
	}

	public AnnotationHttpActionAuthorityManager(ActionManager actionManager) {
		this.actionManager = actionManager;
		HashSet<Class<?>> classList = new HashSet<Class<?>>();
		for (Action action : actionManager.getActions()) {
			classList.add(action.getTargetClass());
		}
		register(classList, new HashSet<Class<?>>());
	}

	@Override
	protected String getAuthorityId(Class<?> clazz, Method method) {
		if (method != null) {
			Action action = actionManager.getAction(clazz, method);
			HttpControllerDescriptor httpControllerDescriptor = getAuthorityControllerDescriptor(action);
			String id = httpControllerDescriptor.getMethod() + "&" + httpControllerDescriptor.getPath();
			return Base64.encode(CompatibleUtils.getStringOperations().getBytes(id, Constants.ISO_8859_1));
		}
		return super.getAuthorityId(clazz, method);
	}

	protected HttpControllerDescriptor getAuthorityControllerDescriptor(Action action) {
		for (HttpControllerDescriptor descriptor : action.getHttpControllerDescriptors()) {
			if (descriptor.getMethod() == HttpMethod.GET) {
				return descriptor;
			}
		}

		for (HttpControllerDescriptor descriptor : action.getHttpControllerDescriptors()) {
			return descriptor;
		}
		return null;
	}

	@Override
	protected HttpAuthority createAuthority(Class<?> clazz, Method method, String id, String parentId, String name,
			Map<String, String> attributeMap, boolean isMenu) {
		if (method != null) {
			Action action = actionManager.getAction(clazz, method);
			HttpControllerDescriptor descriptor = getAuthorityControllerDescriptor(action);
			if (descriptor != null) {
				return new DefaultHttpAuthority(id, parentId, name, attributeMap, isMenu, descriptor.getPath(),
						descriptor.getMethod());
			}
		}
		return new DefaultHttpAuthority(id, parentId, name, attributeMap, isMenu, null, null);
	}

	public HttpAuthority getAuthority(Action action) {
		for (HttpControllerDescriptor descriptor : action.getHttpControllerDescriptors()) {
			HttpAuthority authority = getAuthority(descriptor.getPath(), descriptor.getMethod());
			if (authority != null) {
				return authority;
			}
		}
		return null;
	}
}
