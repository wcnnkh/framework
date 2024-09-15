package io.basc.framework.util.watch;

import java.nio.file.WatchKey;
import java.util.ArrayList;
import java.util.Iterator;

import io.basc.framework.util.Elements;
import io.basc.framework.util.Publisher;
import io.basc.framework.util.event.ChangeEvent;
import io.basc.framework.util.register.container.ArrayListRegistry;
import io.basc.framework.util.register.container.ElementRegistration;
import lombok.NonNull;

public class WatchKeyRegistry extends ArrayListRegistry<WatchKey> {

	public WatchKeyRegistry(int initialCapacity,
			@NonNull Publisher<? super Elements<ChangeEvent<WatchKey>>> changeEventsProducter) {
		super(() -> new ArrayList<>(initialCapacity), changeEventsProducter);
	}

	@Override
	public void cleanup() {
		super.cleanup();

		// 清理无效的WatchKey
		execute((collection) -> {
			Iterator<ElementRegistration<WatchKey>> iterator = collection.iterator();
			while (iterator.hasNext()) {
				ElementRegistration<WatchKey> registration = iterator.next();
				if (registration.getPayload().isValid()) {
					iterator.remove();
				}
			}
			return true;
		});
	}

	@Override
	public Elements<WatchKey> getElements() {
		return super.getElements().filter((e) -> !e.isValid());
	}
}
