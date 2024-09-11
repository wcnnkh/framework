package io.basc.framework.util.observe.future;

import java.util.concurrent.ScheduledFuture;

public interface ScheduledListenableFuture<V> extends ScheduledFuture<V>, ListenableFuture<V> {
}
