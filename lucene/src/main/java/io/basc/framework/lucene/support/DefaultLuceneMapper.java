package io.basc.framework.lucene.support;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.index.DocValuesType;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.SortField.Type;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.util.BytesRef;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.parameter.ParameterDescriptor;
import io.basc.framework.factory.ConfigurableServices;
import io.basc.framework.factory.ServiceLoaderFactory;
import io.basc.framework.lucene.LuceneException;
import io.basc.framework.lucene.LuceneMapper;
import io.basc.framework.orm.EntityStructure;
import io.basc.framework.orm.Property;
import io.basc.framework.orm.StructureRegistry;
import io.basc.framework.orm.repository.Condition;
import io.basc.framework.orm.repository.ConditionKeywords;
import io.basc.framework.orm.repository.Conditions;
import io.basc.framework.orm.repository.OrderColumn;
import io.basc.framework.orm.repository.RelationshipKeywords;
import io.basc.framework.orm.repository.RepositoryColumn;
import io.basc.framework.orm.repository.WithCondition;
import io.basc.framework.orm.support.AbstractObjectMapper;
import io.basc.framework.orm.support.SimpleStructureRegistry;
import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.NumberUtils;
import io.basc.framework.value.Value;

public class DefaultLuceneMapper extends AbstractObjectMapper<Document, LuceneException> implements LuceneMapper {
	private final StructureRegistry<EntityStructure<? extends Property>> structureRegistry = new SimpleStructureRegistry<EntityStructure<? extends Property>>();
	private final ConfigurableServices<LuceneResolverExtend> luceneResolverExtends = new ConfigurableServices<LuceneResolverExtend>(
			LuceneResolverExtend.class);

	@Override
	public void configure(ServiceLoaderFactory serviceLoaderFactory) {
		luceneResolverExtends.configure(serviceLoaderFactory);
		super.configure(serviceLoaderFactory);
	}

	public ConfigurableServices<LuceneResolverExtend> getLuceneResolverExtends() {
		return luceneResolverExtends;
	}

	@Override
	public boolean isStructureRegistred(Class<?> entityClass) {
		return structureRegistry.isStructureRegistred(entityClass);
	}

	@Override
	public EntityStructure<? extends Property> getStructure(Class<?> entityClass) {
		EntityStructure<? extends Property> structure = structureRegistry.getStructure(entityClass);
		if (structure == null) {
			return super.getStructure(entityClass);
		}
		return structure;
	}

	@Override
	public void registerStructure(Class<?> entityClass, EntityStructure<? extends Property> structure) {
		structureRegistry.registerStructure(entityClass, structure);
	}

	@Override
	public Collection<org.apache.lucene.document.Field> resolve(ParameterDescriptor descriptor, Value value) {
		return LuceneResolverExtendChain.build(luceneResolverExtends.iterator()).resolve(descriptor, value);
	}

	private Query parseQuery(Condition condition, ConditionKeywords conditionKeywords) {
		RepositoryColumn column = condition.getColumn();
		if (column.getValue() == null) {
			return null;
		}

		if (conditionKeywords.getEqualKeywords().exists(condition.getCondition())) {
			// =
			Term term;
			if (column.getType() == byte[].class) {
				term = new Term(column.getName(), new BytesRef((byte[]) column.getValue()));
			} else {
				term = new Term(column.getName(), (String) getConversionService().convert(column.getValue(),
						column.getValueTypeDescriptor(), TypeDescriptor.valueOf(String.class)));
			}
			return new TermQuery(term);
		} else if (conditionKeywords.getInKeywords().exists(condition.getCondition())) {
			BooleanQuery.Builder builder = new BooleanQuery.Builder();
			List<?> list;
			TypeDescriptor elementTypeDescriptor;
			if (column.getValueTypeDescriptor().isArray() || column.getValueTypeDescriptor().isCollection()) {
				elementTypeDescriptor = column.getValueTypeDescriptor().getElementTypeDescriptor();
				list = (List<?>) getConversionService().convert(column.getValue(), column.getValueTypeDescriptor(),
						TypeDescriptor.collection(List.class, elementTypeDescriptor));
			} else {
				list = Arrays.asList(column.getValue());
				elementTypeDescriptor = column.getValueTypeDescriptor();
			}

			for (Object value : list) {
				Term term;
				if (elementTypeDescriptor.getType() == byte[].class) {
					term = new Term(column.getName(), new BytesRef((byte[]) value));
				} else {
					term = new Term(column.getName(), (String) getConversionService().convert(value,
							elementTypeDescriptor, TypeDescriptor.valueOf(String.class)));
				}
				// 或
				builder.add(new TermQuery(term), Occur.SHOULD);
			}
			return builder.build();
		} else {
			if (NumberUtils.isInteger(column.getType())) {
				long min = 0;
				long max = 0;
				Long value = (Long) getConversionService().convert(column.getValue(), column.getValueTypeDescriptor(),
						TypeDescriptor.valueOf(Long.class));
				if (conditionKeywords.getEqualOrGreaterThanKeywords().exists(condition.getCondition())) {
					max = Long.MAX_VALUE;
					min = value;
				} else if (conditionKeywords.getGreaterThanKeywords().exists(condition.getCondition())) {
					max = Long.MAX_VALUE;
					min = value + 1;
				} else if (conditionKeywords.getEqualOrLessThanKeywords().exists(condition.getCondition())) {
					min = Long.MIN_VALUE;
					max = value;
				} else if (conditionKeywords.getLessThanKeywords().exists(condition.getCondition())) {
					min = Long.MIN_VALUE;
					max = value - 1;
				} else if (conditionKeywords.getNotEqualKeywords().exists(condition.getCondition())) {
					max = value + 1;
					min = value - 1;
				}
				return LongPoint.newRangeQuery(column.getName(), min, max);
			} else {
				String value = (String) getConversionService().convert(column.getValue(),
						column.getValueTypeDescriptor(), TypeDescriptor.valueOf(String.class));
				String max = null;
				String min = null;
				boolean includeLower = false;
				boolean includeUpper = false;
				if (conditionKeywords.getEqualOrGreaterThanKeywords().exists(condition.getCondition())) {
					min = value;
					includeLower = true;
				} else if (conditionKeywords.getGreaterThanKeywords().exists(condition.getCondition())) {
					min = value;
				} else if (conditionKeywords.getEqualOrLessThanKeywords().exists(condition.getCondition())) {
					max = value;
					includeUpper = true;
				} else if (conditionKeywords.getLessThanKeywords().exists(condition.getCondition())) {
					max = value;
				} else if (conditionKeywords.getNotEqualKeywords().exists(condition.getCondition())) {
					max = value;
					min = value;
				}
				return new TermRangeQuery(column.getName(), min == null ? null : new BytesRef(min),
						max == null ? null : new BytesRef(max), includeLower, includeUpper);
			}
		}
	}

