package io.basc.framework.boot;

import io.basc.framework.env.MainArgs;

public interface Main {
	void main(ConfigurableApplication application, Class<?> mainClass, MainArgs args) throws Throwable;
}
