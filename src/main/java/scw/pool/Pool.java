package scw.pool;

import java.lang.reflect.Proxy;
import java.util.concurrent.atomic.AtomicInteger;

public final class Pool {
	private static final Class<?>[] PROXY_INTERFACES = new Class<?>[] { ItemProxy.class };

	private final int minSize;
	private final ItemProxy[] items;
	private final AtomicInteger readIndex = new AtomicInteger(-1);
	private final ItemFactory itemFactory;
	private volatile boolean lazyInit = false;

	public Pool(int minSize, int maxSize, ItemFactory itemFactory) {
		this.minSize = minSize;
		this.items = new ItemProxy[maxSize];
		this.itemFactory = itemFactory;
	}

	private void init() {
		if (!lazyInit) {
			synchronized (this) {
				if (!lazyInit) {
					lazyInit = true;
					for (int i = 0; i < minSize; i++) {
						ItemProxy itemProxy = create(i);
						items[i] = itemProxy;
					}
				}
			}
		}
	}

	private ItemProxy create(int index) {
		Item item = itemFactory.create();
		return (ItemProxy) Proxy.newProxyInstance(
				ItemProxy.class.getClassLoader(), PROXY_INTERFACES,
				new ItemProxyInvocationHandler(index, item));
	}

	private Item next() {
		int index = readIndex.incrementAndGet();
		if (index < 0) {
			index = -index;
		}

		if (index > items.length) {
			index = index / items.length;
		}

		return items[index];
	}

	public void destroy(ItemProxy item) {
		item.destroy();
		items[item.getPoolIndex()] = item;
	}

	public Item getItem() {
		init();
		Item item = next();
		while (!item.isAvailable()) {
			item = next();
		}

		item.reset();
		return item;
	}
}
