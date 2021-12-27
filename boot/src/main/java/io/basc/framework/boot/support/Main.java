package io.basc.framework.boot.support;

import io.basc.framework.boot.ConfigurableApplication;
import io.basc.framework.env.MainArgs;

/**
 * @see MainApplication#postProcessApplication(ConfigurableApplication)
 * @author wcnnkh
 *
 */
public interface Main {
	void main(ConfigurableApplication application, Class<?> mainClass, MainArgs args) throws Throwable;
}
