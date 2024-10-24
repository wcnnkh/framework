/*
 * Copyright 2012-2020 the original author or authors.
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

package io.basc.framework.cloud.commons.util;

import io.basc.framework.env.Environment;
import io.basc.framework.util.StringUtils;
import io.basc.framework.value.PropertyFactory;

/**
 * @author Spencer Gibb
 */
public final class IdUtils {

	private static final String SEPARATOR = ":";

	// @checkstyle:off
	public static final String DEFAULT_SERVICE_ID_STRING = "${vcap.application.name:${application.name:application}}:${vcap.application.instance_index:${application.index:${local.server.port:${server.port:0}}}}:${vcap.application.instance_id:${cachedrandom.${vcap.application.name:${application.name:application}}.value}}";

	// @checkstyle:on

	private IdUtils() {
		throw new IllegalStateException("Can't instantiate a utility class");
	}

	public static String getDefaultInstanceId(PropertyFactory environment) {
		return getDefaultInstanceId(environment, true);
	}

	public static String getDefaultInstanceId(PropertyFactory environment, boolean includeHostname) {
		String vcapInstanceId = environment.getAsString("vcap.application.instance_id");
		if (StringUtils.hasText(vcapInstanceId)) {
			return vcapInstanceId;
		}

		String hostname = null;
		if (includeHostname) {
			hostname = environment.getAsString("cloud.client.hostname");
		}
		String appName = environment.getAsString("application.name");

		String namePart = combineParts(hostname, SEPARATOR, appName);

		String indexPart = environment.get("application.instance_id").orElse(environment.get("server.port"))
				.getAsString();

		return combineParts(namePart, SEPARATOR, indexPart);
	}

	/**
	 * Gets the resolved service id.
	 * 
	 * @param environment A property resolved
	 * @return A unique id that can be used to uniquely identify a service
	 */
	public static String getResolvedServiceId(Environment environment) {
		return environment.replacePlaceholders(getUnresolvedServiceId());
	}

	/**
	 * Gets an the unresolved service id.
	 * 
	 * @return The combination of properties to create a unique service id
	 */
	public static String getUnresolvedServiceId() {
		return DEFAULT_SERVICE_ID_STRING;
	}

	public static String combineParts(String firstPart, String separator, String secondPart) {
		String combined = null;
		if (firstPart != null && secondPart != null) {
			combined = firstPart + separator + secondPart;
		} else if (firstPart != null) {
			combined = firstPart;
		} else if (secondPart != null) {
			combined = secondPart;
		}
		return combined;
	}

}
