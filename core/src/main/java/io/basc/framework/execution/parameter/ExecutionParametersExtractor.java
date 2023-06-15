package io.basc.framework.execution.parameter;

import io.basc.framework.execution.Executor;
import io.basc.framework.util.Elements;

/**
 * 可执行参数提取器
 * 
 * @author wcnnkh
 *
 */
public interface ExecutionParametersExtractor {
	/**
	 * 是否能提取执行参数
	 * 
	 * @param executor
	 * @return
	 */
	boolean canExtractExecutionParameters(Executor executor);

	/**
	 * 提取执行参数
	 * 
	 * @param executor
	 * @return
	 */
	Elements<? extends Object> extractExecutionParameters(Executor executor) throws ParameterException;
}
