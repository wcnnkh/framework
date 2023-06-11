package io.basc.framework.execution.parameter;

import io.basc.framework.execution.Executable;
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
	boolean canExtractExecutionParameters(Executable executable);

	/**
	 * 提取执行参数
	 * 
	 * @param executable
	 * @return
	 */
	Elements<? extends Object> extractExecutionParameters(Executable executable) throws ParameterException;
}
