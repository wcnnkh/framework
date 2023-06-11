package io.basc.framework.execution.parameter;

import io.basc.framework.execution.Executor;
import io.basc.framework.util.Elements;

/**
 * 可执行参数提取器
 * 
 * @author wcnnkh
 *
 */
public interface ExecutableParametersExtractor {
	/**
	 * 是否能提取执行参数
	 * 
	 * @param executable
	 * @return
	 */
	boolean canExtractExecutionParameters(Executor executable);

	/**
	 * 提取执行参数
	 * 
	 * @param executable
	 * @return
	 */
	Elements<? extends Object> extractExecutionParameters(Executor executable) throws ParameterException;
}
