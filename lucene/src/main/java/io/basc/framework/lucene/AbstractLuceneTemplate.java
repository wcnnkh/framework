package io.basc.framework.lucene;

import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.ConversionServiceAware;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.parameter.ParameterDescriptor;
import io.basc.framework.env.Sys;
import io.basc.framework.json.JSONUtils;
import io.basc.framework.lang.Nullable;
import io.basc.framework.lucene.annotation.AnnotationFieldResolver;
import io.basc.framework.mapper.Fields;
import io.basc.framework.mapper.Mapper;
import io.basc.framework.mapper.SimpleMapper;
import io.basc.framework.orm.EntityStructure;
import io.basc.framework.orm.Property;
import io.basc.framework.orm.repository.Condition;
import io.basc.framework.orm.repository.ConditionKeywords;
import io.basc.framework.orm.repository.Conditions;
import io.basc.framework.orm.repository.OrderColumn;
import io.basc.framework.orm.repository.RelationshipKeywords;
import io.basc.framework.orm.repository.RepositoryColumn;
import io.basc.framework.orm.repository.WithCondition;
import io.basc.framework.util.Assert;
import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.NumberUtils;
import io.basc.framework.util.concurrent.AsyncExecutor;
import io.basc.framework.util.concurrent.TaskQueue;
import io.basc.framework.util.stream.Processor;
import io.basc.framework.value.AnyValue;
import io.basc.framework.value.StringValue;
import io.basc.framework.value.Value;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.index.DocValuesType;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.SortField.Type;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.util.BytesRef;

