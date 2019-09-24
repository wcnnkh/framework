package scw.mvc.http.authority;

import java.io.Serializable;
import java.util.List;

public class SimpleTreeHttpAuthority implements TreeHttpAuthority, Serializable {
	private static final long serialVersionUID = 1L;
	private Authority httpAuthority;
	private List<TreeHttpAuthority> subList;

	public Authority getHttpAuthority() {
		return httpAuthority;
	}

	public void setHttpAuthority(Authority httpAuthority) {
		this.httpAuthority = httpAuthority;
	}

	public List<TreeHttpAuthority> getSubList() {
		return subList;
	}

	public void setSubList(List<TreeHttpAuthority> subList) {
		this.subList = subList;
	}
}
