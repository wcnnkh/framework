package io.basc.framework.net.server.mvc;

import java.util.Collections;
import java.util.Map;

import io.basc.framework.net.MimeTypes;
import io.basc.framework.net.Request;
import io.basc.framework.util.Assert;
import io.basc.framework.util.ObjectUtils;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.match.AntPathMatcher;
import io.basc.framework.util.match.PathMatcher;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class PathController implements Controller, Cloneable {
	private static final PathMatcher DEFAULT_PATH_MATCHER = new AntPathMatcher();
	@NonNull
	private PathMatcher pathMatcher = DEFAULT_PATH_MATCHER;
	private final String path;
	private MimeTypes consumes;
	private MimeTypes produces;

	public PathController(String path) {
		Assert.requiredArgument(path != null, "path");
		this.path = path;
	}

	public PathMatcher getPathMatcher() {
		return pathMatcher == null ? DEFAULT_PATH_MATCHER : pathMatcher;
	}

	public String getPath() {
		return path;
	}

	public MimeTypes getConsumes() {
		return consumes == null ? MimeTypes.EMPTY : consumes;
	}

	public boolean hasConsumes() {
		return consumes != null && !consumes.isEmpty();
	}

	public MimeTypes getProduces() {
		return produces == null ? MimeTypes.EMPTY : produces;
	}

	public boolean hasProduces() {
		return produces != null && !produces.isEmpty();
	}

	public boolean isPattern() {
		if (path == null) {
			return true;
		}
		return getPathMatcher().isPattern(path);
	}

	@Override
	public boolean test(Request request) {
		if (consumes != null && !consumes.isCompatibleWith(request.getContentType())) {
			return false;
		}

		if (path != null) {
			String requestPath = request.getURI().getPath();
			if (isPattern()) {
				if (getPathMatcher().match(path, requestPath)) {
					return true;
				}
				return false;
			} else {
				if (!StringUtils.equals(path, requestPath)) {
					return false;
				}
			}
		}
		return true;
	}

	public Map<String, String> extractUriTemplateVariables(String path) {
		if (path == null) {
			return Collections.emptyMap();
		}

		return getPathMatcher().extractUriTemplateVariables(this.path, path);
	}

	@Override
	public int hashCode() {
		int code = 0;
		if (path != null) {
			code += path.hashCode();
		}

		if (consumes != null) {
			code += consumes.hashCode();
		}
		return code;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj instanceof PathController) {
			PathController pathController = (PathController) obj;
			if (!ObjectUtils.equals(consumes, pathController.consumes)) {
				return false;
			}

			if (StringUtils.isNotEmpty(path) && StringUtils.isNotEmpty(pathController.path)) {
				if (getPathMatcher().match(path, pathController.path)
						&& getPathMatcher().match(pathController.path, path)) {
					return true;
				}

				if (pathController.getPathMatcher().match(path, pathController.path)
						&& pathController.getPathMatcher().match(pathController.path, path)) {
					return true;
				}
			}
			return ObjectUtils.equals(path, pathController.path);
		}
		return false;
	}

	@Override
	public String toString() {
		if (path == null && consumes == null) {
			return "[ANY]";
		}

		StringBuilder sb = new StringBuilder();
		if (path != null) {
			if (sb.length() > 0) {
				sb.append(" ");
			}
			sb.append(path);
		}

		if (consumes != null) {
			if (sb.length() > 0) {
				sb.append(" ");
			}
			sb.append(consumes);
		}
		return sb.toString();
	}

	@Override
	public PathController clone() {
		PathController pathController = new PathController(path);
		return pathController;
	}

	@Override
	public int compareTo(Controller o) {
		if (this.equals(o)) {
			return 0;
		}

		if (o instanceof PathController) {
			PathController controller = (PathController) o;
			if (path != null && controller.path != null) {
				if (getPathMatcher().match(controller.path, path)
						|| controller.getPathMatcher().match(controller.path, path)) {
					return -1;
				}

				if (getPathMatcher().match(path, controller.path)
						|| controller.getPathMatcher().match(path, controller.path)) {
					return 1;
				}
			}

			if (controller.consumes != null && this.consumes != null) {
				return this.consumes.compareTo(controller.consumes);
			}

			if (path == null && controller.path == null) {
				return 0;
			}

			if (path == null) {
				return -1;
			}

			if (controller.path == null) {
				return 1;
			}
			return path.compareTo(controller.path);
		}
		return -1;
	}
}
