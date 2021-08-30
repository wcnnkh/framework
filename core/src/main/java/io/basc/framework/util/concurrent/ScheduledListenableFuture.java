package io.basc.framework.util.concurrent;

import java.util.concurrent.ScheduledFuture;

public interface ScheduledListenableFuture<V> extends ScheduledFuture<V>,
		Listenable<V> {
}
