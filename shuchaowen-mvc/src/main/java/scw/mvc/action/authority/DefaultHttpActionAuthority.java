package scw.mvc.action.authority;

import java.util.Map;

import scw.net.http.HttpMethod;
import scw.security.authority.http.DefaultHttpAuthority;

public class DefaultHttpActionAuthority extends DefaultHttpAuthority implements
		HttpActionAuthority {
	private static final long serialVersionUID = 1L;
	private boolean menuAction;

	public DefaultHttpActionAuthority(String id, String parentId, String name,
			Map<String, String> attributeMap, String path,
			HttpMethod httpMethod, boolean menuAction) {
		super(id, parentId, name, attributeMap, path, httpMethod);
		this.menuAction = menuAction;
	}

	public boolean isMenuAction() {
		return menuAction;
	}

}
