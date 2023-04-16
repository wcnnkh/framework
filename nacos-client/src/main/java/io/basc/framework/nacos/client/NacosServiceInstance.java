package io.basc.framework.nacos.client;

import java.net.URI;
import java.util.Map;

import com.alibaba.nacos.api.naming.pojo.Instance;

import io.basc.framework.cloud.Service;
import io.basc.framework.core.reflect.ReflectionUtils;

public class NacosServiceInstance implements Service {
	private final Instance instance;

	public NacosServiceInstance(Instance instance) {
		this.instance = instance;
	}

	public Instance getInstance() {
		return instance;
	}

	public String getId() {
		return instance.getInstanceId();
	}

	public String getName() {
		return instance.getServiceName();
	}

	public String getHost() {
		return instance.getIp();
	}

	public int getPort() {
		return instance.getPort();
	}

	public boolean isSecure() {
		return getPort() == 443;
	}

	public URI getUri() {
		String scheme = (isSecure()) ? "https" : "http";
		String uri = String.format("%s://%s:%s", scheme, getHost(), getPort());
		return URI.create(uri);
	}

	public Map<String, String> getMetadata() {
		return instance.getMetadata();
	}

	@Override
	public String toString() {
		return ReflectionUtils.toString(this);
	}
}
