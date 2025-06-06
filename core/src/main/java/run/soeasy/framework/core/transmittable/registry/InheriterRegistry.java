package run.soeasy.framework.core.transmittable.registry;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

import run.soeasy.framework.core.spi.Services;
import run.soeasy.framework.core.transmittable.Inheriter;
import run.soeasy.framework.core.transmittable.InheriterBackup;
import run.soeasy.framework.core.transmittable.InheriterCapture;
import run.soeasy.framework.core.transmittable.wrapped.InheritableExecutor;
import run.soeasy.framework.core.transmittable.wrapped.InheritableExecutorService;
import run.soeasy.framework.core.transmittable.wrapped.InheritableScheduledExecutorService;

public class InheriterRegistry<A, B> extends Services<Inheriter<A, B>>
		implements Inheriter<InheriterCapture<A, B>, InheriterBackup<A, B>> {

	@Override
	public InheriterCapture<A, B> capture() {
		InheriterCapture<A, B> capture = new InheriterCapture<>();
		for (Inheriter<A, B> inheriter : this) {
			capture.put(inheriter, inheriter.capture());
		}
		return capture;
	}

	@Override
	public InheriterBackup<A, B> replay(InheriterCapture<A, B> capture) {
		return capture.replay();
	}

	@Override
	public void restore(InheriterBackup<A, B> backup) {
		backup.restore();
	}

	@Override
	public InheriterBackup<A, B> clear() {
		InheriterBackup<A, B> backup = new InheriterBackup<>();
		for (Inheriter<A, B> inheriter : this) {
			backup.put(inheriter, inheriter.clear());
		}
		return backup;
	}

	public Executor inheritable(Executor executor) {
		return new InheritableExecutor<>(executor, this);
	}

	public ExecutorService inheritable(ExecutorService executorService) {
		return new InheritableExecutorService<>(executorService, this);
	}

	public ScheduledExecutorService inheritable(ScheduledExecutorService scheduledExecutorService) {
		return new InheritableScheduledExecutorService<>(scheduledExecutorService, this);
	}
}
