package io.basc.framework.orm.transfer;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.basc.framework.codec.support.CharsetCodec;
import io.basc.framework.codec.support.ListRecordCodec;
import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.reflect.ReflectionApi;
import io.basc.framework.env.Sys;
import io.basc.framework.io.FileRecords;
import io.basc.framework.mapper.Structure;
import io.basc.framework.orm.ObjectRelational;
import io.basc.framework.orm.ObjectRelationalFactory;
import io.basc.framework.orm.OrmException;
import io.basc.framework.orm.Property;
import io.basc.framework.util.ArrayUtils;
import io.basc.framework.util.Assert;
import io.basc.framework.util.ConsumeProcessor;
import io.basc.framework.util.ConvertibleIterator;
import io.basc.framework.util.Cursor;
import io.basc.framework.util.Pair;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.XUtils;
import io.basc.framework.value.Value;

public abstract class TableTransfer implements Importer, ExportProcessor<Object> {
	private ObjectRelationalFactory mapper;
	private ConversionService conversionService;
	private boolean header = true;

	public TableTransfer() {
		this.mapper = TransfRelationalMapping.INSTANCE;
		this.conversionService = Sys.getEnv().getConversionService();
	}

	protected TableTransfer(TableTransfer source) {
		Assert.requiredArgument(source != null, "source");
		this.mapper = source.mapper;
		this.conversionService = source.conversionService;
	}

	public ObjectRelationalFactory getMapper() {
		return mapper;
	}

	public void setOrm(ObjectRelationalFactory mapper) {
		Assert.requiredArgument(mapper != null, "mapper");
		this.mapper = mapper;
	}

	public ConversionService getConversionService() {
		return conversionService;
	}

	public void setConversionService(ConversionService conversionService) {
		Assert.requiredArgument(conversionService != null, "conversionService");
		this.conversionService = conversionService;
	}

	public boolean isHeader() {
		return header;
	}

	public void setHeader(boolean header) {
		this.header = header;
	}

	public abstract Cursor<String[]> read(Object source) throws IOException;

	@Override
	public final <T> Cursor<T> read(File source, TypeDescriptor targetType) throws IOException {
		return read((Object) source, targetType);
	}

	@SuppressWarnings("unchecked")
	public final <T> Cursor<T> read(Object source, TypeDescriptor targetType) throws IOException {
		Assert.requiredArgument(targetType != null, "type");
		Cursor<String[]> cursor = read(source);
		if (Value.isBaseType(targetType.getType())) {
			return cursor.map((values) -> ArrayUtils.isEmpty(values) ? null
					: (T) conversionService.convert(values[0], TypeDescriptor.valueOf(String.class), targetType));
		} else if (targetType.isArray() || targetType.isCollection()) {
			return cursor.map((values) -> {
				return (T) conversionService.convert(values, TypeDescriptor.forObject(values), targetType);
			});
		} else if (targetType.isMap()) {
			if (isHeader()) {
				String[] titles = null;
				while (cursor.hasNext()) {
					String[] contents = cursor.next();
					if (titles == null && contents != null && contents.length > 0) {
						titles = contents.clone();
						break;
					}
				}

				if (titles == null) {
					return Cursor.empty();
				}

				final String[] titleUes = titles.clone();
				// 映射
				return cursor.map((contents) -> {
					Map<String, String> columnMap = new LinkedHashMap<>();
					for (int i = 0; i < contents.length; i++) {
						columnMap.put(titleUes[i], contents[i]);
					}
					return (T) conversionService.convert(columnMap,
							TypeDescriptor.map(LinkedHashMap.class, String.class, String.class), targetType);
				});
			} else {
				return cursor.map((e) -> {
					Map<Integer, String> columnMap = new LinkedHashMap<Integer, String>();
					for (int i = 0; i < e.length; i++) {
						columnMap.put(i, e[i]);
					}
					return (T) conversionService.convert(columnMap,
							TypeDescriptor.map(LinkedHashMap.class, Integer.class, String.class), targetType);
				});
			}
		} else {
			return mapEntity(cursor, mapper.getStructure(targetType.getType()));
		}
	}

	public final <T> Cursor<T> read(Object source, Structure<? extends Property> structure) throws IOException {
		Assert.requiredArgument(structure != null, "structure");
		Assert.requiredArgument(source != null, "source");
		return mapEntity(read(source), structure);
	}

