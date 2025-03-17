package run.soeasy.framework.net.call;

import run.soeasy.framework.core.execution.Executable;
import run.soeasy.framework.net.RequestPattern;
import run.soeasy.framework.util.collections.Elements;

public interface RequestPatternResolver {
	boolean canResolve(Executable executable);

	Elements<RequestPattern> resolveRequestPatterns(Executable executable, Object... args);
}
