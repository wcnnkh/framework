package io.basc.framework.data;

import java.util.concurrent.TimeUnit;

import io.basc.framework.data.kv.TemporaryKeyOperations;

public interface TemporaryCounter extends Counter, TemporaryKeyOperations<String> {

	long incr(String key, long delta, long initialValue, long exp, TimeUnit expUnit);

	long decr(String key, long delta, long initialValue, long exp, TimeUnit expUnit);
}
