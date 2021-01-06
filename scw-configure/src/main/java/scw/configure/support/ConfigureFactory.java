package scw.configure.support;

import java.util.SortedSet;
import java.util.TreeSet;

import scw.configure.Configure;
import scw.convert.TypeDescriptor;
import scw.convert.support.ConvertibleConditionalComparator;
import scw.convert.support.ConvertiblePair;
import scw.lang.NotSupportedException;
import scw.lang.Nullable;
import scw.util.XUtils;

public class ConfigureFactory extends ConvertibleConditionalComparator<Object> implements Configure, Comparable<Object> {
	protected final TreeSet<Configure> configures = new TreeSet<Configure>(
			this);
	
	public int compareTo(Object o) {
		for (Configure configure : configures) {
			if (ConvertibleConditionalComparator.INSTANCE.compare(configure, o) == 1) {
				return 1;
			}
		}
		return -1;
	}
	
	public SortedSet<Configure> getConfigures() {
		return XUtils.synchronizedProxy(configures, this);
	}
	
	public final Configure getConfigure(Class<?> sourceType,
			Class<?> targetType) {
		return getConfigure(TypeDescriptor.valueOf(sourceType), TypeDescriptor.valueOf(targetType));
	}

	@Nullable
	public Configure getConfigure(TypeDescriptor sourceType,
			TypeDescriptor targetType) {
		for (Configure configure : configures) {
			if (configure.isSupported(sourceType, targetType)) {
				return configure;
			}
		}
		return null;
	}

	public boolean isSupported(TypeDescriptor sourceType, TypeDescriptor targetType) {
		for (Configure configure : configures) {
			if (configure.isSupported(sourceType, targetType)) {
				return true;
			}
		}
		return false;
	}

	public void configuration(Object source, TypeDescriptor sourceType,
			Object target, TypeDescriptor targetType) {
		for (Configure configure : configures) {
			if (configure.isSupported(sourceType, targetType)) {
				configure.configuration(source, sourceType, target,
						targetType);
				return;
			}
		}
		throw new NotSupportedException(new ConvertiblePair(sourceType.getType(), targetType.getType()).toString());
	}

	public final void configuration(Object source, Object target,
			TypeDescriptor targetType) {
		if (source == null) {
			return;
		}

		configuration(source, TypeDescriptor.forObject(source), target,
				targetType);
	}
	
	public final void configuration(Object source, Object target) {
		if(source == null || target == null){
			return ;
		}
		
		configuration(source, TypeDescriptor.forObject(source), target, TypeDescriptor.forObject(target));
	}

	public void configuration(Object source, Class<?> sourceType,
			Object target, Class<?> targetType) {
		configuration(source, TypeDescriptor.valueOf(sourceType), target, TypeDescriptor.valueOf(targetType));
	}
}
