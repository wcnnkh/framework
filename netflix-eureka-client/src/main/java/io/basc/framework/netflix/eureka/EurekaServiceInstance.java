package io.basc.framework.netflix.eureka;

import static com.netflix.appinfo.InstanceInfo.PortType.SECURE;

import java.net.URI;
import java.util.Map;

import com.netflix.appinfo.InstanceInfo;

import io.basc.framework.cloud.Service;
import io.basc.framework.util.Assert;
import lombok.Data;

/**
 * An Eureka-specific {@link ServiceInstance} implementation.
 *
 */
@Data
public class EurekaServiceInstance implements Service {

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
}
