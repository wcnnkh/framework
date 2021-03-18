package scw.locks;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;

import scw.util.CacheableSupplier;
import scw.util.Supplier;

public abstract class MemoryLockFactory implements LockFactory {
	private volatile ConcurrentHashMap<String, Supplier<Lock>> lockMap = new ConcurrentHashMap<String, Supplier<Lock>>();

	public final Lock getLock(String name) {
		Supplier<Lock> supplier = lockMap.get(name);
		if (supplier == null) {
			Supplier<Lock> newSupplier = new LockSupplier(name);
			newSupplier = new CacheableSupplier<Lock>(newSupplier);
			supplier = lockMap.putIfAbsent(name, newSupplier);
			if (supplier == null) {
				supplier = newSupplier;
			}
		}
		return supplier.get();
	}

	protected abstract Lock createLock(String name);
	
	private final class LockSupplier implements Supplier<Lock>{
		private final String name;
		
		public LockSupplier(String name){
			this.name = name;
		}
		
		public Lock get() {
			return createLock(name);
		}
	}
}
