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

package scw.websocket.sockjs.frame;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import scw.json.JSONUtils;

/**
 * A Jackson 2.6+ codec for encoding and decoding SockJS messages.
 *
 * <p>It customizes Jackson's default properties with the following ones:
 * <ul>
 * <li>{@link MapperFeature#DEFAULT_VIEW_INCLUSION} is disabled</li>
 * <li>{@link DeserializationFeature#FAIL_ON_UNKNOWN_PROPERTIES} is disabled</li>
 * </ul>
 *
 * <p>Note that Jackson's JSR-310 and Joda-Time support modules will be registered automatically
 * when available (and when Java 8 and Joda-Time themselves are available, respectively).
 *
 * @author Rossen Stoyanchev
 * @since 4.0
 */
public class Jackson2SockJsMessageCodec extends AbstractSockJsMessageCodec {

	public String[] decode(String content) throws IOException {
		return JSONUtils.parseObject(content, String[].class);
	}

	public String[] decodeInputStream(InputStream content) throws IOException {
		return JSONUtils.getJsonSupport().parseObject(new InputStreamReader(content), String[].class);
	}

	protected char[] applyJsonQuoting(String content) {
		return content.toCharArray();
	}

}
