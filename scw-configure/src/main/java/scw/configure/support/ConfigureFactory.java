package scw.configure.support;

import java.util.Collections;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import scw.configure.Configure;
import scw.convert.TypeDescriptor;
import scw.convert.support.ConversionServiceFactory;
import scw.convert.support.ConvertiblePair;
import scw.core.instance.InstanceUtils;
import scw.core.instance.NoArgsInstanceFactory;
import scw.lang.NotSupportedException;
import scw.lang.Nullable;
import scw.value.property.BasePropertyFactory;

public class ConfigureFactory extends ConversionServiceFactory implements
		Configure {
	private final TreeSet<Configure> configurations = new TreeSet<Configure>(
			this);

	public ConfigureFactory() {
		this(InstanceUtils.INSTANCE_FACTORY);
	}

	public ConfigureFactory(NoArgsInstanceFactory instanceFactory) {
		configurations.add(new MapConfigure(this));
		configurations.add(new PropertyFactoryConfigure(this));
		getConversionServices().add(
				new ConfigureConversionService(this, instanceFactory,
						Collections.singleton(new ConvertiblePair(Map.class,
								Object.class))));
		getConversionServices().add(
				new ConfigureConversionService(this, instanceFactory,
						Collections.singleton(new ConvertiblePair(
								BasePropertyFactory.class, Object.class))));
	}
	
	public SortedSet<Configure> getConfigurations() {
		return Collections.synchronizedSortedSet(configurations);
	}
	
	public final Configure getConfiguration(Class<?> sourceType,
			Class<?> targetType) {
		return getConfiguration(TypeDescriptor.valueOf(sourceType), TypeDescriptor.valueOf(targetType));
	}

	@Nullable
	public Configure getConfiguration(TypeDescriptor sourceType,
			TypeDescriptor targetType) {
		for (Configure configuration : configurations) {
			if (configuration.matches(sourceType, targetType)) {
				return configuration;
			}
		}
		return null;
	}

	public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
		for (Configure configuration : configurations) {
			if (configuration.matches(sourceType, targetType)) {
				return true;
			}
		}
		return false;
	}

	public void configuration(Object source, TypeDescriptor sourceType,
			Object target, TypeDescriptor targetType) {
		for (Configure configuration : configurations) {
			if (configuration.matches(sourceType, targetType)) {
				configuration.configuration(source, sourceType, target,
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

	public void configuration(Object source, Class<?> sourceType,
			Object target, Class<?> targetType) {
		configuration(source, TypeDescriptor.valueOf(sourceType), target, TypeDescriptor.valueOf(targetType));;
	}
}
