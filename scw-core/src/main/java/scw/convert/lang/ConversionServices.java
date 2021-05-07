package scw.convert.lang;

import java.util.TreeSet;

import scw.convert.ConfigurableConversionService;
import scw.convert.ConversionService;
import scw.convert.ConversionServiceAware;
import scw.convert.ConverterNotFoundException;
import scw.convert.TypeDescriptor;
import scw.lang.NamedThreadLocal;

public class ConversionServices extends ConvertibleConditionalComparator<Object>
		implements ConfigurableConversionService, Comparable<Object> {
	private static final ThreadLocal<ConversionService> NESTING = new NamedThreadLocal<ConversionService>(ConversionServices.class.getName());
	
	/**
	 * 处理嵌套使用的情况
	 * @param conversionService
	 */
	public static void setNesting(ConversionService conversionService) {
		NESTING.set(conversionService);
	}
	
	private final TreeSet<ConversionService> conversionServices = new TreeSet<ConversionService>(this);

	public void addConversionService(ConversionService conversionService) {
		if (conversionService instanceof ConversionServiceAware) {
			((ConversionServiceAware) conversionService).setConversionService(this);
		}

		synchronized (conversionServices) {
			conversionServices.add(conversionService);
		}
	}

	public boolean canConvert(TypeDescriptor sourceType, TypeDescriptor targetType) {
		for (ConversionService service : conversionServices) {
			if(service == NESTING.get()) {
				continue;
			}
			
			try {
				if (service.canConvert(sourceType, targetType)) {
					return true;
				}
			} finally {
				NESTING.remove();
			}
		}
		return canDirectlyConvert(sourceType, targetType);
	}

	public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
		for (ConversionService service : conversionServices) {
			if(service == NESTING.get()) {
				continue;
			}
			
			try {
				if (!service.canConvert(sourceType, targetType)) {
					continue;
				}
			} finally {
				NESTING.remove();
			}
			
			return service.convert(source, sourceType, targetType);
		}

		if (canDirectlyConvert(sourceType, targetType)) {
			return source;
		}

		throw new ConverterNotFoundException(sourceType, targetType);
	}

	public int compareTo(Object o) {
		for (ConversionService service : conversionServices) {
			if (ConvertibleConditionalComparator.INSTANCE.compare(service, o) == 1) {
				return 1;
			}
		}
		return -1;
	}
}
