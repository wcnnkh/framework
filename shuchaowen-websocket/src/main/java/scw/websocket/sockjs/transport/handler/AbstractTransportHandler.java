/*
 * Copyright 2002-2017 the original author or authors.
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

package scw.websocket.sockjs.transport.handler;

import java.nio.charset.Charset;

import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.websocket.sockjs.transport.SockJsServiceConfig;
import scw.websocket.sockjs.transport.TransportHandler;

/**
 * Common base class for {@link TransportHandler} implementations.
 *
 * @author Rossen Stoyanchev
 * @since 4.0
 */
public abstract class AbstractTransportHandler implements TransportHandler {

	protected static final Charset UTF8_CHARSET = Charset.forName("UTF-8");

	protected final Logger logger = LoggerUtils.getLogger(getClass());

	private SockJsServiceConfig serviceConfig;


	public void initialize(SockJsServiceConfig serviceConfig) {
		this.serviceConfig = serviceConfig;
	}

	public SockJsServiceConfig getServiceConfig() {
		return this.serviceConfig;
	}

}
