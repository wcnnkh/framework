package scw.mvc.action.authority;

import java.util.HashMap;
import java.util.Map;

import scw.beans.annotation.Bean;
import scw.compatible.CompatibleUtils;
import scw.core.Base64;
import scw.core.Constants;
import scw.core.Init;
import scw.core.annotation.KeyValuePair;
import scw.core.instance.annotation.Configuration;
import scw.mvc.action.Action;
import scw.mvc.action.manager.ActionManager;
import scw.mvc.action.manager.HttpAction;
import scw.mvc.action.manager.HttpAction.ControllerDescriptor;
import scw.mvc.annotation.Authority;
import scw.mvc.annotation.AuthorityParent;
import scw.net.http.HttpMethod;
import scw.security.authority.http.DefaultHttpAuthorityManager;

@Configuration(order = Integer.MIN_VALUE, value = HttpActionAuthorityManager.class)
@Bean(proxy = false)
public class DefaultHttpActionAuthorityManager extends
		DefaultHttpAuthorityManager<HttpActionAuthority> implements
		HttpActionAuthorityManager, Init {
	private final ActionManager actionManager;

	public DefaultHttpActionAuthorityManager(ActionManager actionManager) {
		this.actionManager = actionManager;
	}

	public void init() {
		for (Action action : actionManager.getActions()) {
			if (action instanceof HttpAction) {
				register((HttpAction) action);
			}
		}
	}

	public void register(HttpAction action) {
		AuthorityParent authorityParent = action.getAnnotatedElement()
				.getAnnotation(AuthorityParent.class);
		String parentId = authorityParent == null ? null : authorityParent
				.value().getName();
		if(parentId != null){
			parentId = Base64.encode(CompatibleUtils.getStringOperations().getBytes(parentId, Constants.ISO_8859_1));
		}
		
		Authority classAuthority = action.getTargetClassAnnotatedElement()
				.getAnnotation(Authority.class);
		if (classAuthority != null) {// 如果在类上存在此注解说明这是一个菜单
			String id = action.getTargetClass().getName();
			id = Base64.encode(CompatibleUtils.getStringOperations().getBytes(id, Constants.ISO_8859_1));
			HttpActionAuthority authority = getAuthority(id);
			if (authority == null) {
				register(new DefaultHttpActionAuthority(id, parentId,
						classAuthority.value(),
						getAttributeMap(classAuthority), null, null, false));
			}
			parentId = id;
		}

		Authority methodAuthority = action.getMethodAnnotatedElement()
				.getAnnotation(Authority.class);
		if (methodAuthority == null) {
			return;
		}

		ControllerDescriptor descriptor = getAuthorityControllerDescriptor(action);
		if (descriptor == null) {
			return;
		}

		String id = descriptor.getHttpMethod() + "&"
				+ descriptor.getController();
		id = Base64.encode(CompatibleUtils.getStringOperations().getBytes(id, Constants.ISO_8859_1));
		
		register(new DefaultHttpActionAuthority(id, parentId,
				methodAuthority.value(), getAttributeMap(classAuthority,
						methodAuthority), descriptor.getController(),
				descriptor.getHttpMethod(), methodAuthority.menuAction()));
	}

	public HttpActionAuthority getAuthority(HttpAction action) {
		for (ControllerDescriptor descriptor : action
				.getControllerDescriptors()) {
			HttpActionAuthority authority = getAuthority(
					descriptor.getController(), descriptor.getHttpMethod());
			if (authority != null) {
				return authority;
			}
		}
		return null;
	}

	protected final Map<String, String> getAttributeMap(Authority... authoritys) {
		Map<String, String> attributeMap = new HashMap<String, String>();
		if (authoritys != null) {
			for (Authority authority : authoritys) {
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

	protected ControllerDescriptor getAuthorityControllerDescriptor(
			HttpAction action) {
		for (ControllerDescriptor descriptor : action
				.getControllerDescriptors()) {
			if (descriptor.getHttpMethod() == HttpMethod.GET) {
				return descriptor;
			}
		}

		for (ControllerDescriptor descriptor : action
				.getControllerDescriptors()) {
			return descriptor;
		}
		return null;
	}

}
