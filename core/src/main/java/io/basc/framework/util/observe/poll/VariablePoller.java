package io.basc.framework.util.observe.poll;

import java.io.IOException;

import io.basc.framework.util.Elements;
import io.basc.framework.util.logging.Logger;
import io.basc.framework.util.logging.LoggerFactory;
import io.basc.framework.util.observe.Publisher;
import io.basc.framework.util.observe.event.ChangeEvent;
import io.basc.framework.util.observe.event.ChangeType;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class VariablePoller<T extends Variable> extends Pollable {
	private static Logger logger = LoggerFactory.getLogger(VariablePoller.class);

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
