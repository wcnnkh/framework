package run.soeasy.framework.aop;

import java.util.Iterator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.invoke.Execution;

@RequiredArgsConstructor
@AllArgsConstructor
@Data
public class ExecutionInterceptorChain implements ExecutionInterceptor {
	@NonNull
	private final Iterator<? extends ExecutionInterceptor> iterator;
	private Execution nextChain;

	@Override
	public Object intercept(@NonNull Execution function, @NonNull Object... args) throws Throwable {
		if (iterator.hasNext()) {
			return iterator.next().intercept(function, args);
		}
		return nextChain == null ? null : nextChain.execute(args);
	}
}
