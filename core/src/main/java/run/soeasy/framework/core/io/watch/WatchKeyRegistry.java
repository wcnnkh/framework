package run.soeasy.framework.core.io.watch;

import java.nio.file.WatchKey;
import java.util.Iterator;

import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.exchange.container.ElementRegistration;
import run.soeasy.framework.core.exchange.container.collection.ArrayListContainer;

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
