package io.basc.framework.mvc.security;

import io.basc.framework.context.annotation.Provider;
import io.basc.framework.http.HttpCookie;
import io.basc.framework.mvc.HttpChannel;
import io.basc.framework.value.EmptyValue;
import io.basc.framework.value.StringValue;
import io.basc.framework.value.Value;
import io.basc.framework.web.WebUtils;

@Provider
public class DefaultUserSessionResolver implements UserSessionResolver {

	public <T> T getUid(HttpChannel httpChannel, Class<T> type) {
		Value value = getParameter(httpChannel, UID_NAME);
		return value == null ? EmptyValue.INSTANCE.getAsObject(type) : value
				.getAsObject(type);
	}

	public String getSessionId(HttpChannel httpChannel) {
		Value value = getParameter(httpChannel, TOKEN_NAME);
		return value == null ? null : value.getAsString();
	}

	protected Value getParameter(HttpChannel httpChannel, String name) {
		Value value = httpChannel.getValue(name);
		if (value == null || value.isEmpty()) {
			String token = httpChannel.getRequest().getHeaders().getFirst(name);
			if (token == null) {
				HttpCookie httpCookie = WebUtils.getCookie(
						httpChannel.getRequest(), name);
				if (httpCookie != null) {
					token = httpCookie.getValue();
				}
			}

			if (token != null) {
				value = new StringValue(token);
			}
		}
		return value;
	}
}
