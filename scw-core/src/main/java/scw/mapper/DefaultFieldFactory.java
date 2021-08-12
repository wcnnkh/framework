package scw.mapper;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import scw.util.CacheableSupplier;
import scw.util.Supplier;


public class DefaultFieldFactory extends DefaultMetadataFactory implements FieldFactory{
	private final ConcurrentMap<Class<?>, Supplier<FieldMetadata[]>> cacheMap = new ConcurrentHashMap<Class<?>, Supplier<FieldMetadata[]>>();
	
	public DefaultFieldFactory(String[] getterMethodPrefixs, String[] setterMethodPrefixs){
		super(getterMethodPrefixs, setterMethodPrefixs);
	}
	
	@Override
	public List<FieldMetadata> getFieldMetadataList(Class<?> clazz) {
		Supplier<FieldMetadata[]> supplier = cacheMap.get(clazz);
		if(supplier == null){
			Supplier<FieldMetadata[]> newSupplier = new Supplier<FieldMetadata[]>() {
				public FieldMetadata[] get() {
					Collection<FieldMetadata> metadatas = DefaultFieldFactory.super.getFieldMetadataList(clazz);
					return metadatas.toArray(new FieldMetadata[metadatas.size()]);
				}
			};
			newSupplier = new CacheableSupplier<FieldMetadata[]>(newSupplier);
			supplier = cacheMap.putIfAbsent(clazz, newSupplier);
			if(supplier == null){
				supplier = newSupplier;
			}
		}
		return Arrays.asList(supplier.get());
	}
	
	@Override
	public Fields getFields(Class<?> entityClass, Field parentField) {
		return new DefaultFields(parentField, getFieldMetadatas(entityClass));
	}
}