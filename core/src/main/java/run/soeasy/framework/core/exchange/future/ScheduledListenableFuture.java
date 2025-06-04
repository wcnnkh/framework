package run.soeasy.framework.core.exchange.future;

import java.util.concurrent.ScheduledFuture;

public interface ScheduledListenableFuture<V> extends ScheduledFuture<V>, ListenableFuture<V> {
}
