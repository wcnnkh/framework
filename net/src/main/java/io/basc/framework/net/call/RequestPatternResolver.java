package io.basc.framework.net.call;

import io.basc.framework.core.execution.Executable;
import io.basc.framework.net.RequestPattern;
import io.basc.framework.util.collections.Elements;

public interface RequestPatternResolver {
	boolean canResolve(Executable executable);

	Elements<RequestPattern> resolveRequestPatterns(Executable executable, Object... args);
}
