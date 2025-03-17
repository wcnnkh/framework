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

package run.soeasy.framework.http.client;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

import run.soeasy.framework.http.HttpHeaders;
import run.soeasy.framework.util.StringUtils;
import run.soeasy.framework.util.io.IOUtils;

final class SimpleClientHttpResponse implements ClientHttpResponse {

	private final HttpURLConnection connection;

	private HttpHeaders headers;

	private InputStream responseStream;

	SimpleClientHttpResponse(HttpURLConnection connection) {
		this.connection = connection;
	}

	public int getRawStatusCode() throws IOException {
		return this.connection.getResponseCode();
	}

	public String getStatusText() throws IOException {
		return this.connection.getResponseMessage();
	}

	public HttpHeaders getHeaders() {
		if (this.headers == null) {
			this.headers = new HttpHeaders();
			// Header field 0 is the status line for most HttpURLConnections,
			// but not on GAE
			String name = this.connection.getHeaderFieldKey(0);
			if (StringUtils.isNotEmpty(name)) {
				this.headers.add(name, this.connection.getHeaderField(0));
			}
			int i = 1;
			while (true) {
				name = this.connection.getHeaderFieldKey(i);
				if (StringUtils.isEmpty(name)) {
					break;
				}
				this.headers.add(name, this.connection.getHeaderField(i));
				i++;
			}
		}
		return this.headers;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		if (this.responseStream == null) {
			InputStream errorStream = this.connection.getErrorStream();
			this.responseStream = (errorStream != null ? errorStream : this.connection.getInputStream());
		}
		return this.responseStream;
	}

	public void close() {
		if (this.responseStream != null) {
			try {
				IOUtils.drain(responseStream);
				this.responseStream.close();
			} catch (Exception ex) {
				// ignore
			}
		}
	}

}
