package io.basc.framework.util.register;

public interface PayloadRegistration<T> extends Registration {
	T getPayload();
}