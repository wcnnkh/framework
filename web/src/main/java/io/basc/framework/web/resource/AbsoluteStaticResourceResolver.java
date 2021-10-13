package io.basc.framework.web.resource;

import io.basc.framework.util.Assert;
import io.basc.framework.util.ObjectUtils;
import io.basc.framework.util.placeholder.support.SmartPlaceholderReplacer;
import io.basc.framework.web.ServerHttpRequest;
import io.basc.framework.web.WebUtils;

public class AbsoluteStaticResourceResolver implements StaticResourceResolver {
	private final String location;

	public AbsoluteStaticResourceResolver(String location) {
		Assert.requiredArgument(location != null, "location");
		this.location = location;
	}

	public final String getLocation() {
		return location;
	}

	@Override
	public String resolve(ServerHttpRequest request) {
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

		if (obj instanceof AbsoluteStaticResourceResolver) {
			return ObjectUtils.nullSafeEquals(obj, ((AbsoluteStaticResourceResolver) obj).location);
		}
		return false;
	}

	@Override
	public String toString() {
		return location;
	}
}
