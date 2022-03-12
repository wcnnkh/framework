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
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.ConvertibleIterator;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.reflect.ReflectionApi;
import io.basc.framework.env.Sys;
import io.basc.framework.orm.EntityStructure;
import io.basc.framework.orm.ObjectRelationalMapping;
import io.basc.framework.orm.OrmException;
import io.basc.framework.util.ArrayUtils;
import io.basc.framework.util.Assert;
import io.basc.framework.util.Pair;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.XUtils;
import io.basc.framework.util.stream.Cursor;
import io.basc.framework.util.stream.StreamProcessorSupport;

public abstract class TableTransfer implements Importer, ExportProcessor<Object> {
	private ObjectRelationalMapping orm;
	private ConversionService conversionService;

	public TableTransfer() {
		this.orm = TransfRelationalMapping.INSTANCE;
		this.conversionService = Sys.env.getConversionService();
	}

	protected TableTransfer(TableTransfer source) {
		Assert.requiredArgument(source != null, "source");
		this.orm = source.orm;
		this.conversionService = source.conversionService;
	}

	public ObjectRelationalMapping getOrm() {
		return orm;
	}

	public void setOrm(ObjectRelationalMapping orm) {
		Assert.requiredArgument(orm != null, "orm");
		this.orm = orm;
	}

	public ConversionService getConversionService() {
		return conversionService;
	}

	public void setConversionService(ConversionService conversionService) {
		Assert.requiredArgument(conversionService != null, "conversionService");
		this.conversionService = conversionService;
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
		if (targetType.getType() == String.class) {
			return (Cursor<T>) cursor.filter((e) -> cursor.getPosition() != 0)
					.map((values) -> ArrayUtils.isEmpty(values) ? null : values[0]);
		} else if (targetType.isArray() || targetType.isCollection()) {
			return cursor.filter((e) -> cursor.getPosition() != 0).map((values) -> {
				return (T) conversionService.convert(values, TypeDescriptor.forObject(values), targetType);
			});
		} else if (targetType.isMap()) {
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
						TypeDescriptor.map(LinkedHashMap.class, String.class, String.class), targetType);
			});
		} else {
			return mapToEntity(cursor, orm.getStructure(targetType.getType()));
		}
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
	public final <T> Cursor<T> read(Object source, EntityStructure<?> structure) throws IOException {
		Assert.requiredArgument(structure != null, "structure");
		Assert.requiredArgument(source != null, "source");
		return mapToEntity(read(source), structure);
	}

	@SuppressWarnings("unchecked")
	public final <T> Cursor<T> mapToEntity(Stream<String[]> source, EntityStructure<?> structure) {
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

	protected String lookupColumnName(ResultSetMetaData resultSetMetaData, int columnIndex) throws SQLException {
		String name = resultSetMetaData.getColumnLabel(columnIndex);
		if (StringUtils.isEmpty(name)) {
			name = resultSetMetaData.getColumnName(columnIndex);
		}
		return name;
	}

	public TransfColumns<String, String> mapToColumns(Object source) throws OrmException {
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

		if (source instanceof String) {
			return new TransfColumns<String, String>(new String[] { (String) source });
		} else if (source instanceof String[]) {
			return new TransfColumns<String, String>(new String[] { (String) source });
		} else {
			TypeDescriptor type = TypeDescriptor.forObject(source);
			if (type.isCollection() || type.isArray()) {
				String[] values = (String[]) getConversionService().convert(source, type,
						TypeDescriptor.valueOf(String[].class));
				return new TransfColumns<String, String>(values);
			} else {
				// ORM
				EntityStructure<?> structure = getOrm().getStructure(source.getClass());
				return mapToColumns(source, structure);
			}
		}
	}

	public final TransfColumns<String, String> mapToColumns(Object source, EntityStructure<?> structure) {
		return structure.columns().map((property) -> {
			if (!property.getField().isSupportGetter()) {
				return null;
			}

			Object value = property.getField().getGetter().get(source);
			if (value == null) {
				return null;
			}

			return new Pair<String, String>(property.getName(), (String) getConversionService().convert(value,
					new TypeDescriptor(property.getField().getGetter()), TypeDescriptor.valueOf(String.class)));
		}).collect(Collectors.toCollection(TransfColumns::new));
	}

	public final Iterator<TransfColumns<String, String>> exportAll(Iterator<?> source) {
		return XUtils.stream(
				new ConvertibleIterator<Object, TransfColumns<String, String>>(source, (e) -> mapToColumns(source)))
				.filter((e) -> e != null).iterator();
	}

	public final java.util.List<TransfColumns<String, String>> exportAll(Collection<?> source) {
		return XUtils.stream(exportAll(source.iterator())).collect(Collectors.toList());
	}
}
