package scw.instance.supplier;

import scw.instance.NoArgsInstanceFactory;
import scw.util.Supplier;

public class ClassInstanceSupplier<T> implements Supplier<T> {
	private final NoArgsInstanceFactory instanceFactory;
	private final Class<T> clazz;
	
	public ClassInstanceSupplier(NoArgsInstanceFactory instanceFactory, Class<T> clazz){
		this.instanceFactory = instanceFactory;
		this.clazz = clazz;
	}
	
	public T get() {
		return instanceFactory.getInstance(clazz);
	}
}
