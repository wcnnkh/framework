package scw.mvc.action.authority;

import java.lang.reflect.AnnotatedElement;
import java.util.HashMap;
import java.util.Map;

import scw.beans.annotation.Bean;
import scw.compatible.CompatibleUtils;
import scw.core.Constants;
import scw.core.annotation.KeyValuePair;
import scw.core.instance.annotation.Configuration;
import scw.lang.NotSupportedException;
import scw.mvc.action.Action;
import scw.mvc.action.manager.ActionManager;
import scw.mvc.action.manager.HttpAction;
import scw.mvc.action.manager.HttpAction.ControllerDescriptor;
import scw.mvc.annotation.ActionAuthority;
import scw.mvc.annotation.ActionAuthorityParent;
import scw.net.http.HttpMethod;
import scw.security.authority.http.DefaultHttpAuthority;
import scw.security.authority.http.DefaultHttpAuthorityManager;
import scw.security.authority.http.HttpAuthority;
import scw.util.Base64;

@Configuration(order = Integer.MIN_VALUE, value = HttpActionAuthorityManager.class)
@Bean(proxy = false)
public class DefaultHttpActionAuthorityManager extends DefaultHttpAuthorityManager<HttpAuthority>
		implements HttpActionAuthorityManager {

	public DefaultHttpActionAuthorityManager(ActionManager actionManager) {
		for (Action action : actionManager.getActions()) {
			if (action instanceof HttpAction) {
				register((HttpAction) action);
			}
		}
	}

	private String getParentId(AnnotatedElement annotatedElement, String defaultId) {
		ActionAuthorityParent actionAuthorityParent = annotatedElement.getAnnotation(ActionAuthorityParent.class);
		String parentId = actionAuthorityParent == null ? defaultId : actionAuthorityParent.value().getName();
		if (parentId != null) {
			parentId = Base64.encode(CompatibleUtils.getStringOperations().getBytes(parentId, Constants.ISO_8859_1));
		}
		return parentId;
	}

	public void register(HttpAction action) {
		ActionAuthority classAuthority = action.getTargetClassAnnotatedElement().getAnnotation(ActionAuthority.class);
		if (classAuthority != null) {// 如果在类上存在此注解说明这是一个菜单
			String id = action.getTargetClass().getName();
			id = Base64.encode(CompatibleUtils.getStringOperations().getBytes(id, Constants.ISO_8859_1));
			HttpAuthority authority = getAuthority(id);
			if (authority == null) {
				String parentId = getParentId(action.getTargetClassAnnotatedElement(), null);
				boolean isMenu = classAuthority.menu();
				if (isMenu) {
					checkIsMenu(parentId, action);
				}

				register(new DefaultHttpAuthority(id, parentId, classAuthority.value(), getAttributeMap(classAuthority),
						isMenu, null, null));
			}
		}

		ActionAuthority methodAuthority = action.getMethodAnnotatedElement().getAnnotation(ActionAuthority.class);
		if (methodAuthority == null) {
			return;
		}

		ControllerDescriptor descriptor = getAuthorityControllerDescriptor(action);
		if (descriptor == null) {
			logger.warn("not found controller descriptor: {}", action);
			return;
		}

		String parentId = getParentId(action.getAnnotatedElement(), action.getTargetClass().getName());
		boolean isMenu = methodAuthority.menu();
		if (isMenu) {
			checkIsMenu(parentId, action);
		}

		String id = descriptor.getHttpMethod() + "&" + descriptor.getController();
		id = Base64.encode(CompatibleUtils.getStringOperations().getBytes(id, Constants.ISO_8859_1));

		register(new DefaultHttpAuthority(id, parentId, methodAuthority.value(),
				getAttributeMap(classAuthority, methodAuthority), isMenu, descriptor.getController(),
				descriptor.getHttpMethod()));
	}

	private void checkIsMenu(String parentId, Action action) {
		if (parentId != null) {
			HttpAuthority parent = getAuthority(parentId);
			if (parent != null && !parent.isMenu()) {
				throw new NotSupportedException("标注为一个菜单,但父级并不是一个菜单: " + action);
			}
		}
	}

	public HttpAuthority getAuthority(HttpAction action) {
		for (ControllerDescriptor descriptor : action.getControllerDescriptors()) {
			HttpAuthority authority = getAuthority(descriptor.getController(), descriptor.getHttpMethod());
			if (authority != null) {
				return authority;
			}
		}
		return null;
	}

	protected final Map<String, String> getAttributeMap(ActionAuthority... authoritys) {
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
}
