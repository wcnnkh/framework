package io.basc.framework.cloud.loadbalancer;

import io.basc.framework.util.Weighted;

public interface Server<T> extends Weighted {
	String getId();

	T getService();
}