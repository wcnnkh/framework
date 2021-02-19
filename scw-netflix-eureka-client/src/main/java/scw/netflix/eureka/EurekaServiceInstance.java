/*
 * Copyright 2013-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package scw.netflix.eureka;

import static com.netflix.appinfo.InstanceInfo.PortType.SECURE;

import java.net.URI;
import java.util.Map;
import java.util.Objects;

import scw.core.Assert;
import scw.discovery.ServiceInstance;
import scw.mapper.MapperUtils;

import com.netflix.appinfo.InstanceInfo;

/**
 * An Eureka-specific {@link ServiceInstance} implementation.
 *
 * @author Spencer Gibb
 * @author Dave Syer
 * @author Olga Maciaszek-Sharma
 * @author Tim Ysewyn
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
		return MapperUtils.getMapper().getFields(EurekaServiceInstance.class).getValueMap(this).toString();
	}
}
