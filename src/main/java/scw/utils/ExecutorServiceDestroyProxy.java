package scw.utils;

import java.util.concurrent.ExecutorService;

import scw.core.Destroy;

public interface ExecutorServiceDestroyProxy<T extends ExecutorService> extends ExecutorService, Destroy {
	T getTargetExecutorService();
}
