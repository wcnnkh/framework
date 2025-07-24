package run.soeasy.framework.aop;

import java.util.Iterator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.execute.Execution;

/**
 * 执行拦截拦截器链，实现{@link ExecutionInterceptor}接口，用于用于管理多个拦截器的执行顺序，
 * 形成拦截器调用链，支持在目标执行前后按序执行多个拦截逻辑，是AOP框架中实现拦截器链式调用的核心组件。
 * 
 * <p>
 * 该类通过迭代器（{@link Iterator}）管理拦截器集合，在拦截过程中依次触发每个拦截器，
 * 当所有拦截器执行完毕后，通过{@code nextChain}执行最终的目标逻辑，实现"拦截器链+目标执行"的完整流程。
 * 
 * @author soeasy.run
 * @see ExecutionInterceptor
 * @see Execution
 */
@RequiredArgsConstructor
@AllArgsConstructor
@Data
public class ExecutionInterceptorChain implements ExecutionInterceptor {

	/**
	 * 拦截器迭代器（非空），用于遍历并执行链中的每个拦截器
	 */
	@NonNull
	private final Iterator<? extends ExecutionInterceptor> iterator;

	/**
	 * 链的下一个执行节点（通常为目标执行逻辑或下一级拦截链），可为null
	 */
	private Execution nextChain;

	/**
	 * 执行拦截器链，依次触发每个拦截器并最终执行目标逻辑
	 * 
	 * <p>
	 * 处理逻辑： 1.
	 * 若迭代器存在下一个拦截器，调用该拦截器的{@link ExecutionInterceptor#intercept(Execution)}方法，
	 * 并将当前执行上下文传递给它（拦截器内部可通过{@link Execution#proceed()}继续触发后续流程）； 2.
	 * 若所有拦截器已执行完毕（迭代器无下一个元素），则执行{@code nextChain}（目标逻辑），
	 * 若{@code nextChain}为null，返回null。
	 * 
	 * @param execution 执行上下文（非空，包含执行所需的所有信息）
	 * @return 执行结果（经过所有拦截器处理后的目标逻辑返回值）
	 * @throws Throwable 拦截过程中任意拦截器或目标逻辑抛出的异常
	 */
	@Override
	public Object intercept(@NonNull Execution execution) throws Throwable {
		if (iterator.hasNext()) {
			// 存在下一个拦截器，执行该拦截器
			return iterator.next().intercept(execution);
		}
		// 所有拦截器执行完毕，执行下一个节点（目标逻辑）
		return nextChain == null ? null : nextChain.execute();
	}
}