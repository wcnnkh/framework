package scw.mapper;

import java.util.Collection;

import scw.core.Assert;
import scw.util.cache.CacheLoader;
import scw.util.cache.LocalCacheType;

public class DefaultMapper extends Mapper {
	private Collection<String> getterMethodPrefix;
	private Collection<String> setterMethodPrefix;

	public DefaultMapper(Collection<String> getterMethodPrefix,
			Collection<String> setterMethodPrefix, LocalCacheType localCacheType) {
		super(localCacheType);
		Assert.notEmpty(getterMethodPrefix);
		Assert.notEmpty(setterMethodPrefix);
		this.getterMethodPrefix = getterMethodPrefix;
		this.setterMethodPrefix = setterMethodPrefix;
	}
	
	
	@Override
	protected CacheLoader<Class<?>, FieldMetadata[]> createCacheLoader(
			Class<?> clazz) {
		return new DefaultFieldLoader(getterMethodPrefix, setterMethodPrefix);
	}
}
