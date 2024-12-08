package io.basc.framework.core;

import java.lang.reflect.Executable;

import lombok.NonNull;

public interface ParameterNameDiscoverer {
	/**
	 * Return parameter names for a executable, or {@code null} if they cannot be
	 * determined.
	 * <p>
	 * Individual entries in the array may be {@code null} if parameter names are
	 * only available for some parameters of the given constructor but not for
	 * others. However, it is recommended to use stub parameter names instead
	 * wherever feasible.
	 * 
	 * @param ctor the executable to find parameter names for
	 * @return an array of parameter names if the names can be resolved, or
	 *         {@code null} if they cannot
	 */

	String[] getParameterNames(@NonNull Executable executable);

}
