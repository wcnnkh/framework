package scw.rabbit;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.AMQP.BasicProperties;

import scw.core.utils.StringUtils;

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

	public boolean isDelay() {
		return StringUtils.parseBoolean(getHeader(RABBIT_DELAY_MESSAGE));
	}

	public void setDelay(long delay, TimeUnit timeUnit) {
		if (delay <= 0) {
			return;
		}

		if (properties == null) {
			this.properties = new BasicProperties();
		}

		this.properties = properties.builder().expiration("" + timeUnit.toMillis(delay)).build();
		setDelay(true);
	}

	public void setDelay(boolean delay) {
		setHeader(RABBIT_DELAY_MESSAGE, true);
	}

	public void setHeader(String name, Object value) {
		if (properties == null) {
			this.properties = new BasicProperties();
		}

		Map<String, Object> headerMap = properties.getHeaders();
		if (headerMap == null) {
			headerMap = new HashMap<String, Object>();
		}
		headerMap.put(name, value);
		this.properties = properties.builder().headers(headerMap).build();
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

	public void removeHeader(String name) {
		if (properties == null) {
			return;
		}

		Map<String, Object> headerMap = properties.getHeaders();
		if (headerMap == null) {
			return;
		}

		headerMap.remove(name);
	}
}
