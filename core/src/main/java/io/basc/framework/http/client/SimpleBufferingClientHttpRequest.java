/*
 * Copyright 2002-2018 the original author or authors.
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

package io.basc.framework.http.client;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import io.basc.framework.http.HttpHeaders;
import io.basc.framework.http.HttpMethod;
import io.basc.framework.io.IOUtils;
import io.basc.framework.util.StringUtils;

final class SimpleBufferingClientHttpRequest extends AbstractBufferingClientHttpRequest {

	private final HttpURLConnection connection;

	private final boolean outputStreaming;

	SimpleBufferingClientHttpRequest(HttpURLConnection connection, boolean outputStreaming) {
		this.connection = connection;
		this.outputStreaming = outputStreaming;
	}

	@Override
	public String getRawMethod() {
		return this.connection.getRequestMethod();
	}

	public URI getURI() {
		try {
			return this.connection.getURL().toURI();
		} catch (URISyntaxException ex) {
			throw new IllegalStateException("Could not get HttpURLConnection URI: " + ex.getMessage(), ex);
		}
	}

	@Override
	protected ClientHttpResponse executeInternal(HttpHeaders headers, byte[] bufferedOutput) throws IOException {
		addHeaders(this.connection, headers);
		// JDK <1.8 doesn't support getOutputStream with HTTP DELETE
		if (getMethod() == HttpMethod.DELETE && bufferedOutput.length == 0) {
			this.connection.setDoOutput(false);
		}
		if (this.connection.getDoOutput() && this.outputStreaming) {
			this.connection.setFixedLengthStreamingMode(bufferedOutput.length);
		}
		this.connection.connect();
		if (this.connection.getDoOutput()) {
			IOUtils.copy(bufferedOutput, this.connection.getOutputStream());
		} else {
			// Immediately trigger the request in a no-output scenario as well
			this.connection.getResponseCode();
		}
		return new SimpleClientHttpResponse(this.connection);
	}

	static void addHeaders(HttpURLConnection connection, HttpHeaders headers) {
		for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
			String headerName = entry.getKey();
			if (HttpHeaders.COOKIE.equalsIgnoreCase(headerName)) { // RFC 6265
				String headerValue = StringUtils.collectionToDelimitedString(entry.getValue(), "; ");
				connection.setRequestProperty(headerName, headerValue);
			} else {
				for (String headerValue : entry.getValue()) {
					String actualHeaderValue = headerValue != null ? headerValue : "";
					connection.addRequestProperty(headerName, actualHeaderValue);
				}
			}
		}
	}

}