	private Query parseQuery(Conditions conditions, RelationshipKeywords relationshipKeywords,
			ConditionKeywords conditionKeywords) {
		if (conditions == null) {
			return null;
		}

		Query firstQuery = null;
		if (conditions.getCondition() != null && !conditions.getCondition().isEmpty()) {
			firstQuery = parseQuery(conditions.getCondition(), conditionKeywords);
		}

		List<WithCondition> withConditions = conditions.getWiths();
		if (CollectionUtils.isEmpty(withConditions)) {
			return firstQuery;
		}

		BooleanQuery.Builder builder = new BooleanQuery.Builder();
		if (firstQuery != null) {
			builder.add(firstQuery, Occur.MUST);
		}

		for (WithCondition condition : withConditions) {
			Occur occur = parseOccur(relationshipKeywords, condition.getWith());
			Query query = parseQuery(condition.getCondition(), relationshipKeywords, conditionKeywords);
			if (query == null) {
				continue;
			}
			builder.add(query, occur);
		}
		return builder.build();
	}

	/**
	 * 1．MUST和MUST：取得连个查询子句的交集。 2．MUST和MUST_NOT：表示查询结果中不能包含MUST_NOT所对应得查询子句的检索结果。
	 * 3．SHOULD与MUST_NOT：连用时，功能同MUST和MUST_NOT。
	 * 4．SHOULD与MUST连用时，结果为MUST子句的检索结果,但是SHOULD可影响排序。
	 * 5．SHOULD与SHOULD：表示“或”关系，最终检索结果为所有检索子句的并集。 6．MUST_NOT和MUST_NOT：无意义，检索无结果。
	 * 
	 * @param relationshipKeywords
	 * @param condition
	 * @return
	 */
	private Occur parseOccur(RelationshipKeywords relationshipKeywords, String condition) {
		if (relationshipKeywords.getAndKeywords().exists(condition)) {
			return Occur.MUST;
		} else if (relationshipKeywords.getOrKeywords().exists(condition)) {
			return Occur.SHOULD;
		}
		return null;
	}

	@Override
	public Query parseQuery(Conditions conditions) {
		return parseQuery(conditions, getRelationshipKeywords(), getConditionKeywords());
	}

	@Override
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

	@Override
	public Sort parseSort(EntityStructure<? extends Property> structure, List<? extends OrderColumn> orders) {
		if (CollectionUtils.isEmpty(orders)) {
			return null;
		}

		List<SortField> sortFields = new ArrayList<SortField>();
		appendSort(structure, sortFields, orders);
		return new Sort(sortFields.toArray(new SortField[0]));
	}

	private void appendSort(EntityStructure<? extends Property> structure, List<SortField> sortFields,
			List<? extends OrderColumn> orders) {
		if (CollectionUtils.isEmpty(orders)) {
			return;
		}

		for (OrderColumn column : orders) {
			Property property = structure.getByName(column.getName());
			if (NumberUtils.isNumber(property.getField().getGetter().getType())) {
				if (NumberUtils.isInteger(property.getField().getGetter().getType())) {
					sortFields.add(new SortField(column.getName(), Type.LONG,
							column.getSort() == io.basc.framework.util.comparator.Sort.ASC));
				} else {
					sortFields.add(new SortField(column.getName(), Type.DOUBLE,
							column.getSort() == io.basc.framework.util.comparator.Sort.ASC));
				}
			} else {
				sortFields.add(new SortField(column.getName(), Type.STRING,
						column.getSort() == io.basc.framework.util.comparator.Sort.ASC));
			}
			appendSort(structure, sortFields, column.getWithOrders());
		}
	}
}
