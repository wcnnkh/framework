package scw.amqp;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import scw.core.utils.StringUtils;

public class MessageProperties implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final String DELAY_MESSAGE = "scw.delay";
	private static final String RETRY_COUNT = "scw.retry.count";

	private String contentType;
	private String contentEncoding;
	private Map<String, Object> headers;
	private Integer deliveryMode;
	private Integer priority;
	private String correlationId;
	private String replyTo;
	private String expiration;
	private String messageId;
	private Date timestamp;
	private String type;
	private String userId;
	private String appId;
	private String clusterId;

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getContentEncoding() {
		return contentEncoding;
	}

	public void setContentEncoding(String contentEncoding) {
		this.contentEncoding = contentEncoding;
	}

	public Map<String, Object> getHeaders() {
		return headers;
	}

	public void setHeaders(Map<String, Object> headers) {
		this.headers = headers;
	}

	public Integer getDeliveryMode() {
		return deliveryMode;
	}

	public void setDeliveryMode(Integer deliveryMode) {
		this.deliveryMode = deliveryMode;
	}

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	public String getCorrelationId() {
		return correlationId;
	}

	public void setCorrelationId(String correlationId) {
		this.correlationId = correlationId;
	}

	public String getReplyTo() {
		return replyTo;
	}

	public void setReplyTo(String replyTo) {
		this.replyTo = replyTo;
	}

	public String getExpiration() {
		return expiration;
	}

	public void setExpiration(Long expiration) {
		this.expiration = expiration == null ? null : ("" + expiration);
	}

	public void setExpiration(String expiration) {
		this.expiration = expiration;
	}

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getClusterId() {
		return clusterId;
	}

	public void setClusterId(String clusterId) {
		this.clusterId = clusterId;
	}

	public long getDelay() {
		Object delay = getHeader(DELAY_MESSAGE);
		return delay == null ? 0 : StringUtils.parseLong(delay.toString());
	}

	public MessageProperties setDelay(long delay, TimeUnit timeUnit) {
		if (delay <= 0) {
			removeHeader(DELAY_MESSAGE);
			setExpiration((String) null);
		} else {
			setExpiration(timeUnit.toMillis(delay));
			setHeader(DELAY_MESSAGE, getExpiration());
		}
		return this;
	}

	public MessageProperties setHeader(String name, Object value) {
		if (headers == null) {
			headers = new HashMap<String, Object>();
		}

		headers.put(name, value);
		return this;
	}

	public Object getHeader(String name) {
		if (headers == null) {
			return null;
		}

		return headers.get(name);
	}

	public MessageProperties removeHeader(String name) {
		if (headers != null) {
			headers.remove(name);
		}
		return this;
	}

	public int getRetryCount() {
		if (headers == null) {
			return 0;
		}

		Object count = headers.get(RETRY_COUNT);
		if (count != null) {
			return StringUtils.parseInt(count.toString());
		}
		return 0;
	}

	public void incrRetryCount() {
		setHeader(RETRY_COUNT, getRetryCount() + 1);
	}
}
