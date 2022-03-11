package io.basc.framework.microsoft.mapper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;

import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.reflect.ReflectionApi;
import io.basc.framework.env.Sys;
import io.basc.framework.io.Resource;
import io.basc.framework.lang.NotSupportedException;
import io.basc.framework.microsoft.ExcelException;
import io.basc.framework.microsoft.ExcelReader;
import io.basc.framework.orm.EntityStructure;
import io.basc.framework.orm.ObjectRelationalMapping;
import io.basc.framework.util.ArrayUtils;
import io.basc.framework.util.Assert;
import io.basc.framework.util.stream.Cursor;
import io.basc.framework.util.stream.StreamProcessorSupport;

public class ExcelMappingReader {
	private final ExcelReader reader;
	private final ObjectRelationalMapping orm;
	private final ConversionService conversionService;
	private long start = 0;
	private long limit = -1;

	public ExcelMappingReader(ExcelReader reader) {
		this(Sys.env.getConversionService(), reader);
	}

	public ExcelMappingReader(ObjectRelationalMapping orm, ExcelReader reader) {
		this(orm, Sys.env.getConversionService(), reader);
	}

	public ExcelMappingReader(ConversionService conversionService, ExcelReader reader) {
		this(ExcelMapping.INSTANCE, conversionService, reader);
	}

	public ExcelMappingReader(ObjectRelationalMapping orm, ConversionService conversionService, ExcelReader reader) {
		Assert.requiredArgument(orm != null, "orm");
		Assert.requiredArgument(conversionService != null, "conversionService");
		Assert.requiredArgument(reader != null, "export");
		this.orm = orm;
		this.conversionService = conversionService;
		this.reader = reader;
	}

	protected ExcelMappingReader(ExcelMappingReader mapper) {
		this.orm = mapper.orm;
		this.reader = mapper.reader;
		this.conversionService = mapper.conversionService;
		this.start = mapper.start;
		this.limit = mapper.limit;
	}

	public long getStart() {
		return start;
	}

	public ExcelMappingReader setStart(long start) {
		ExcelMappingReader mapper = new ExcelMappingReader(this);
		mapper.start = start;
		return mapper;
	}

	public long getLimit() {
		return limit;
	}

	public ExcelMappingReader setLimit(long limit) {
		ExcelMappingReader mapper = new ExcelMappingReader(this);
		mapper.limit = limit;
		return mapper;
	}

	public Cursor<String[]> read(Object source) throws ExcelException, IOException {
		Assert.requiredArgument(source != null, "source");
		Assert.requiredArgument(source != null, "source");
		Stream<String[]> stream;
		if (source instanceof InputStream) {
			stream = reader.read((InputStream) source);
		} else if (source instanceof File) {
			stream = reader.read((File) source);
		} else if (source instanceof Resource) {
			stream = ((Resource) source).read((input) -> reader.read(input));
		} else {
			throw new NotSupportedException(source.getClass().getName());
		}
		return StreamProcessorSupport.cursor(stream).limit(start, limit);
	}

	public final <T> Cursor<T> read(Object source, Class<? extends T> type) throws ExcelException, IOException {
		return read(source, TypeDescriptor.valueOf(type));
	}

	@SuppressWarnings("unchecked")
	public final <T> Cursor<T> read(Object source, TypeDescriptor type) throws ExcelException, IOException {
		Assert.requiredArgument(type != null, "type");
		Cursor<String[]> cursor = read(source);
		if (type.getType() == String.class) {
			return (Cursor<T>) cursor.filter((e) -> cursor.getPosition() != 0)
					.map((values) -> ArrayUtils.isEmpty(values) ? null : values[0]);
		} else if (type.isArray() || type.isCollection()) {
			return cursor.filter((e) -> cursor.getPosition() != 0).map((values) -> {
				return (T) conversionService.convert(values, TypeDescriptor.forObject(values), type);
			});
		} else if (type.isMap()) {
			String[] titles = null;
			Iterator<String[]> iterator = cursor.iterator();
			while (iterator.hasNext()) {
				String[] contents = iterator.next();
				if (titles == null && contents != null && contents.length > 0) {
					titles = contents.clone();
					break;
				}
			}

			if (titles == null) {
				return StreamProcessorSupport.emptyCursor();
			}

			final String[] titleUes = titles.clone();
			// 映射
			return StreamProcessorSupport.cursor(iterator).onClose(() -> cursor.close()).map((contents) -> {
				Map<String, String> columnMap = new LinkedHashMap<>();
				for (int i = 0; i < contents.length; i++) {
					columnMap.put(titleUes[i], contents[i]);
				}
				return (T) conversionService.convert(columnMap,
						TypeDescriptor.map(LinkedHashMap.class, String.class, String.class), type);
			});
		} else {
			return map(cursor, orm.getStructure(type.getType()));
		}
	}

	@SuppressWarnings("unchecked")
	public <T> Cursor<T> map(Stream<String[]> source, EntityStructure<?> structure) {
		Assert.requiredArgument(structure != null, "structure");
		Assert.requiredArgument(source != null, "source");
		Map<String, Integer> nameToIndexMap = new HashMap<String, Integer>();
		Iterator<String[]> iterator = source.iterator();
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
		return StreamProcessorSupport.cursor(iterator).onClose(() -> source.close()).map((contents) -> {
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

	/**
	 * 从输入源中read excel
	 * 
	 * @param reader
	 * @param structure
	 * @param source    InputStream or File or Resource
	 * @return
	 * @throws ExcelException
	 */
	public final <T> Cursor<T> read(Object source, EntityStructure<?> structure) throws ExcelException, IOException {
		Assert.requiredArgument(structure != null, "structure");
		Assert.requiredArgument(source != null, "source");
		return map(read(source), structure);
	}

	public static ExcelMappingReader wrap(ExcelReader reader) {
		return new ExcelMappingReader(reader);
	}
}
