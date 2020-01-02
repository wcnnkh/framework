package scw.oas.support;

import java.util.List;

import scw.oas.ApiInfo;
import scw.oas.ApiParameter;

public class SimpleApiInfo extends SimpleApiDescription implements ApiInfo {
	private static final long serialVersionUID = 1L;
	private String requestContentType;
	private String responseContentType;
	private List<? extends ApiParameter> requestParameterList;
	private List<? extends ApiParameter> responseParameterList;
	private List<? extends ApiInfo> subApiInfoList;

	public String getRequestContentType() {
		return requestContentType;
	}

	public void setRequestContentType(String requestContentType) {
		this.requestContentType = requestContentType;
	}

	public String getResponseContentType() {
		return responseContentType;
	}

	public void setResponseContentType(String responseContentType) {
		this.responseContentType = responseContentType;
	}

	public List<? extends ApiParameter> getRequestParameterList() {
		return requestParameterList;
	}

	public void setRequestParameterList(List<? extends ApiParameter> requestParameterList) {
		this.requestParameterList = requestParameterList;
	}

	public List<? extends ApiParameter> getResponseParameterList() {
		return responseParameterList;
	}

	public void setResponseParameterList(List<? extends ApiParameter> responseParameterList) {
		this.responseParameterList = responseParameterList;
	}

	public List<? extends ApiInfo> getSubApiInfoList() {
		return subApiInfoList;
	}

	public void setSubApiInfoList(List<? extends ApiInfo> subApiInfoList) {
		this.subApiInfoList = subApiInfoList;
	}
}
