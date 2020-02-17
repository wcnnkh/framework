package scw.mvc.logger.db;

import java.io.Serializable;
import java.util.Map;

import scw.db.cache.TemporaryCacheEnable;
import scw.net.http.Method;
import scw.orm.annotation.PrimaryKey;
import scw.orm.sql.annotation.Column;
import scw.orm.sql.annotation.CreateTime;
import scw.orm.sql.annotation.Generator;
import scw.orm.sql.annotation.SequenceId;
import scw.orm.sql.annotation.Table;

@Table
@TemporaryCacheEnable(false)
public class LogTable implements Serializable{
	private static final long serialVersionUID = 1L;
	@PrimaryKey
	@Generator
	@SequenceId
	private String logId;
	private String identification;
	@Column(length=1000, nullAble=false)
	private String controller;
	@Column(length=1000, nullAble=false)
	private String requestController;
	private Method httpMethod;
	private String requestContentType;
	@Column(type="text")
	private String requestBody;
	private String responseContentType;
	@Column(type="text")
	private String responseBody;
	private String errorMessage;
	@Generator
	@CreateTime
	private long createTime;
	private long executeTime;
	@Column(length=5000)
	private Map<String, String> attributeMap;
	public String getLogId() {
		return logId;
	}
	public void setLogId(String logId) {
		this.logId = logId;
	}
	public String getIdentification() {
		return identification;
	}
	public void setIdentification(String identification) {
		this.identification = identification;
	}
	public String getController() {
		return controller;
	}
	public void setController(String controller) {
		this.controller = controller;
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
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public long getCreateTime() {
		return createTime;
	}
	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}
	public String getRequestController() {
		return requestController;
	}
	public void setRequestController(String requestController) {
		this.requestController = requestController;
	}
	public long getExecuteTime() {
		return executeTime;
	}
	public void setExecuteTime(long executeTime) {
		this.executeTime = executeTime;
	}
	public Map<String, String> getAttributeMap() {
		return attributeMap;
	}
	public void setAttributeMap(Map<String, String> attributeMap) {
		this.attributeMap = attributeMap;
	}
}
