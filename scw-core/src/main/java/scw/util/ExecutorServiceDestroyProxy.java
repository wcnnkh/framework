package scw.util;

import java.util.concurrent.ExecutorService;

import scw.beans.Destroy;

public interface ExecutorServiceDestroyProxy<T extends ExecutorService> extends ExecutorService, Destroy {
	T getTargetExecutorService();
}
