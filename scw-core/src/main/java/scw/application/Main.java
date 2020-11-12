package scw.application;

import scw.aop.annotation.AopEnable;

@AopEnable(false)
public interface Main {
	void main(Application application, Class<?> mainClass, MainArgs mainArgs) throws Throwable;
}
