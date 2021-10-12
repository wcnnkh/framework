package io.basc.framework.web.resource;

import io.basc.framework.lang.Nullable;
import io.basc.framework.util.ObjectUtils;
import io.basc.framework.util.placeholder.support.SmartPlaceholderReplacer;
import io.basc.framework.web.ServerHttpRequest;
import io.basc.framework.web.WebUtils;

public class LocalStaticResourceResolver implements StaticResourceResolver {
	private final String location;

	public LocalStaticResourceResolver() {
		this(null);
	}

	public LocalStaticResourceResolver(@Nullable String location) {
		this.location = location;
	}

	@Nullable
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
		return location == null ? 0 : location.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj instanceof LocalStaticResourceResolver) {
			return ObjectUtils.nullSafeEquals(obj, ((LocalStaticResourceResolver) obj).location);
		}
		return false;
	}

	@Override
	public String toString() {
		return location;
	}
}
