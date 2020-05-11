package scw.mvc.action.authority;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Map;

import scw.mvc.action.Action;
import scw.mvc.action.manager.ActionManager;
import scw.mvc.action.manager.HttpAction;
import scw.mvc.action.manager.HttpAction.ControllerDescriptor;
import scw.net.http.HttpMethod;
import scw.security.authority.annotation.AnnotationAuthorityManager;
import scw.security.authority.http.DefaultHttpAuthority;
import scw.security.authority.http.HttpAuthority;

public class AnnotationHttpActionAuthorityManager extends AnnotationAuthorityManager<HttpAuthority> {
	private ActionManager actionManager;

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
			if (action != null) {

			}
		}

		return super.getAuthorityId(clazz, method);
	}

	protected ControllerDescriptor getAuthorityControllerDescriptor(HttpAction action) {
		for (ControllerDescriptor descriptor : action.getControllerDescriptors()) {
			if (descriptor.getHttpMethod() == HttpMethod.GET) {
				return descriptor;
			}
		}

		for (ControllerDescriptor descriptor : action.getControllerDescriptors()) {
			return descriptor;
		}
		return null;
	}

	@Override
	protected HttpAuthority createAuthority(Class<?> clazz, Method method, String id, String parentId, String name,
			Map<String, String> attributeMap, boolean isMenu) {
		if (method != null) {
			Action action = actionManager.getAction(clazz, method);
			if (action != null && action instanceof HttpAction) {
				ControllerDescriptor descriptor = getAuthorityControllerDescriptor((HttpAction) action);
				if (descriptor != null) {
					return new DefaultHttpAuthority(id, parentId, name, attributeMap, isMenu,
							descriptor.getController(), descriptor.getHttpMethod());
				}
			}
		}
		return new DefaultHttpAuthority(id, parentId, name, attributeMap, isMenu, null, null);
	}
}
