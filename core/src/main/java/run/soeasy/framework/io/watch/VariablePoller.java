package run.soeasy.framework.io.watch;

import java.io.IOException;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.exchange.Publisher;
import run.soeasy.framework.core.exchange.event.ChangeEvent;
import run.soeasy.framework.core.exchange.event.ChangeType;
import run.soeasy.framework.logging.LogManager;
import run.soeasy.framework.logging.Logger;

@RequiredArgsConstructor
@Getter
public class VariablePoller<T extends Variable> extends Poller {
	private static Logger logger = LogManager.getLogger(VariablePoller.class);

	@NonNull
	private final T variable;
	@NonNull
	private final Publisher<? super Elements<ChangeEvent<T>>> changeEventProducer;
	private volatile long lastModified = 0L;

	@Override
	public void run() {
		synchronized (this) {
			long lastModified = 0L;
			try {
				lastModified = variable.lastModified();
			} catch (IOException e) {
				logger.trace(e, "ignore get lastModified");
			}
			touchEvent(lastModified, this.lastModified);
			this.lastModified = lastModified;
		}
	}

	private void touchEvent(long current, long parent) {
		if (current == parent) {
			return;
		}

		ChangeEvent<T> changeEvent;
		if (current == 0) {
			changeEvent = new ChangeEvent<>(variable, ChangeType.DELETE);
		} else if (parent == 0) {
			changeEvent = new ChangeEvent<>(variable, ChangeType.CREATE);
		} else {
			changeEvent = new ChangeEvent<>(variable, ChangeType.UPDATE);
		}
		changeEventProducer.publish(Elements.singleton(changeEvent));
	}
}
