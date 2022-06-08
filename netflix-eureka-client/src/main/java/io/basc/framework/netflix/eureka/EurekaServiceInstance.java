package io.basc.framework.netflix.eureka;

import static com.netflix.appinfo.InstanceInfo.PortType.SECURE;

import java.net.URI;
import java.util.Map;
import java.util.Objects;

import com.netflix.appinfo.InstanceInfo;

import io.basc.framework.cloud.ServiceInstance;
import io.basc.framework.core.reflect.ReflectionUtils;
import io.basc.framework.util.Assert;

/**
 * An Eureka-specific {@link ServiceInstance} implementation.
 *
 */
public class EurekaServiceInstance implements ServiceInstance {

	private InstanceInfo instance;

	public EurekaServiceInstance(InstanceInfo instance) {
		Assert.notNull(instance, "Service instance required");
		this.instance = instance;
	}

	public InstanceInfo getInstanceInfo() {
		return instance;
	}

	public String getId() {
		return this.instance.getId();
	}

	public String getName() {
		return this.instance.getAppName();
	}

	public String getHost() {
		return this.instance.getHostName();
	}

	public int getPort() {
		if (isSecure()) {
			return this.instance.getSecurePort();
		}
		return this.instance.getPort();
	}

	public boolean isSecure() {
		// assume if secure is enabled, that is the default
		return this.instance.isPortEnabled(SECURE);
	}

	public URI getUri() {
		String scheme = (isSecure()) ? "https" : "http";
		String uri = String.format("%s://%s:%s", scheme, getHost(), getPort());
		return URI.create(uri);
	}

	@Override
	public Map<String, String> getMetadata() {
		return this.instance.getMetadata();
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		EurekaServiceInstance that = (EurekaServiceInstance) o;
		return Objects.equals(this.instance, that.instance);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.instance);
	}

	@Override
	public String toString() {
		return ReflectionUtils.toString(this);
	}
}
