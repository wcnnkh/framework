package io.basc.framework.websocket;

import java.util.List;

/**
 * 支持RFC 6455中定义的子协议的WebSocket处理程序的接口。 An interface for WebSocket handlers that
 * support sub-protocols as defined in RFC 6455.
 * 
 * @author wcnnkh
 *
 */
public interface SubProtocolCapable {
	List<String> getSubProtocols();
}
