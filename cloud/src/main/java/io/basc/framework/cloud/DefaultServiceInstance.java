package io.basc.framework.cloud;

import java.io.Serializable;
import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;

import io.basc.framework.core.reflect.ReflectionUtils;
import lombok.Data;

@Data
public class DefaultServiceInstance implements Service, Serializable {
	private static final long serialVersionUID = 1L;

	private String id;

	private String name;

	private String host;

	private int port;

	private boolean secure;

	private Map<String, String> metadata = new LinkedHashMap<String, String>();

	private URI uri;

	private int weight;

	/**
	 * @param id       the id of the instance.
	 * @param name     the id of the service.
	 * @param host     the host where the service instance can be found.
	 * @param port     the port on which the service is running.
	 * @param secure   indicates whether or not the connection needs to be secure.
	 * @param metadata a map containing metadata.
	 */
	public DefaultServiceInstance(String id, String name, String host, int port, boolean secure,
			Map<String, String> metadata) {
		this.id = id;
		this.name = name;
		this.host = host;
		this.port = port;
		this.secure = secure;
		this.metadata = metadata;
	}

	/**
	 * @param id     the id of the instance.
	 * @param name   the id of the service.
	 * @param host   the host where the service instance can be found.
	 * @param port   the port on which the service is running.
	 * @param secure indicates whether or not the connection needs to be secure.
	 */
	public DefaultServiceInstance(String id, String name, String host, int port, boolean secure) {
		this(id, name, host, port, secure, new LinkedHashMap<String, String>());
	}

	public DefaultServiceInstance() {
	}

	/**
	 * Creates a URI from the given ServiceInstance's host:port.
	 * 
	 * @param instance the ServiceInstance.
	 * @return URI of the form (secure)?https:http + "host:port".
	 */
	public static URI getUri(Service instance) {
		String scheme = (instance.isSecure()) ? "https" : "http";
		String uri = String.format("%s://%s:%s", scheme, instance.getHost(), instance.getPort());
		return URI.create(uri);
	}

	public URI getUri() {
		return getUri(this);
	}

	public void setUri(URI uri) {
		this.uri = uri;
		this.host = this.uri.getHost();
		this.port = this.uri.getPort();
		String scheme = this.uri.getScheme();
		if ("https".equals(scheme)) {
			this.secure = true;
		}
	}

	@Override
	public String toString() {
		return ReflectionUtils.toString(this);
	}
}
