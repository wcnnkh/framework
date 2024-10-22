package io.basc.framework.util.watch;

import java.nio.file.WatchKey;
import java.util.Iterator;

import io.basc.framework.util.Elements;
import io.basc.framework.util.register.container.ArrayListContainer;
import io.basc.framework.util.register.container.ElementRegistration;

public class WatchKeyRegistry extends ArrayListContainer<WatchKey> {

	public WatchKeyRegistry(int initialCapacity) {
		super(initialCapacity);
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
	public Elements<ElementRegistration<WatchKey>> getElements() {
		return super.getElements().filter((e) -> !e.getPayload().isValid());
	}
}