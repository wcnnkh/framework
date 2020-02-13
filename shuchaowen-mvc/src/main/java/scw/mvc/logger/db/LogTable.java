package scw.mvc.logger.db;

import java.io.Serializable;

import scw.db.cache.TemporaryCacheEnable;
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
	@Column(length=1000)
	private String controller;
	private String httpMethod;
	private String requestContentType;
	@Column(type="text")
	private String requestBody;
	private String responseContentType;
	@Column(type="text")
	private String responseBody;
	private String errorMessage;
	@CreateTime
	private long createTime;
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
	public String getHttpMethod() {
		return httpMethod;
	}
	public void setHttpMethod(String httpMethod) {
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
}
