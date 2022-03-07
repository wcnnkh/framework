package io.basc.framework.microsoft.annotation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.reflect.ReflectionApi;
import io.basc.framework.env.Sys;
import io.basc.framework.mapper.FieldDescriptor;
import io.basc.framework.microsoft.ExcelException;
import io.basc.framework.microsoft.ExcelExport;
import io.basc.framework.microsoft.ExcelReader;
import io.basc.framework.orm.EntityStructure;
import io.basc.framework.orm.Property;
import io.basc.framework.orm.support.DefaultObjectRelationalMapping;
import io.basc.framework.util.Assert;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.XUtils;

public class ExcelResolver extends DefaultObjectRelationalMapping {
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

	public final <T> Stream<T> read(ExcelReader reader, EntityStructure<? extends Property> structure, Object source)
			throws ExcelException {
		return read(reader, getConversionService(), structure, source);
	}

	public final <T> Stream<T> read(ExcelReader reader, Class<T> type, Object source) throws ExcelException {
		return read(reader, getStructure(type), source);
	}

	public final <T> void export(ExcelExport export, Class<T> type, Stream<? extends T> source) {
		export(export, getStructure(type), source, true);
	}

	public final <T> void export(ExcelExport export, EntityStructure<? extends Property> structure,
			Stream<? extends T> source, boolean appendTitles) {
		if (appendTitles) {
			appendTitles(export, structure);
		}
		export(export, getConversionService(), structure, source);
	}

	public final <T> void export(ExcelExport export, Stream<? extends T> source) {
		Assert.requiredArgument(source != null, "source");
		Iterator<? extends T> iterator = source.iterator();
		EntityStructure<? extends Property> structure = null;
		List<T> nullList = new ArrayList<T>();
		while (iterator.hasNext()) {
			T entity = iterator.next();
			if (entity == null) {
				nullList.add(entity);
				continue;
			}

			structure = getStructure(entity.getClass());
			appendTitles(export, structure);
			export(export, structure, nullList.stream(), false);
			export(export, structure, Arrays.asList(entity).stream(), false);
			break;
		}

		if (structure == null) {
			return;
		}

		export(export, structure, XUtils.stream(iterator), false);
	}

	/**
	 * 从输入源中read excel
	 * 
	 * @param reader
	 * @param structure
	 * @param source    InputStream or File or Resource
	 * @return
	 * @throws ExcelException
	 */
	@SuppressWarnings("unchecked")
	private static <T> Stream<T> read(ExcelReader reader, ConversionService conversionService,
			EntityStructure<? extends Property> structure, Object source) throws ExcelException {
		Assert.requiredArgument(reader != null, "reader");
		Assert.requiredArgument(conversionService != null, "conversionService");
		Assert.requiredArgument(structure != null, "structure");
		Assert.requiredArgument(source != null, "source");
		Map<String, Integer> nameToIndexMap = new HashMap<String, Integer>();
		Iterator<String[]> iterator;
		try {
			iterator = reader.read(source).iterator();
		} catch (IOException e) {
			throw new ExcelException(structure.getEntityClass().getName(), e);
		}

		while (iterator.hasNext()) {
			String[] contents = iterator.next();
			if (nameToIndexMap.isEmpty() && contents != null && contents.length > 0) {
				for (int i = 0; i < contents.length; i++) {
					nameToIndexMap.put(contents[i], i);
				}
				break;
			}
		}

		// 映射
		return XUtils.stream(iterator).map((contents) -> {
			T instance = (T) ReflectionApi.newInstance(structure.getEntityClass());
			structure.columns().forEach((property) -> {
				if (!property.getField().isSupportSetter()) {
					return;
				}

				Integer index = nameToIndexMap.get(property.getName());
				if (index == null || index >= contents.length) {
					for (String name : property.getAliasNames()) {
						index = nameToIndexMap.get(name);
						if (index != null && index < contents.length) {
							break;
						}
					}
				}

				if (index == null || index >= contents.length) {
					return;
				}

				property.getField().getSetter().set(instance, contents[index], conversionService);
			});
			return instance;
		});
	}

	private static void appendTitles(ExcelExport export, EntityStructure<? extends Property> structure) {
		try {
			export.append(structure.columns().map((e) -> e.getName()).collect(Collectors.toList()));
		} catch (IOException e) {
			throw new ExcelException(structure.getEntityClass().getName(), e);
		}
	}

	public static <T> Stream<LinkedHashMap<String, String>> map(ConversionService conversionService,
			EntityStructure<? extends Property> structure, Stream<? extends T> source) {
		Assert.requiredArgument(conversionService != null, "conversionService");
		Assert.requiredArgument(structure != null, "structure");
		Assert.requiredArgument(source != null, "source");
		return source.map((entity) -> {
			LinkedHashMap<String, String> map = new LinkedHashMap<>();
			structure.columns().forEach((property) -> {
				if (!property.getField().isSupportGetter()) {
					map.put(property.getName(), null);
					return;
				}

				Object value = property.getField().getGetter().get(entity);
				if (value == null) {
					map.put(property.getName(), null);
					return;
				}

				map.put(property.getName(), (String) conversionService.convert(value,
						new TypeDescriptor(property.getField().getGetter()), TypeDescriptor.valueOf(String.class)));
			});
			return map;
		});
	}

	private static <T> void export(ExcelExport export, ConversionService conversionService,
			EntityStructure<? extends Property> structure, Stream<? extends T> source) {
		Assert.requiredArgument(export != null, "export");
		map(conversionService, structure, source).forEach((e) -> {
			try {
				export.append(e.values());
			} catch (IOException ex) {
				throw new ExcelException(structure.getEntityClass().getName(), ex);
			}
		});
	}
}
