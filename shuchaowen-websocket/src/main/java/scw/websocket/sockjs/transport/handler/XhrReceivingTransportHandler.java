/*
 * Copyright 2002-2013 the original author or authors.
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

import java.io.IOException;

import scw.http.HttpStatus;
import scw.http.server.ServerHttpRequest;
import scw.websocket.sockjs.transport.TransportHandler;
import scw.websocket.sockjs.transport.TransportType;

/**
 * A {@link TransportHandler} that receives messages over HTTP.
 *
 * @author Rossen Stoyanchev
 * @since 4.0
 */
public class XhrReceivingTransportHandler extends AbstractHttpReceivingTransportHandler {

	public TransportType getTransportType() {
		return TransportType.XHR_SEND;
	}

	@Override
	protected String[] readMessages(ServerHttpRequest request) throws IOException {
		return getServiceConfig().getMessageCodec().decodeInputStream(request.getBody());
	}

	@Override
	protected HttpStatus getResponseStatus() {
		return HttpStatus.NO_CONTENT;
	}

}
