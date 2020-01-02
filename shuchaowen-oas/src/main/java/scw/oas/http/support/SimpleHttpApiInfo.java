package scw.oas.http.support;

import java.util.List;

import scw.oas.http.HttpApiInfo;
import scw.oas.support.SimpleApiInfo;

public class SimpleHttpApiInfo extends SimpleApiInfo implements HttpApiInfo {
	private static final long serialVersionUID = 1L;
	private String[] methods;
	private String path;

	@SuppressWarnings("unchecked")
	@Override
	public List<? extends HttpApiInfo> getSubApiInfoList() {
		return (List<? extends HttpApiInfo>) super.getSubApiInfoList();
	}

	public String[] getMethods() {
		return methods;
	}

	public void setMethods(String[] methods) {
		this.methods = methods;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
}
