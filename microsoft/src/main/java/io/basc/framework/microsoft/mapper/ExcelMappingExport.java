package io.basc.framework.microsoft.mapper;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.microsoft.ExcelException;
import io.basc.framework.microsoft.ExcelExport;
import io.basc.framework.orm.EntityStructure;
import io.basc.framework.util.ArrayUtils;
import io.basc.framework.util.Assert;
import io.basc.framework.util.stream.RunnableProcessor;

public class ExcelMappingExport extends ExcelExportProcessor<Object> implements AutoCloseable {
	private final ExcelExport export;
	private AtomicBoolean closed;
	private Map<String, Integer> columnIndexMap;

	public ExcelMappingExport(ExcelExport export) {
		Assert.requiredArgument(export != null, "export");
		this.export = export;
		this.columnIndexMap = new LinkedHashMap<String, Integer>(8);
		this.closed = new AtomicBoolean(false);
	}

	protected ExcelMappingExport(ExcelMappingExport source) {
		super(source);
		this.export = source.export;
		this.columnIndexMap = source.columnIndexMap;
		this.closed = source.closed;
	}

	public final ExcelExport getExport() {
		return export;
	}

	@Override
	public void close() throws IOException {
		if (closed.compareAndSet(false, true)) {
			export.close();
		}
	}

	public <E extends IOException> ExcelMappingExport process(RunnableProcessor<E> process)
			throws IOException, ExcelException {
		try {
			process.process();
			return this;
		} catch (RuntimeException | IOException e) {
			close();
			throw e;
		} catch (Throwable e) {
			close();
			throw new ExcelException(e);
		}
	}

	public final ExcelMappingExport titles(String... titles) throws ExcelException, IOException {
		if (ArrayUtils.isEmpty(titles)) {
			return this;
		}

		return titles(Arrays.asList(titles));
	}

	public ExcelMappingExport titles(List<String> titles) throws ExcelException, IOException {
		if (!export.isEmpty()) {
			return this;
		}

		return process(() -> {
			if (columnIndexMap.isEmpty()) {
				for (int i = 0, size = titles.size(); i < size; i++) {
					columnIndexMap.put(titles.get(i), i);
				}
			}
			titles(export, titles);
		});
	}

	public final ExcelMappingExport titles(EntityStructure<?> structure) throws ExcelException, IOException {
		if (structure == null) {
			return this;
		}

		return process(() -> titles(structure.columns().map((e) -> e.getName()).collect(Collectors.toList())));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ExcelMappingExport put(Object row) throws ExcelException, IOException {
		if (row == null) {
			return this;
		}

		return process(() -> {
			TypeDescriptor type = TypeDescriptor.forObject(row);
			if (type.isMap()) {
				Map map = (Map) row;
				if (columnIndexMap.isEmpty()) {
					// 如果不存在映射关系那么就直接插入
					Collection<Object> columns = map.values();
					String[] values = (String[]) getConversionService().convert(row, TypeDescriptor.forObject(columns),
							TypeDescriptor.valueOf(String[].class));
					export.put(values);
				} else {
					Map<String, String> valueMap = (Map<String, String>) getConversionService().convert(row, type,
							TypeDescriptor.map(LinkedHashMap.class, String.class, String.class));
					// 如果存在映射关系，那么就进行整理
					String[] values = new String[0];
					for (Entry<String, String> entry : valueMap.entrySet()) {
						Integer index = columnIndexMap.get(entry.getKey());
						if (index == null || index < 0) {
							continue;
						}

						if (index > values.length) {
							values = Arrays.copyOf(values, index + 1);
						}

						values[index] = entry.getValue();
					}
					export.put(values);
				}
			} else {
				put(row, export);
			}
		});
	}

	public ExcelMappingExport put(Object row, EntityStructure<?> structure) throws ExcelException, IOException {
		if (structure == null || row == null) {
			return this;
		}

		return process(() -> {
			titles(structure);
			List<String> values = structure.columns().map((property) -> {
				if (!property.getField().isSupportGetter()) {
					return null;
				}

				Object value = property.getField().getGetter().get(row);
				if (value == null) {
					return null;
				}

				return (String) getConversionService().convert(value,
						new TypeDescriptor(property.getField().getGetter()), TypeDescriptor.valueOf(String.class));
			}).collect(Collectors.toList());
			export.put(values);
		});
	}

	public final ExcelMappingExport putAll(Iterator<?> rows) throws ExcelException, IOException {
		return process(() -> {
			while (rows.hasNext()) {
				put(rows.next());
			}
		});
	}

	public final ExcelMappingExport putAll(Stream<?> rows) throws ExcelException, IOException {
		return process(() -> {
			rows.forEach((e) -> {
				try {
					put(e);
				} catch (IOException ex) {
					throw new ExcelException(ex);
				}
			});
		});
	}

	public final ExcelMappingExport putAll(Iterable<?> rows) throws ExcelException, IOException {
		return process(() -> putAll(rows.iterator()));
	}

	public final ExcelMappingExport putAll(Iterator<?> rows, EntityStructure<?> structure)
			throws ExcelException, IOException {
		if (structure == null || rows == null) {
			return this;
		}

		return process(() -> {
			while (rows.hasNext()) {
				put(rows.next(), structure);
			}
		});
	}

	public final ExcelMappingExport putAll(Stream<?> rows, EntityStructure<?> structure)
			throws ExcelException, IOException {
		if (structure == null || rows == null) {
			return this;
		}

		return process(() -> {
			rows.forEach((e) -> {
				try {
					put(e, structure);
				} catch (IOException ex) {
					throw new ExcelException(ex);
				}
			});
		});
	}

	public final ExcelMappingExport putAll(Iterable<?> rows, EntityStructure<?> structure)
			throws ExcelException, IOException {
		if (structure == null || rows == null) {
			return this;
		}

		return process(() -> putAll(rows.iterator(), structure));
	}

	public final <T> ExcelMappingExport putAll(Iterator<? extends T> rows, Class<T> type)
			throws ExcelException, IOException {
		if (type == null || rows == null) {
			return this;
		}

		return process(() -> putAll(rows, getOrm().getStructure(type)));
	}

	public final <T> ExcelMappingExport putAll(Stream<? extends T> rows, Class<T> type)
			throws ExcelException, IOException {
		if (type == null || rows == null) {
			return this;
		}

		return process(() -> {
			EntityStructure<?> structure = getOrm().getStructure(type);
			rows.forEach((e) -> {
				try {
					put(e, structure);
				} catch (IOException ex) {
					throw new ExcelException(ex);
				}
			});
		});
	}

	public final <T> ExcelMappingExport putAll(Iterable<? extends T> rows, Class<T> type)
			throws ExcelException, IOException {
		if (type == null || rows == null) {
			return this;
		}

		return process(() -> putAll(rows, getOrm().getStructure(type)));
	}
}
