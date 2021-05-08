package scw.convert.lang;

import java.util.LinkedList;
import java.util.TreeSet;

import scw.convert.ConfigurableConversionService;
import scw.convert.ConversionException;
import scw.convert.ConversionService;
import scw.convert.ConversionServiceAware;
import scw.convert.ConverterNotFoundException;
import scw.convert.TypeDescriptor;
import scw.core.utils.ObjectUtils;
import scw.lang.NamedThreadLocal;

public class ConversionServices extends
		ConvertibleConditionalComparator<Object> implements
		ConfigurableConversionService, Comparable<Object> {
	private static final ThreadLocal<LinkedList<ConversionService>> NESTING = new NamedThreadLocal<LinkedList<ConversionService>>(
			ConversionServices.class.getName());

	/**
	 * 处理嵌套使用的情况
	 * 
	 * @param conversionService
	 */
	public static void setNesting(ConversionService conversionService) {
		LinkedList<ConversionService> list = NESTING.get();
		if (list == null) {
			list = new LinkedList<ConversionService>();
			NESTING.set(list);
		}
		list.add(conversionService);
	}

	public static void removeNesting(ConversionService conversionService) {
		LinkedList<ConversionService> list = NESTING.get();
		if (list == null) {
			throw new ConversionException("remove nesting conversion service "
					+ conversionService);
		}

		ConversionService nesting = list.getLast();
		if (!ObjectUtils.nullSafeEquals(conversionService, nesting)) {
			throw new ConversionException("remove nesting [" + nesting
					+ "] conversion service [" + conversionService + "]");
		}

		list.removeLast();
		if (list.isEmpty()) {
			NESTING.remove();
		} else {
			NESTING.set(list);
		}
	}

	private static boolean isIgnore(ConversionService conversionService) {
		LinkedList<ConversionService> list = NESTING.get();
		if (list == null) {
			return false;
		}

		return ObjectUtils.nullSafeEquals(conversionService, list.getLast());
	}

	private final TreeSet<ConversionService> conversionServices = new TreeSet<ConversionService>(
			this);

	public void addConversionService(ConversionService conversionService) {
		if (conversionService instanceof ConversionServiceAware) {
			((ConversionServiceAware) conversionService)
					.setConversionService(this);
		}

		synchronized (conversionServices) {
			conversionServices.add(conversionService);
		}
	}

	public boolean canConvert(TypeDescriptor sourceType,
			TypeDescriptor targetType) {
		for (ConversionService service : conversionServices) {
			if (isIgnore(service)) {
				continue;
			}

			if (service.canConvert(sourceType, targetType)) {
				return true;
			}
		}
		return canDirectlyConvert(sourceType, targetType);
	}

	public Object convert(Object source, TypeDescriptor sourceType,
			TypeDescriptor targetType) {
		for (ConversionService service : conversionServices) {
			if (isIgnore(service)) {
				continue;
			}

			if (service.canConvert(sourceType, targetType)) {
				return service.convert(source, sourceType, targetType);
			}
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
