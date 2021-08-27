package io.basc.framework.websocket;

import java.util.List;
/**
 * 支持RFC 6455中定义的子协议的WebSocket处理程序的接口。<br/>
 * An interface for WebSocket handlers that support sub-protocols as defined in RFC 6455.
 * @author shuchaowen
 *
 */
public interface SubProtocolCapable {
	/**
	 * Return the list of supported sub-protocols.
	 */
	List<String> getSubProtocols();
}
