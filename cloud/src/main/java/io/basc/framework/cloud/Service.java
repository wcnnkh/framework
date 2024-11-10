package io.basc.framework.cloud;

import java.net.URI;
import java.util.Map;

import io.basc.framework.cloud.loadbalancer.Node;
import io.basc.framework.convert.lang.ValueWrapper;

/**
 * Represents an instance of a service in a discovery system.
 * 
 * @author wcnnkh
 *
 */
public interface Service extends Node {
	String getHost();

	int getPort();

	boolean isSecure();

	URI getUri();

	Map<String, String> getMetadata();

	@Override
	default int getWeight() {
		Map<String, String> map = getMetadata();
		return map == null ? 1 : ValueWrapper.of(map.get("weight")).or(1).getAsInt();
	}
}
