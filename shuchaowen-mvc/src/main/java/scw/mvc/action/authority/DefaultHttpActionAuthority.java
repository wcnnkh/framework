package scw.mvc.action.authority;

import java.util.Map;

import scw.net.http.HttpMethod;
import scw.security.authority.http.DefaultHttpAuthority;

public class DefaultHttpActionAuthority extends DefaultHttpAuthority implements
		HttpActionAuthority {
	private static final long serialVersionUID = 1L;
	private boolean action;

	public DefaultHttpActionAuthority(String id, String parentId, String name,
			Map<String, String> attributeMap, String path,
			HttpMethod httpMethod, boolean action) {
		super(id, parentId, name, attributeMap, path, httpMethod);
		this.action = action;
	}

	public boolean isAction() {
		return action;
	}

}
