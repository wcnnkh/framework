package io.basc.framework.amqp;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.basc.framework.math.NumberHolder;
import io.basc.framework.script.MathScriptEngine;
import io.basc.framework.util.StringUtils;
import io.basc.framework.value.Value;

public class MessageProperties implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;
	private static final String DELAY_MESSAGE = "framework.amqp.message.delay";
	private static final String RETRY_COUNT = "framework.amqp.message.retry.count";
	private static final String MAX_RETRY_COUNT = "framework.amqp.message.retry.max.count";
	private static final String RETRY_DELAY = "framework.amqp.message.retry.delay";
	private static final String RETRY_DELAY_SCRIPT = "framework.amqp.message.retry.delay";
	private static final String RETRY_DELAY_MULTIPLE = "framework.amqp.message.retry.delay.multiple";
	private static final String PUBLISH_ROUTING_KEY = "framework.amqp.message.publish.routingKey";
	private static final String TRANSACTION_MESSAGE_CONFIRM_DELAY_KEY = "framework.amqp.message.transaction.confirm.delay";

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

	@Override
	public MessageProperties clone() {
		MessageProperties properties = new MessageProperties();
		properties.contentType = this.contentType;
		properties.contentEncoding = this.contentEncoding;
		if (headers != null) {
			properties.headers = new LinkedHashMap<String, Object>(headers);
		}
		properties.deliveryMode = this.deliveryMode;
		properties.priority = this.priority;
		properties.correlationId = this.correlationId;
		properties.replyTo = this.replyTo;
		properties.expiration = this.expiration;
		properties.messageId = this.messageId;
		properties.timestamp = this.timestamp;
		properties.type = this.type;
		properties.userId = this.userId;
		properties.appId = this.appId;
		properties.clusterId = this.clusterId;
		return properties;
	}

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

	public Object getHeader(String name) {
		if (headers == null) {
			return null;
		}

		return headers.get(name);
	}

	public Value getHeaderValue(String name) {
		Object value = getHeader(name);
		return Value.of(value);
	}

	public MessageProperties removeHeader(String name) {
		if (headers != null) {
			headers.remove(name);
		}
		return this;
	}

	/** ====================以下为框架支持的方法，并非AMQP协议内容========================== **/

	/**
	 * 获取消息延迟发送时间(毫秒)
	 * 
	 * @return
	 */
	public long getDelay() {
		Object delay = getHeader(DELAY_MESSAGE);
		return delay == null ? 0 : StringUtils.parseLong(delay.toString());
	}

	/**
	 * 设置消息延迟时间
	 * 
	 * @param delay
	 * @param timeUnit
	 * @return
	 */
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

		if (value == null) {
			headers.remove(name);
		} else {
			headers.put(name, value);
		}
		return this;
	}

	public int getRetryCount() {
		Value value = getHeaderValue(RETRY_COUNT);
		return value == null ? 0 : value.getAsInt();
	}

	public void incrRetryCount() {
		setHeader(RETRY_COUNT, getRetryCount() + 1);
	}

	/**
	 * 0表示没的最大重试次数，-1表示不重试
	 * 
	 * @return
	 */
	public int getMaxRetryCount() {
		Value value = getHeaderValue(MAX_RETRY_COUNT);
		return value == null ? 0 : value.getAsInt();
	}

	public void setMaxRetryCount(int maxRetryCount) {
		setHeader(MAX_RETRY_COUNT, maxRetryCount);
	}

	/**
	 * 重试时间的倍数 0表示没有倍数
	 * 
	 * @return
	 */
	public double getRetryDelayMultiple() {
		Value multiple = getHeaderValue(RETRY_DELAY_MULTIPLE);
		return multiple == null ? 0 : multiple.getAsDouble();
	}

	/**
	 * 设置重试时间的倍数
	 * 
	 * @return
	 */
	public MessageProperties setRetryDelayMultiple(double multiple) {
		if (multiple <= 0) {
			removeHeader(RETRY_DELAY_MULTIPLE);
		} else {
			setHeader(RETRY_DELAY_MULTIPLE, multiple);
		}
		return this;
	}

	public long getRetryDelay() {
		Object script = getHeader(RETRY_DELAY_SCRIPT);
		if (script == null) {
			;
			Value value = getHeaderValue(RETRY_DELAY);
			if (value == null) {
				return 0;
			}

			return value.getAsLong();
		}

		MathScriptEngine mathScriptEngine = new MathScriptEngine();
		mathScriptEngine.getResolvers().add(new MathScriptEngine.ObjectFieldScriptResolver(this));
		NumberHolder value = mathScriptEngine.eval(script.toString());
		return value == null ? null : value.toBigDecimal().longValue();
	}

	public void setRetryDelayScript(String script) {
		setHeader(RETRY_DELAY_SCRIPT, script);
	}

	public void setRetryDelay(long delay, TimeUnit timeUnit) {
		setHeader(RETRY_DELAY, timeUnit.toMillis(delay));
	}

	/**
	 * 获取消息发送时的ORIGIN_ROUTING_KEY
	 * 
	 * @return
	 */
	public String getPublishRoutingKey() {
		return StringUtils.toString(getHeader(PUBLISH_ROUTING_KEY), null);
	}

	public void setPublishRoutingKey(String routingKey) {
		setHeader(PUBLISH_ROUTING_KEY, routingKey);
	}

	/**
	 * 获取事务消息确认延迟, 默认10分钟<br/>
	 * 使用在消息发送前先发送延迟消息来保证消息一定会发送成功
	 * 
	 * @return
	 */
	public long getTransactionMessageConfirmDelay() {
		Value value = getHeaderValue(TRANSACTION_MESSAGE_CONFIRM_DELAY_KEY);
		return (value == null || value.isEmpty()) ? TimeUnit.MINUTES.toMillis(10) : value.getAsLong();
	}

	/**
	 * 设置事务消息的确认延迟
	 * 
	 * @param delay
	 * @param timeUnit
	 */
	public void setTransactionMessageConfirmDelay(long delay, TimeUnit timeUnit) {
		setHeader(TRANSACTION_MESSAGE_CONFIRM_DELAY_KEY, timeUnit.toMillis(delay));
	}
}