	@SuppressWarnings("unchecked")
	public final <T> Cursor<T> mapEntity(Cursor<String[]> source, Structure<? extends Property> structure) {
		Assert.requiredArgument(structure != null, "structure");
		Assert.requiredArgument(source != null, "source");
		if (isHeader()) {
			Map<String, Integer> nameToIndexMap = new HashMap<String, Integer>();
			while (source.hasNext()) {
				String[] contents = source.next();
				if (nameToIndexMap.isEmpty() && contents != null && contents.length > 0) {
					for (int i = 0; i < contents.length; i++) {
						nameToIndexMap.put(contents[i], i);
					}
					break;
				}
			}

			List<Property> properties = structure.stream().filter((e) -> e.isSupportSetter())
					.collect(Collectors.toList());
			// 映射
			return source.map((contents) -> {
				T instance = (T) ReflectionApi.newInstance(structure.getSourceClass());
				properties.forEach((property) -> {
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

					property.getSetter().set(instance, contents[index], conversionService);
				});
				return instance;
			});
		} else {
			return source.map((e) -> {
				T instance = (T) ReflectionApi.newInstance(structure.getSourceClass());
				int i = 0;
				Iterator<? extends Property> iterator = structure.stream().filter((p) -> p.isSupportSetter())
						.iterator();
				while (iterator.hasNext() && i < e.length) {
					Property property = iterator.next();
					property.getSetter().set(instance, e[i++], conversionService);
				}
				return instance;
			});
		}
	}

	protected String lookupColumnName(ResultSetMetaData resultSetMetaData, int columnIndex) throws SQLException {
		String name = resultSetMetaData.getColumnLabel(columnIndex);
		if (StringUtils.isEmpty(name)) {
			name = resultSetMetaData.getColumnName(columnIndex);
		}
		return name;
	}

	public TransfColumns<String, String> mapColumns(Object source) throws OrmException {
		// TODO 是否考虑移到单独的processor, 因为并不是所有的jvm都包含 java.sql.* 环境
		if (source instanceof ResultSet) {
			ResultSet rs = (ResultSet) source;
			try {
				ResultSetMetaData rsmd = rs.getMetaData();
				int count = rsmd.getColumnCount();
				if (count == 0) {
					return null;
				}

				TransfColumns<String, String> columns = new TransfColumns<String, String>(count);
				for (int i = 1; i <= count; i++) {
					String key = lookupColumnName(rsmd, i);
					Object value = rs.getObject(i);
					columns.add(key, (String) getConversionService().convert(value, TypeDescriptor.forObject(value),
							TypeDescriptor.valueOf(String.class)));
				}
				return columns;
			} catch (SQLException e) {
				throw new OrmException(e);
			}
		}

		TypeDescriptor type = TypeDescriptor.forObject(source);
		if (Value.isBaseType(type.getType())) {
			return new TransfColumns<String, String>(new String[] {
					(String) conversionService.convert(source, type, TypeDescriptor.valueOf(String.class)) });
		} else if (type.isCollection() || type.isArray()) {
			String[] values = (String[]) getConversionService().convert(source, type,
					TypeDescriptor.valueOf(String[].class));
			return new TransfColumns<String, String>(values);
		} else {
			// ORM
			ObjectRelational<? extends Property> structure = mapper.getStructure(type.getType());
			return mapColumns(source, structure);
		}
	}

	public final TransfColumns<String, String> mapColumns(Object source, Structure<? extends Property> structure) {
		return structure.stream().filter((e) -> e.isSupportGetter()).map((property) -> {
			Object value = property.get(source);
			if (value == null) {
				return new Pair<String, String>(property.getName(), null);
			}

			return new Pair<String, String>(property.getName(), (String) getConversionService().convert(value,
					new TypeDescriptor(property.getGetter()), TypeDescriptor.valueOf(String.class)));
		}).collect(Collectors.toCollection(TransfColumns::new));
	}

	public final Iterator<TransfColumns<String, String>> exportAll(Iterator<?> source) {
		return XUtils
				.stream(new ConvertibleIterator<Object, TransfColumns<String, String>>(source, (e) -> mapColumns(e)))
				.filter((e) -> e != null).iterator();
	}

	public final java.util.List<TransfColumns<String, String>> exportAll(Collection<?> source) {
		return XUtils.stream(exportAll(source.iterator())).collect(Collectors.toList());
	}

	public final <E extends Throwable> void exportAll(Iterator<?> source,
			ConsumeProcessor<? super List<String>, ? extends E> consumer) throws E, IOException {
		Iterator<TransfColumns<String, String>> iterator = exportAll(source);
		// 是否已写入header
		boolean writeHeader = false;
		FileRecords<List<String>> tempRecords = new FileRecords<List<String>>(
				new ListRecordCodec<String>(CharsetCodec.UTF_8));
		try {
			while (iterator.hasNext()) {
				TransfColumns<String, String> columns = iterator.next();
				if (columns == null) {
					continue;
				}

				if (!writeHeader && isHeader()) {
					if (columns.hasKeys()) {
						consumer.process(columns.getKeys());
						// 将临时存储的记录推出去
						try {
							tempRecords.consume(consumer);
						} finally {
							tempRecords.delete();
						}
						consumer.process(columns.getValues());
						writeHeader = true;
					} else {
						// 如果在第一次插入的时候不存在, 先临时缓存起来
						tempRecords.append(columns.getValues());
					}
					continue;
				}
				consumer.process(columns.getValues());
			}

			// 如果到结束时还存在数据，那么就推出去
			tempRecords.consume(consumer);
		} finally {
			tempRecords.delete();
		}

	}
}
