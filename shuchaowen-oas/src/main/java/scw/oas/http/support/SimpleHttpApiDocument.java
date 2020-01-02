package scw.oas.http.support;

import java.util.List;

import scw.oas.http.HttpApiDocument;
import scw.oas.http.HttpApiInfo;
import scw.oas.support.SimpleApiDocument;

public class SimpleHttpApiDocument extends SimpleApiDocument implements HttpApiDocument {
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unchecked")
	@Override
	public List<? extends HttpApiInfo> getApiInfoList() {
		return (List<? extends HttpApiInfo>) super.getApiInfoList();
	}
}
