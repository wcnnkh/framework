package scw.oas.support;

import java.util.List;

import scw.oas.ApiDocument;
import scw.oas.ApiInfo;

public class SimpleApiDocument extends SimpleApiDescription implements ApiDocument {
	private static final long serialVersionUID = 1L;
	private List<? extends ApiInfo> apiInfoList;

	public List<? extends ApiInfo> getApiInfoList() {
		return apiInfoList;
	}

	public void setApiInfoList(List<? extends ApiInfo> apiInfoList) {
		this.apiInfoList = apiInfoList;
	}
}
