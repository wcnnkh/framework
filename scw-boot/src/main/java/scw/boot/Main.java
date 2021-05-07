package scw.boot;

import scw.env.MainArgs;

public interface Main {
	void main(ConfigurableApplication application, Class<?> mainClass, MainArgs args) throws Throwable;
}
