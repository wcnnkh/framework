package scw.security.authority.http;

import java.io.Serializable;
import java.util.List;

public class SimpleTreeHttpAuthority implements TreeHttpAuthority, Serializable {
	private static final long serialVersionUID = 1L;
	private HttpAuthority httpAuthority;
	private List<TreeHttpAuthority> subList;

	public HttpAuthority getHttpAuthority() {
		return httpAuthority;
	}

	public void setHttpAuthority(HttpAuthority httpAuthority) {
		this.httpAuthority = httpAuthority;
	}

	public List<TreeHttpAuthority> getSubList() {
		return subList;
	}

	public void setSubList(List<TreeHttpAuthority> subList) {
		this.subList = subList;
	}
}
