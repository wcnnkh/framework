package io.basc.framework.mapper.support;

import io.basc.framework.convert.RecursiveConversionService;
import io.basc.framework.util.Assert;

public class Copy extends DefaultObjectMapper {

	/**
	 * 浅拷贝
	 */
	public static final Copy SHALLOW = new Copy();
	/**
	 * 深拷贝
	 */
	public static final Copy DEEP = new Copy();
	static {
		DEEP.getMappingStrategy().setConversionService(new RecursiveConversionService(DEEP));
	}

	/**
	 * 深拷贝
	 * 
	 * @see #DEEP
	 * @param source
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T clone(T source) {
		return (T) DEEP.convert(source, source.getClass());
	}

	/**
	 * 浅拷贝
	 * 
	 * @see #SHALLOW
	 * @param <T>
	 * @param targetClass
	 * @param source
	 * @return
	 */
	public static <T> T copy(Object source, Class<? extends T> targetClass) {
		Assert.requiredArgument(source != null, "source");
		Assert.requiredArgument(targetClass != null, "targetClass");
		return SHALLOW.convert(source, targetClass);
	}

	/**
	 * 浅拷贝
	 * 
	 * @see #SHALLOW
	 * @param <T>
	 * @param source
	 * @param target
	 * @return
	 */
	public static <T> T copy(Object source, T target) {
		Assert.requiredArgument(source != null, "source");
		Assert.requiredArgument(target != null, "target");
		SHALLOW.transform(source, target);
		return target;
	}
}
