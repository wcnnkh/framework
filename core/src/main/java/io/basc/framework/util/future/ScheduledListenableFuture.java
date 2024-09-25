package io.basc.framework.util.future;

import java.util.concurrent.ScheduledFuture;

import io.basc.framework.util.actor.ListenableFuture;

public interface ScheduledListenableFuture<V> extends ScheduledFuture<V>, ListenableFuture<V> {
}
