/*
 * Copyright 2002-2015 the original author or authors.
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

import lombok.Getter;
import run.soeasy.framework.http.HttpHeaders;
import run.soeasy.framework.http.client.ClientHttpRequest.ClientHttpRequestWrapper;

/**
 * Simple implementation of {@link ClientHttpRequest} that wraps another
 * request.
 *
 */
@Getter
public final class BufferingClientHttpRequestWrapper<W extends ClientHttpRequest>
		extends AbstractBufferingClientHttpRequest implements ClientHttpRequestWrapper<W> {
	private final W source;

	public BufferingClientHttpRequestWrapper(W source) {
		this.source = source;
	}

	@Override
	protected ClientHttpResponse executeInternal(HttpHeaders headers, byte[] bufferedOutput) throws IOException {
		this.source.getHeaders().putAll(headers);
		source.getOutputStreamPipeline().optional().ifPresent((e) -> e.write(bufferedOutput));
		ClientHttpResponse response = this.source.execute();
		return new BufferingClientHttpResponseWrapper<>(response);
	}

}
