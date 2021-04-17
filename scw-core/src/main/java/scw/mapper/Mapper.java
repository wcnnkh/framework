package scw.mapper;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import scw.util.CacheableSupplier;
import scw.util.Supplier;


public class Mapper extends DefaultMetadataFactory{
	private final ConcurrentMap<Class<?>, Supplier<FieldMetadata[]>> cacheMap = new ConcurrentHashMap<Class<?>, Supplier<FieldMetadata[]>>();
	
	public Mapper(String[] getterMethodPrefixs, String[] setterMethodPrefixs){
		super(getterMethodPrefixs, setterMethodPrefixs);
	}
	
	@Override
	public Collection<FieldMetadata> getFieldMetadatas(final Class<?> clazz) {
		Supplier<FieldMetadata[]> supplier = cacheMap.get(clazz);
		if(supplier == null){
			Supplier<FieldMetadata[]> newSupplier = new Supplier<FieldMetadata[]>() {
				public FieldMetadata[] get() {
					Collection<FieldMetadata> metadatas = Mapper.super.getFieldMetadatas(clazz);
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
	
	/**
	 * 获取一个类所有的字段，使用父类
	 * @param entityClass
	 * @return
	 */
	public final Fields getFields(Class<?> entityClass) {
		return getFields(entityClass, true, null);
	}
	
	/**
	 * 获取一个类所有的字段
	 * @param entityClass
	 * @param useSuperClass 是否使用父类
	 * @return
	 */
	public final Fields getFields(Class<?> entityClass, boolean useSuperClass) {
		return getFields(entityClass, useSuperClass, null);
	}

	/**
	 * 获取一个类所有的字段
	 * @param entityClass
	 * @param parentField 父级字段
	 * @return
	 */
	public final Fields getFields(Class<?> entityClass, Field parentField) {
		return getFields(entityClass, true, parentField);
	}

	/**
	 * 获取一个类所有的字段
	 * @param entityClass 实体类
	 * @param useSuperClass 是否使用父类
	 * @param parentField 父级字段
	 * @return
	 */
	public Fields getFields(Class<?> entityClass, boolean useSuperClass, Field parentField) {
		return new DefaultFields(this, entityClass, useSuperClass, parentField);
	}

	public final <T> T mapping(Class<T> entityClass, Field parentField, Mapping mapping) {
		return mapping.mapping(entityClass, getFields(entityClass, parentField).accept(FieldFeature.SUPPORT_SETTER).accept(mapping), this);
	}

	public final <T> T mapping(Class<T> entityClass, Mapping mapping) {
		return mapping(entityClass, null, mapping);
	}
}