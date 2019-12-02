/*
\ * Copyright 2002-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package scw.core.resource;

import java.io.InputStream;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;

import scw.core.Converter;
import scw.core.utils.StringUtils;

/**
 * 资源工具
 * 
 * @author scw
 */
public final class ResourceUtils {
	private ResourceUtils() {
	};

	private static final MultiResourceLookup RESOURCE_LOOKUP = new MultiResourceLookup();
	private static final ResourceOperations RESOURCE_OPERATIONS;

	static {
		RESOURCE_LOOKUP.add(new LocalResourceLookup(false));
		RESOURCE_OPERATIONS = new SystemPropertyMultiSuffixResourceOperations(RESOURCE_LOOKUP);
	}

	/**
	 * @return 结果可能为空
	 */
	public static URL getClassPathURL() {
		URL url = ResourceUtils.class.getResource("/");
		if (url == null) {
			ProtectionDomain protectionDomain = ResourceUtils.class.getProtectionDomain();
			if (protectionDomain != null) {
				CodeSource codeSource = protectionDomain.getCodeSource();
				if (codeSource != null) {
					url = codeSource.getLocation();
				}
			}
		}
		return url;
	}

	public static final ResourceOperations getResourceOperations() {
		return RESOURCE_OPERATIONS;
	}

	public static final MultiResourceLookup getResourceLookup() {
		return RESOURCE_LOOKUP;
	}

	public static <T> T getResource(String resource, Converter<InputStream, T> converter,
			ResourceLookup resourceLookup) {
		if (StringUtils.isEmpty(resource)) {
			return null;
		}

		InputStreamConvertConsumer<T> inputStreamConvertConsumer = new InputStreamConvertConsumer<T>(converter);
		resourceLookup.lookup(resource, inputStreamConvertConsumer);
		return inputStreamConvertConsumer.getValue();
	}
}
