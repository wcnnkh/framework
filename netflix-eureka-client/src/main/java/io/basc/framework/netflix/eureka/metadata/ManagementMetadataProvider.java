package io.basc.framework.netflix.eureka.metadata;

import io.basc.framework.netflix.eureka.EurekaInstanceConfigBean;

/**
 * Provider for Eureka-specific management metadata.
 *
 */
public interface ManagementMetadataProvider {

	ManagementMetadata get(EurekaInstanceConfigBean instance, int serverPort, String serverContextPath,
			String managementContextPath, Integer managementPort);

}
