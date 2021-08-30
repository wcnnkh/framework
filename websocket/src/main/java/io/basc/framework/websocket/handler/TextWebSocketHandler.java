/*
 * Copyright 2002-2016 the original author or authors.
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

package io.basc.framework.websocket.handler;

import io.basc.framework.messageing.BinaryMessage;
import io.basc.framework.websocket.CloseStatus;
import io.basc.framework.websocket.WebSocketHandler;
import io.basc.framework.websocket.WebSocketSession;

import java.io.IOException;

/**
 * A convenient base class for {@link WebSocketHandler} implementations
 * that process text messages only.
 *
 * <p>Binary messages are rejected with {@link CloseStatus#NOT_ACCEPTABLE}.
 * All other methods have empty implementations.
 *
 * @author Rossen Stoyanchev
 * @author Phillip Webb
 */
public class TextWebSocketHandler extends AbstractWebSocketHandler {

	@Override
	protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {
		try {
			session.close(CloseStatus.NOT_ACCEPTABLE.withReason("Binary messages not supported"));
		}
		catch (IOException ex) {
			// ignore
		}
	}

}
