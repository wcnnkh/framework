package io.basc.framework.cloud;

import java.net.URI;
import java.util.Map;

/**
 * Represents an instance of a service in a discovery system.
 * 
 * @author wcnnkh
 *
 */
public interface ServiceInstance {
	String getId();

	String getName();

	String getHost();

	int getPort();

	boolean isSecure();

	URI getUri();

	Map<String, String> getMetadata();
}
