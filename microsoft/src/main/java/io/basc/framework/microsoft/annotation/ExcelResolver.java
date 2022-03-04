package io.basc.framework.microsoft.annotation;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.reflect.ReflectionApi;
import io.basc.framework.env.Sys;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.mapper.FieldDescriptor;
import io.basc.framework.microsoft.ExcelReader;
import io.basc.framework.orm.EntityStructure;
import io.basc.framework.orm.Property;
import io.basc.framework.orm.support.DefaultObjectRelationalMapping;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.XUtils;
import io.basc.framework.util.stream.ResponsiveIterator;

public class ExcelResolver extends DefaultObjectRelationalMapping {
	private static Logger logger = LoggerFactory.getLogger(ExcelResolver.class);
	private static ExecutorService executor = Executors.newCachedThreadPool();

	static {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				executor.shutdown();
			}
		});
	}

	private ConversionService conversionService;

	public ConversionService getConversionService() {
		return conversionService == null ? Sys.env.getConversionService() : conversionService;
	}

	public void setConversionService(ConversionService conversionService) {
		this.conversionService = conversionService;
	}

	@Override
	public Boolean isIgnore(Class<?> entityClass, FieldDescriptor fieldDescriptor) {
		return !fieldDescriptor.isAnnotationPresent(ExcelColumn.class);
	}

	@Override
	public Collection<String> getAliasNames(Class<?> entityClass, FieldDescriptor fieldDescriptor) {
		ExcelColumn excelColumn = fieldDescriptor.getAnnotation(ExcelColumn.class);
		if (excelColumn == null) {
			return super.getAliasNames(entityClass, fieldDescriptor);
		}

		String[] alias = excelColumn.alias();
		if (alias == null || alias.length == 0) {
			return StringUtils.isEmpty(excelColumn.value()) ? super.getAliasNames(entityClass, fieldDescriptor)
					: Arrays.asList(excelColumn.value());
		}
		return Arrays.asList(alias);
	}

	@Override
	public String getName(Class<?> entityClass, FieldDescriptor fieldDescriptor) {
		ExcelColumn excelColumn = fieldDescriptor.getAnnotation(ExcelColumn.class);
		if (excelColumn == null || StringUtils.isEmpty(excelColumn.value())) {
			return super.getName(entityClass, fieldDescriptor);
		}
		return excelColumn.value();
	}

	@SuppressWarnings("unchecked")
	public <T> Stream<T> read(ExcelReader reader, EntityStructure<? extends Property> structure,
			InputStream inputStream) {
		Map<String, Integer> nameToIndexMap = new HashMap<String, Integer>();
		ResponsiveIterator<String[]> iterator = new ResponsiveIterator<String[]>();
		executor.execute(() -> {
			try {
				reader.read(inputStream, (sheetIndex, rowIndex, contents) -> {
					if (nameToIndexMap.isEmpty() && contents != null && contents.length > 0) {
						for (int i = 0; i < contents.length; i++) {
							nameToIndexMap.put(contents[i], i);
						}
						return;
					}

					try {
						iterator.put(contents);
					} catch (InterruptedException e) {
						logger.error(e, "put sheetIndex={}, rowIndex={}, contents={} error", sheetIndex, rowIndex,
								contents);
					}
				});
			} catch (Exception e) {
				logger.error(e, "read excel error");
			} finally {
				try {
					iterator.close();
				} catch (InterruptedException e) {
					logger.error(e, "read thread error");
				}
			}
		});

		// 映射
		return XUtils.stream(iterator).map((contents) -> {
			T instance = (T) ReflectionApi.newInstance(structure.getEntityClass());
			structure.columns().forEach((property) -> {
				if (!property.getField().isSupportSetter()) {
					return;
				}

				Integer index = nameToIndexMap.get(property.getName());
				if (index == null) {
					return;
				}

				if (index >= contents.length) {
					return;
				}

				Object value = getConversionService().convert(contents[index], TypeDescriptor.valueOf(String.class),
						new TypeDescriptor(property.getField().getSetter()));
				property.getField().getSetter().set(instance, value);
			});
			return instance;
		});
	}
}
