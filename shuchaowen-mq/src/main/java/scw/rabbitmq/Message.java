package scw.rabbitmq;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import scw.core.utils.StringUtils;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.AMQP.BasicProperties;

public class Message {
	private static final String RABBIT_DELAY_MESSAGE = "scw.rabbit.delay.message";
	private byte[] body;
	private AMQP.BasicProperties properties;

	public Message(byte[] body) {
		this(body, null);
	}

	public Message(byte[] body, AMQP.BasicProperties properties) {
		this.body = body;
		this.properties = properties;
	}

	public byte[] getBody() {
		return body;
	}

	public AMQP.BasicProperties getProperties() {
		return properties;
	}

	public long getDelay() {
		Object delay = getHeader(RABBIT_DELAY_MESSAGE);
		return delay == null ? 0 : StringUtils.parseLong(delay.toString());
	}

	public Message setDelay(long delay, TimeUnit timeUnit) {
		if (delay <= 0) {
			removeHeader(RABBIT_DELAY_MESSAGE);
			if (properties != null) {
				properties.builder().expiration(null).build();
			}
			setHeader(RABBIT_DELAY_MESSAGE, timeUnit.toMillis(delay));
		} else {
			if (properties == null) {
				this.properties = new BasicProperties();
			}
			this.properties = properties.builder()
					.expiration("" + timeUnit.toMillis(delay)).build();
			setHeader(RABBIT_DELAY_MESSAGE, timeUnit.toMillis(delay));
		}
		return this;
	}

	public Message setHeader(String name, Object value) {
		if (properties == null) {
			this.properties = new BasicProperties();
		}

		Map<String, Object> headerMap = properties.getHeaders();
		if (headerMap == null) {
			headerMap = new HashMap<String, Object>();
		}else{
			headerMap = new HashMap<String, Object>(headerMap);
		}
		headerMap.put(name, value);
		this.properties = properties.builder().headers(headerMap).build();
		return this;
	}

	public Object getHeader(String name) {
		if (properties == null) {
			return null;
		}

		Map<String, Object> headers = properties.getHeaders();
		if (headers != null) {
			return headers.get(name);
		}
		return null;
	}

	public Message removeHeader(String name) {
		if (properties != null) {
			Map<String, Object> headerMap = properties.getHeaders();
			if (headerMap != null) {
				headerMap = new HashMap<String, Object>(headerMap);
				headerMap.remove(name);
				this.properties = properties.builder().headers(headerMap).build();
			}
		}
		return this;
	}
}
