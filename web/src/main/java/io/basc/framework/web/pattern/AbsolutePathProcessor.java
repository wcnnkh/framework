package io.basc.framework.web.pattern;

import io.basc.framework.util.Assert;
import io.basc.framework.util.ObjectUtils;
import io.basc.framework.util.placeholder.support.SmartPlaceholderReplacer;
import io.basc.framework.util.stream.Processor;
import io.basc.framework.web.ServerHttpRequest;
import io.basc.framework.web.WebException;
import io.basc.framework.web.WebUtils;

public class AbsolutePathProcessor implements Processor<ServerHttpRequest, String, WebException> {
	private final String location;

	public AbsolutePathProcessor(String location) {
		Assert.requiredArgument(location != null, "location");
		this.location = location;
	}

	public final String getLocation() {
		return location;
	}

	@Override
	public String process(ServerHttpRequest request) throws WebException {
		StringBuilder sb = new StringBuilder();
		if (location != null) {
			sb.append("/");
			sb.append(SmartPlaceholderReplacer.NON_STRICT_REPLACER.replacePlaceholders(location,
					(name) -> WebUtils.getRestfulParameterMap(request).get(name)));
		}

		sb.append("/");
		sb.append(request.getPath());
		return sb.toString();
	}

	@Override
	public int hashCode() {
		return location.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj instanceof AbsolutePathProcessor) {
			return ObjectUtils.nullSafeEquals(obj, ((AbsolutePathProcessor) obj).location);
		}
		return false;
	}

	@Override
	public String toString() {
		return location;
	}
}