public abstract class AbstractLuceneTemplate implements LuceneTemplate,
		ConversionServiceAware {

	// 默认的写操作队列, 所有的写都排队处理
	protected static final TaskQueue TASK_QUEUE = new TaskQueue();

	static {
		// 启动写操作队列
		TASK_QUEUE.setName(AbstractLuceneTemplate.class.getName());
		TASK_QUEUE.start();
	}

	private final Mapper<Document, LuceneException> mapper = new SimpleMapper<Document, LuceneException>();
	private ConversionService conversionService;
	private FieldResolver fieldResolver = new AnnotationFieldResolver();
	private final AsyncExecutor writeExecutor;// 写执行器
	private final Executor searchExecutor;// 搜索执行器

	public AbstractLuceneTemplate() {
		this(Executors.newWorkStealingPool());
	}

	public AbstractLuceneTemplate(Executor searchExecutor) {
		this(TASK_QUEUE, searchExecutor);
	}

	public AbstractLuceneTemplate(AsyncExecutor writeExecutor,
			@Nullable Executor searchExecutor) {
		this.writeExecutor = writeExecutor;
		this.searchExecutor = searchExecutor;
	}

	@Override
	public Mapper<Document, LuceneException> getMapper() {
		return this.mapper;
	}

	public ConversionService getConversionService() {
		return conversionService == null ? Sys.env.getConversionService()
				: conversionService;
	}

	public void setConversionService(ConversionService conversionService) {
		this.conversionService = conversionService;
	}

	public final FieldResolver getFieldResolver() {
		return fieldResolver;
	}

	public void setFieldResolver(FieldResolver fieldResolver) {
		Assert.requiredArgument(fieldResolver != null, "fieldResolver");
		this.fieldResolver = fieldResolver;
	}

	protected abstract IndexWriter getIndexWriter() throws IOException;

	@Override
	public <T, E extends Exception> Future<T> write(
			Processor<IndexWriter, T, E> processor) throws LuceneWriteException {
		return writeExecutor.submit(() -> {
			IndexWriter indexWriter = null;
			try {
				indexWriter = getIndexWriter();
				T value = processor.process(indexWriter);
				indexWriter.commit();
				return value;
			} catch (Exception e) {
				if (indexWriter != null) {
					indexWriter.rollback();
				}
				throw e;
			} finally {
				if (indexWriter != null) {
					indexWriter.close();
				}
			}
		});
	}

	@Nullable
	protected abstract IndexReader getIndexReader() throws IOException;

	protected void closeIndexReader(IndexReader indexReader) throws IOException {
		if (indexReader == null) {
			return;
		}

		indexReader.close();
	}

	@Override
	public <T, E extends Exception> T read(
			Processor<IndexReader, T, E> processor) throws LuceneReadException {

		IndexReader indexReader = null;
		try {
			indexReader = getIndexReader();
			return processor.process(indexReader);
		} catch (Exception e) {
			throw new LuceneReadException(e);
		} finally {
			try {
				closeIndexReader(indexReader);
			} catch (IOException e) {
				throw new LuceneReadException(e);
			}
		}
	}

	@Override
	public <T, E extends Exception> T search(
			Processor<IndexSearcher, T, ? extends E> processor)
			throws LuceneSearchException {
		if (searchExecutor == null) {
			return LuceneTemplate.super.search(processor);
		}
		try {
			return read((reader) -> {
				IndexSearcher indexSearcher = new IndexSearcher(reader,
						searchExecutor);
				return processor.process(indexSearcher);
			});
		} catch (LuceneException e) {
			throw e;
		} catch (Throwable e) {
			throw new LuceneSearchException(e);
		}
	}

	public void wrap(Document document, ParameterDescriptor descriptor,
			Object value) {
		document.removeField(descriptor.getName());
		Value v;
		if (Value.isBaseType(descriptor.getType())) {
			v = new AnyValue(value, getConversionService());
		} else {
			v = new StringValue(JSONUtils.getJsonSupport().toJSONString(value));
		}

		fieldResolver.resolve(descriptor, v).forEach((f) -> document.add(f));
	}

	@Override
	public Document wrap(Document document, Object instance, Fields fields) {
		for (io.basc.framework.mapper.Field field : fields) {
			Object value = field.getGetter().get(instance);
			if (value == null) {
				continue;
			}

			wrap(document, field.getGetter(), value);
		}
		return document;
	}

	@Override
	public Document wrap(Document document,
			EntityStructure<? extends Property> structure, Object instance) {
		for (Property property : structure) {
			Object value = property.getField().get(instance);
			if (value == null) {
				continue;
			}

			wrap(document, property.getField().getGetter(), value);
		}
		return document;
	}

	private void appendSort(EntityStructure<? extends Property> structure,
			List<SortField> sortFields, List<? extends OrderColumn> orders) {
		if (CollectionUtils.isEmpty(orders)) {
			return;
		}

		for (OrderColumn column : orders) {
			Property property = structure.getByName(column.getName());
			if (NumberUtils.isNumber(property.getField().getGetter().getType())) {
				if (NumberUtils.isInteger(property.getField().getGetter()
						.getType())) {
					sortFields
							.add(new SortField(
									column.getName(),
									Type.LONG,
									column.getSort() == io.basc.framework.util.comparator.Sort.ASC));
				} else {
					sortFields
							.add(new SortField(
									column.getName(),
									Type.DOUBLE,
									column.getSort() == io.basc.framework.util.comparator.Sort.ASC));
				}
			} else {
				sortFields
						.add(new SortField(
								column.getName(),
								Type.STRING,
								column.getSort() == io.basc.framework.util.comparator.Sort.ASC));
			}
			appendSort(structure, sortFields, column.getWithOrders());
		}
	}

	public Sort parseSort(EntityStructure<? extends Property> structure,
			List<? extends OrderColumn> orders) {
		if (CollectionUtils.isEmpty(orders)) {
			return null;
		}

		List<SortField> sortFields = new ArrayList<SortField>();
		appendSort(structure, sortFields, orders);
		return new Sort(sortFields.toArray(new SortField[0]));
	}

	public Query parseQuery(Document document) {
		BooleanQuery.Builder builder = new BooleanQuery.Builder();
		for (IndexableField field : document) {
			Term term;
			if (field.fieldType().docValuesType() == DocValuesType.BINARY) {
				term = new Term(field.name(), field.binaryValue());
			} else {
				term = new Term(field.name(), field.stringValue());
			}

			TermQuery query = new TermQuery(term);
			builder.add(query, Occur.MUST);
		}
		return builder.build();
	}

	/**
	 * 1．MUST和MUST：取得连个查询子句的交集。
	 * 2．MUST和MUST_NOT：表示查询结果中不能包含MUST_NOT所对应得查询子句的检索结果。
	 * 3．SHOULD与MUST_NOT：连用时，功能同MUST和MUST_NOT。
	 * 4．SHOULD与MUST连用时，结果为MUST子句的检索结果,但是SHOULD可影响排序。
	 * 5．SHOULD与SHOULD：表示“或”关系，最终检索结果为所有检索子句的并集。 6．MUST_NOT和MUST_NOT：无意义，检索无结果。
	 * 
	 * @param relationshipKeywords
	 * @param condition
	 * @return
	 */
	private Occur parseOccur(RelationshipKeywords relationshipKeywords,
			String condition) {
		if (relationshipKeywords.getAndKeywords().exists(condition)) {
			return Occur.MUST;
		} else if (relationshipKeywords.getOrKeywords().exists(condition)) {
			return Occur.SHOULD;
		}
		return null;
	}

	private Query parseQuery(Condition condition,
			ConditionKeywords conditionKeywords) {
		RepositoryColumn column = condition.getColumn();
		if (column.getValue() == null) {
			return null;
		}

		if (conditionKeywords.getEqualKeywords().exists(
				condition.getCondition())) {
			// =
			Term term;
			if (column.getType() == byte[].class) {
				term = new Term(column.getName(), new BytesRef(
						(byte[]) column.getValue()));
			} else {
				term = new Term(column.getName(),
						(String) getConversionService().convert(
								column.getValue(),
								column.getValueTypeDescriptor(),
								TypeDescriptor.valueOf(String.class)));
			}
			return new TermQuery(term);
		} else if (conditionKeywords.getInKeywords().exists(
				condition.getCondition())) {
			BooleanQuery.Builder builder = new BooleanQuery.Builder();
			List<?> list;
			TypeDescriptor elementTypeDescriptor;
			if (column.getValueTypeDescriptor().isArray()
					|| column.getValueTypeDescriptor().isCollection()) {
				elementTypeDescriptor = column.getValueTypeDescriptor()
						.getElementTypeDescriptor();
				list = (List<?>) getConversionService().convert(
						column.getValue(),
						column.getValueTypeDescriptor(),
						TypeDescriptor.collection(List.class,
								elementTypeDescriptor));
			} else {
				list = Arrays.asList(column.getValue());
				elementTypeDescriptor = column.getValueTypeDescriptor();
			}

			for (Object value : list) {
				Term term;
				if (elementTypeDescriptor.getType() == byte[].class) {
					term = new Term(column.getName(), new BytesRef(
							(byte[]) value));
				} else {
					term = new Term(column.getName(),
							(String) getConversionService().convert(value,
									elementTypeDescriptor,
									TypeDescriptor.valueOf(String.class)));
				}
				// 或
				builder.add(new TermQuery(term), Occur.SHOULD);
			}
			return builder.build();
		} else {
			if (NumberUtils.isInteger(column.getType())) {
				long min = 0;
				long max = 0;
				Long value = (Long) getConversionService().convert(
						column.getValue(), column.getValueTypeDescriptor(),
						TypeDescriptor.valueOf(Long.class));
				if (conditionKeywords.getEqualOrGreaterThanKeywords().exists(
						condition.getCondition())) {
					max = Long.MAX_VALUE;
					min = value;
				} else if (conditionKeywords.getGreaterThanKeywords().exists(
						condition.getCondition())) {
					max = Long.MAX_VALUE;
					min = value + 1;
				} else if (conditionKeywords.getEqualOrLessThanKeywords()
						.exists(condition.getCondition())) {
					min = Long.MIN_VALUE;
					max = value;
				} else if (conditionKeywords.getLessThanKeywords().equals(
						condition.getCondition())) {
					min = Long.MIN_VALUE;
					max = value - 1;
				} else if (conditionKeywords.getNotEqualKeywords().equals(
						condition.getCondition())) {
					max = value + 1;
					min = value - 1;
				}
				return LongPoint.newRangeQuery(column.getName(), min, max);
			} else {
				String value = (String) getConversionService().convert(
						column.getValue(), column.getValueTypeDescriptor(),
						TypeDescriptor.valueOf(String.class));
				String max = null;
				String min = null;
				boolean includeLower = false;
				boolean includeUpper = false;
				if (conditionKeywords.getEqualOrGreaterThanKeywords().exists(
						condition.getCondition())) {
					min = value;
					includeLower = true;
				} else if (conditionKeywords.getGreaterThanKeywords().exists(
						condition.getCondition())) {
					min = value;
				} else if (conditionKeywords.getEqualOrLessThanKeywords()
						.exists(condition.getCondition())) {
					max = value;
					includeUpper = true;
				} else if (conditionKeywords.getLessThanKeywords().equals(
						condition.getCondition())) {
					max = value;
				} else if (conditionKeywords.getNotEqualKeywords().equals(
						condition.getCondition())) {
					max = value;
					min = value;
				}
				return new TermRangeQuery(column.getName(), min == null ? null
						: new BytesRef(min), max == null ? null : new BytesRef(
						max), includeLower, includeUpper);
			}
		}
	}

	private Query parseQuery(Conditions conditions,
			RelationshipKeywords relationshipKeywords,
			ConditionKeywords conditionKeywords) {
		Query firstQuery = parseQuery(conditions.getCondition(),
				conditionKeywords);
		List<WithCondition> withConditions = conditions.getWiths();
		if (CollectionUtils.isEmpty(withConditions)) {
			return firstQuery;
		}

		BooleanQuery.Builder builder = new BooleanQuery.Builder();
		builder.add(firstQuery, Occur.MUST);
		for (WithCondition condition : withConditions) {
			Occur occur = parseOccur(relationshipKeywords, condition.getWith());
			Query query = parseQuery(condition.getCondition(),
					relationshipKeywords, conditionKeywords);
			builder.add(query, occur);
		}
		return builder.build();
	}

	@Override
	public Query parseQuery(Conditions conditions) {
		return parseQuery(conditions, getMapping().getRelationshipKeywords(),
				getMapping().getConditionKeywords());
	}
}
