package io.basc.framework.websocket.adapter;

import io.basc.framework.websocket.WebSocketSession;

public interface NativeWebSocketSession extends WebSocketSession {
	Object getNativeSession();

	<T> T getNativeSession(Class<T> requiredType);
}
