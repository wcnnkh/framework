package scw.websocket;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import scw.core.utils.CollectionUtils;
import scw.lang.Nullable;
import scw.net.http.HttpHeaders;

public class WebSocketHttpHeaders extends HttpHeaders {

	public static final String SEC_WEBSOCKET_ACCEPT = "Sec-WebSocket-Accept";

	public static final String SEC_WEBSOCKET_EXTENSIONS = "Sec-WebSocket-Extensions";

	public static final String SEC_WEBSOCKET_KEY = "Sec-WebSocket-Key";

	public static final String SEC_WEBSOCKET_PROTOCOL = "Sec-WebSocket-Protocol";

	public static final String SEC_WEBSOCKET_VERSION = "Sec-WebSocket-Version";

	private static final long serialVersionUID = -6644521016187828916L;

	/**
	 * Create a new instance.
	 */
	public WebSocketHttpHeaders() {
		super();
	}

	/**
	 * Create an instance that wraps the given pre-existing HttpHeaders and also
	 * propagate all changes to it.
	 * 
	 * @param headers
	 *            the HTTP headers to wrap
	 */
	public WebSocketHttpHeaders(Map<String, List<String>> headers) {
		super(headers);
	}

	/**
	 * Sets the (new) value of the {@code Sec-WebSocket-Accept} header.
	 * 
	 * @param secWebSocketAccept
	 *            the value of the header
	 */
	public void setSecWebSocketAccept(@Nullable String secWebSocketAccept) {
		set(SEC_WEBSOCKET_ACCEPT, secWebSocketAccept);
	}

	/**
	 * Returns the value of the {@code Sec-WebSocket-Accept} header.
	 * 
	 * @return the value of the header
	 */
	@Nullable
	public String getSecWebSocketAccept() {
		return getFirst(SEC_WEBSOCKET_ACCEPT);
	}

	/**
	 * Returns the value of the {@code Sec-WebSocket-Extensions} header.
	 * 
	 * @return the value of the header
	 */
	public List<WebSocketExtension> getSecWebSocketExtensions() {
		List<String> values = get(SEC_WEBSOCKET_EXTENSIONS);
		if (CollectionUtils.isEmpty(values)) {
			return Collections.emptyList();
		} else {
			List<WebSocketExtension> result = new ArrayList<WebSocketExtension>(values.size());
			for (String value : values) {
				result.addAll(WebSocketExtension.parseExtensions(value));
			}
			return result;
		}
	}

	/**
	 * Sets the (new) value(s) of the {@code Sec-WebSocket-Extensions} header.
	 * 
	 * @param extensions
	 *            the values for the header
	 */
	public void setSecWebSocketExtensions(List<WebSocketExtension> extensions) {
		List<String> result = new ArrayList<String>(extensions.size());
		for (WebSocketExtension extension : extensions) {
			result.add(extension.toString());
		}
		set(SEC_WEBSOCKET_EXTENSIONS, toCommaDelimitedString(result));
	}

	/**
	 * Sets the (new) value of the {@code Sec-WebSocket-Key} header.
	 * 
	 * @param secWebSocketKey
	 *            the value of the header
	 */
	public void setSecWebSocketKey(@Nullable String secWebSocketKey) {
		set(SEC_WEBSOCKET_KEY, secWebSocketKey);
	}

	/**
	 * Returns the value of the {@code Sec-WebSocket-Key} header.
	 * 
	 * @return the value of the header
	 */
	@Nullable
	public String getSecWebSocketKey() {
		return getFirst(SEC_WEBSOCKET_KEY);
	}

	/**
	 * Sets the (new) value of the {@code Sec-WebSocket-Protocol} header.
	 * 
	 * @param secWebSocketProtocol
	 *            the value of the header
	 */
	public void setSecWebSocketProtocol(String secWebSocketProtocol) {
		set(SEC_WEBSOCKET_PROTOCOL, secWebSocketProtocol);
	}

	/**
	 * Sets the (new) value of the {@code Sec-WebSocket-Protocol} header.
	 * 
	 * @param secWebSocketProtocols
	 *            the value of the header
	 */
	public void setSecWebSocketProtocol(List<String> secWebSocketProtocols) {
		set(SEC_WEBSOCKET_PROTOCOL, toCommaDelimitedString(secWebSocketProtocols));
	}

	/**
	 * Returns the value of the {@code Sec-WebSocket-Key} header.
	 * 
	 * @return the value of the header
	 */
	public List<String> getSecWebSocketProtocol() {
		List<String> values = get(SEC_WEBSOCKET_PROTOCOL);
		if (CollectionUtils.isEmpty(values)) {
			return Collections.emptyList();
		} else if (values.size() == 1) {
			return getValuesAsList(SEC_WEBSOCKET_PROTOCOL);
		} else {
			return values;
		}
	}

	/**
	 * Sets the (new) value of the {@code Sec-WebSocket-Version} header.
	 * 
	 * @param secWebSocketVersion
	 *            the value of the header
	 */
	public void setSecWebSocketVersion(@Nullable String secWebSocketVersion) {
		set(SEC_WEBSOCKET_VERSION, secWebSocketVersion);
	}

	/**
	 * Returns the value of the {@code Sec-WebSocket-Version} header.
	 * 
	 * @return the value of the header
	 */
	@Nullable
	public String getSecWebSocketVersion() {
		return getFirst(SEC_WEBSOCKET_VERSION);
	}
}
