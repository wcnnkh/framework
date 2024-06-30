package io.basc.framework.http.uri;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.basc.framework.net.Headers;
import io.basc.framework.net.Request;
import io.basc.framework.net.uri.UriComponentsBuilder;
import io.basc.framework.util.Assert;
import io.basc.framework.util.StringUtils;

public class HttpUriComponentsBuilder extends UriComponentsBuilder {
	private static final String HTTP_PATTERN = "(?i)(http|https):";
	private static final Pattern HTTP_URL_PATTERN = Pattern.compile("^" + HTTP_PATTERN + "(//(" + USERINFO_PATTERN
			+ "@)?" + HOST_PATTERN + "(:" + PORT_PATTERN + ")?" + ")?" + PATH_PATTERN + "(\\?" + LAST_PATTERN + ")?");
	private static final Pattern FORWARDED_HOST_PATTERN = Pattern.compile("host=\"?([^;,\"]+)\"?");

	private static final Pattern FORWARDED_PROTO_PATTERN = Pattern.compile("proto=\"?([^;,\"]+)\"?");

	/**
	 * Create a new {@code UriComponents} object from the URI associated with the
	 * given HttpRequest while also overlaying with values from the headers
	 * "Forwarded" (<a href="https://tools.ietf.org/html/rfc7239">RFC 7239</a>), or
	 * "X-Forwarded-Host", "X-Forwarded-Port", and "X-Forwarded-Proto" if
	 * "Forwarded" is not found.
	 * <p>
	 * <strong>Note:</strong> this method uses values from forwarded headers, if
	 * present, in order to reflect the client-originated protocol and address.
	 * Consider using the {@code ForwardedHeaderFilter} in order to choose from a
	 * central place whether to extract and use, or to discard such headers.
	 * 
	 * @param request the source request
	 * @return the URI components of the URI
	 */
	public static HttpUriComponentsBuilder fromHttpRequest(Request request) {
		HttpUriComponentsBuilder builder = new HttpUriComponentsBuilder();
		builder.uri(request.getURI());
		builder.adaptFromForwardedHeaders(request.getHeaders());
		return builder;
	}

	/**
	 * Create a URI components builder from the given HTTP URL String.
	 * <p>
	 * <strong>Note:</strong> The presence of reserved characters can prevent
	 * correct parsing of the URI string. For example if a query parameter contains
	 * {@code '='} or {@code '&'} characters, the query string cannot be parsed
	 * unambiguously. Such values should be substituted for URI variables to enable
	 * correct parsing:
	 * 
	 * <pre class="code">
	 * String urlString = &quot;https://example.com/hotels/42?filter={value}&quot;;
	 * UriComponentsBuilder.fromHttpUrl(urlString).buildAndExpand(&quot;hot&amp;cold&quot;);
	 * </pre>
	 * 
	 * @param httpUrl the source URI
	 * @return the URI components of the URI
	 */
	public static HttpUriComponentsBuilder fromHttpUrl(String httpUrl) {
		Assert.notNull(httpUrl, "HTTP URL must not be null");
		Matcher matcher = HTTP_URL_PATTERN.matcher(httpUrl);
		if (matcher.matches()) {
			HttpUriComponentsBuilder builder = new HttpUriComponentsBuilder();
			String scheme = matcher.group(1);
			builder.scheme(scheme != null ? scheme.toLowerCase() : null);
			builder.userInfo(matcher.group(4));
			String host = matcher.group(5);
			if (StringUtils.isNotEmpty(scheme) && StringUtils.isEmpty(host)) {
				throw new IllegalArgumentException("[" + httpUrl + "] is not a valid HTTP URL");
			}
			builder.host(host);
			String port = matcher.group(7);
			if (StringUtils.isNotEmpty(port)) {
				builder.port(port);
			}
			builder.path(matcher.group(8));
			builder.query(matcher.group(10));
			return builder;
		} else {
			throw new IllegalArgumentException("[" + httpUrl + "] is not a valid HTTP URL");
		}
	}

	/**
	 * Adapt this builder's scheme+host+port from the given headers, specifically
	 * "Forwarded" (<a href="https://tools.ietf.org/html/rfc7239">RFC 7239</a>, or
	 * "X-Forwarded-Host", "X-Forwarded-Port", and "X-Forwarded-Proto" if
	 * "Forwarded" is not found.
	 * 
	 * @param headers the HTTP headers to consider
	 * @return this UriComponentsBuilder
	 */
	HttpUriComponentsBuilder adaptFromForwardedHeaders(Headers headers) {
		try {
			String forwardedHeader = headers.getFirst("Forwarded");
			if (StringUtils.hasText(forwardedHeader)) {
				String forwardedToUse = StringUtils.tokenizeToArray(forwardedHeader, ",")[0];
				Matcher matcher = FORWARDED_PROTO_PATTERN.matcher(forwardedToUse);
				if (matcher.find()) {
					scheme(matcher.group(1).trim());
					port(null);
				}
				matcher = FORWARDED_HOST_PATTERN.matcher(forwardedToUse);
				if (matcher.find()) {
					adaptForwardedHost(matcher.group(1).trim());
				}
			} else {
				String protocolHeader = headers.getFirst("X-Forwarded-Proto");
				if (StringUtils.hasText(protocolHeader)) {
					scheme(StringUtils.tokenizeToArray(protocolHeader, ",")[0]);
					port(null);
				}

				String hostHeader = headers.getFirst("X-Forwarded-Host");
				if (StringUtils.hasText(hostHeader)) {
					adaptForwardedHost(StringUtils.tokenizeToArray(hostHeader, ",")[0]);
				}

				String portHeader = headers.getFirst("X-Forwarded-Port");
				if (StringUtils.hasText(portHeader)) {
					port(Integer.parseInt(StringUtils.tokenizeToArray(portHeader, ",")[0]));
				}
			}
		} catch (NumberFormatException ex) {
			throw new IllegalArgumentException("Failed to parse a port from \"forwarded\"-type headers. "
					+ "If not behind a trusted proxy, consider using ForwardedHeaderFilter "
					+ "with the removeOnly=true. Request headers: " + headers);
		}

		if (this.scheme != null && ((this.scheme.equals("http") && "80".equals(this.port))
				|| (this.scheme.equals("https") && "443".equals(this.port)))) {
			port(null);
		}

		return this;
	}

	private void adaptForwardedHost(String hostToUse) {
		int portSeparatorIdx = hostToUse.lastIndexOf(':');
		if (portSeparatorIdx > hostToUse.lastIndexOf(']')) {
			host(hostToUse.substring(0, portSeparatorIdx));
			port(Integer.parseInt(hostToUse.substring(portSeparatorIdx + 1)));
		} else {
			host(hostToUse);
			port(null);
		}
	}
}
