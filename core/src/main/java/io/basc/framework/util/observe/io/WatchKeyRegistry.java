package io.basc.framework.util.observe.io;

import java.nio.file.WatchKey;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import io.basc.framework.util.Elements;
import io.basc.framework.util.event.EventPublishService;
import io.basc.framework.util.observe.ChangeEvent;
import io.basc.framework.util.register.container.ElementRegistration;
import io.basc.framework.util.register.container.ElementRegistry;
import lombok.NonNull;

public class WatchKeyRegistry extends ElementRegistry<WatchKey, Collection<ElementRegistration<WatchKey>>> {

	public WatchKeyRegistry(int initialCapacity,
			@NonNull EventPublishService<ChangeEvent<WatchKey>> eventPublishService) {
		super(() -> new ArrayList<>(initialCapacity), eventPublishService);
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
