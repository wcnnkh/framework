package io.basc.framework.amqp;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.basc.framework.core.convert.Any;
import io.basc.framework.core.convert.strings.StringConverter;
import io.basc.framework.script.MathScriptEngine;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.math.NumberHolder;
import lombok.Data;

@Data
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

	public MessageProperties() {
	}

	public MessageProperties(MessageProperties messageProperties) {
		this.contentType = messageProperties.contentType;
		this.contentEncoding = messageProperties.contentEncoding;
		if (messageProperties.headers != null) {
			this.headers = new LinkedHashMap<String, Object>(messageProperties.headers);
		}
		this.deliveryMode = messageProperties.deliveryMode;
		this.priority = messageProperties.priority;
		this.correlationId = messageProperties.correlationId;
		this.replyTo = messageProperties.replyTo;
		this.expiration = messageProperties.expiration;
		this.messageId = messageProperties.messageId;
		this.timestamp = messageProperties.timestamp;
		this.type = messageProperties.type;
		this.userId = messageProperties.userId;
		this.appId = messageProperties.appId;
		this.clusterId = messageProperties.clusterId;
	}

	@Override
	public MessageProperties clone() {
		return new MessageProperties(this);
	}

	public void setLongExpiration(Long expiration) {
		this.expiration = expiration == null ? null : ("" + expiration);
	}

	public Object getHeader(String name) {
		if (headers == null) {
			return null;
		}

		return headers.get(name);
	}

	public Any getHeaderValue(String name) {
		Object value = getHeader(name);
		return Any.of(value);
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
		return delay == null ? 0 : StringConverter.parseLong(delay.toString());
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
			setLongExpiration(timeUnit.toMillis(delay));
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
		Any value = getHeaderValue(RETRY_COUNT);
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
		Any value = getHeaderValue(MAX_RETRY_COUNT);
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
		Any multiple = getHeaderValue(RETRY_DELAY_MULTIPLE);
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
			Any value = getHeaderValue(RETRY_DELAY);
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
	 * 获取事务消息确认延迟, 默认10分钟 使用在消息发送前先发送延迟消息来保证消息一定会发送成功
	 * 
	 * @return
	 */
	public long getTransactionMessageConfirmDelay() {
		Any value = getHeaderValue(TRANSACTION_MESSAGE_CONFIRM_DELAY_KEY);
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
