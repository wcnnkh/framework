package run.soeasy.framework.core.execution.aop;

import java.util.Iterator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.execution.Function;

@RequiredArgsConstructor
@AllArgsConstructor
@Data
public class ExecutionInterceptorChain implements ExecutionInterceptor {
	@NonNull
	private final Iterator<? extends ExecutionInterceptor> iterator;
	private Function nextChain;

	@Override
	public Object intercept(@NonNull Function function, @NonNull Object... args) throws Throwable {
		if (iterator.hasNext()) {
			return iterator.next().intercept(function, args);
		}
		return nextChain == null ? null : nextChain.execute(args);
	}
}
