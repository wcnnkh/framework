package scw.mvc.logger;

import java.io.Serializable;
import java.util.Map;

import scw.net.http.Method;

public class LogQuery implements Serializable {
	private static final long serialVersionUID = 1L;
	private String identification;
	private Map<String, String> attributeMap;
	private String controller;
	private String requestController;
	private Method httpMethod;
	private String requestContentType;
	private String requestBody;
	private String responseContentType;
	private String responseBody;

	public String getIdentification() {
		return identification;
	}

	public void setIdentification(String identification) {
		this.identification = identification;
	}

	public Map<String, String> getAttributeMap() {
		return attributeMap;
	}

	public void setAttributeMap(Map<String, String> attributeMap) {
		this.attributeMap = attributeMap;
	}

	public String getController() {
		return controller;
	}

	public void setController(String controller) {
		this.controller = controller;
	}

	public String getRequestController() {
		return requestController;
	}

	public void setRequestController(String requestController) {
		this.requestController = requestController;
	}

	public Method getHttpMethod() {
		return httpMethod;
	}

	public void setHttpMethod(Method httpMethod) {
		this.httpMethod = httpMethod;
	}

	public String getRequestContentType() {
		return requestContentType;
	}

	public void setRequestContentType(String requestContentType) {
		this.requestContentType = requestContentType;
	}

	public String getRequestBody() {
		return requestBody;
	}

	public void setRequestBody(String requestBody) {
		this.requestBody = requestBody;
	}

	public String getResponseContentType() {
		return responseContentType;
	}

	public void setResponseContentType(String responseContentType) {
		this.responseContentType = responseContentType;
	}

	public String getResponseBody() {
		return responseBody;
	}

	public void setResponseBody(String responseBody) {
		this.responseBody = responseBody;
	}
}
