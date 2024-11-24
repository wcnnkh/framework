package io.basc.framework.lucene.support;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongPoint;
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

import io.basc.framework.beans.factory.ServiceLoaderFactory;
import io.basc.framework.beans.factory.config.ConfigurableServices;
import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.execution.param.Parameter;
import io.basc.framework.data.repository.Condition;
import io.basc.framework.data.repository.ConditionSymbol;
import io.basc.framework.data.repository.Expression;
import io.basc.framework.data.repository.Operation;
import io.basc.framework.data.repository.RelationshipSymbol;
import io.basc.framework.data.repository.SortOrder;
import io.basc.framework.lucene.DocumentProperties;
import io.basc.framework.lucene.LuceneMapper;
import io.basc.framework.lucene.annotation.AnnotationLuceneResolverExtend;
import io.basc.framework.orm.support.DefaultEntityMapper;
import io.basc.framework.util.Elements;
import io.basc.framework.util.NumberUtils;
import io.basc.framework.util.collect.CollectionUtils;

public class DefaultLuceneMapper extends DefaultEntityMapper implements LuceneMapper {
	private final ConfigurableServices<LuceneResolverExtend> luceneResolverExtends = new ConfigurableServices<LuceneResolverExtend>(
			LuceneResolverExtend.class);

	public DefaultLuceneMapper() {
		luceneResolverExtends.register(new AnnotationLuceneResolverExtend());
		registerPropertiesTransformer(Document.class, (s, e) -> new DocumentProperties(s, this));
	}

	@Override
	public void configure(ServiceLoaderFactory serviceLoaderFactory) {
		luceneResolverExtends.configure(serviceLoaderFactory);
		super.configure(serviceLoaderFactory);
	}

	public ConfigurableServices<LuceneResolverExtend> getLuceneResolverExtends() {
		return luceneResolverExtends;
	}

	@Override
	public Collection<org.apache.lucene.document.Field> resolve(Parameter parameter) {
		if (parameter == null || !parameter.isPresent()) {
			return Collections.emptyList();
		}

		return LuceneResolverExtendChain.build(luceneResolverExtends.getServices().iterator()).resolve(parameter);
	}

	private String toString(Condition condition) {
		String value;
		if (canConvert(condition.getTypeDescriptor(), String.class)) {
			value = convert(condition.getValue(), condition.getTypeDescriptor(), String.class);
		} else {
			value = condition.getAsString();
		}
		return value;
	}

	private Query toQuery(Operation operation, Condition condition) {
		if (condition.getConditionSymbol().getName().equals(ConditionSymbol.EQU.getName())) {
			// =
			Term term;
			if (condition.getTypeDescriptor().getType() == byte[].class) {
				term = new Term(condition.getName(), new BytesRef((byte[]) condition.getValue()));
			} else {
				String value = toString(condition);
				term = new Term(condition.getName(), value);
			}
			return new TermQuery(term);
		} else if (condition.getConditionSymbol().getName().equals(ConditionSymbol.IN.getName())) {
			BooleanQuery.Builder builder = new BooleanQuery.Builder();
			List<?> list;
			TypeDescriptor elementTypeDescriptor;
			if (condition.getTypeDescriptor().isArray() || condition.getTypeDescriptor().isCollection()) {
				elementTypeDescriptor = condition.getTypeDescriptor().getElementTypeDescriptor();
				TypeDescriptor targetType = TypeDescriptor.collection(List.class, elementTypeDescriptor);
				if (canConvert(condition.getTypeDescriptor(), targetType)) {
					list = (List<?>) convert(condition.getValue(), condition.getTypeDescriptor(), targetType);
				} else {
					list = (List<?>) condition.getAsObject(targetType);
				}
			} else {
				list = Arrays.asList(condition.getValue());
				elementTypeDescriptor = condition.getTypeDescriptor();
			}

			for (Object value : list) {
				Term term;
				if (elementTypeDescriptor.getType() == byte[].class) {
					term = new Term(condition.getName(), new BytesRef((byte[]) value));
				} else {
					term = new Term(condition.getName(), toString(condition));
				}
				// 或
				builder.add(new TermQuery(term), Occur.SHOULD);
			}
			return builder.build();
		} else {
			if (NumberUtils.isInteger(condition.getTypeDescriptor().getType())) {
				long min = 0;
				long max = 0;
				Long value = condition.getAsLong();
				if (condition.getConditionSymbol().getName().equals(ConditionSymbol.GEQ.getName())) {
					max = Long.MAX_VALUE;
					min = value;
				} else if (condition.getConditionSymbol().getName().equals(ConditionSymbol.GTR.getName())) {
					max = Long.MAX_VALUE;
					min = value + 1;
				} else if (condition.getConditionSymbol().getName().equals(ConditionSymbol.LEQ.getName())) {
					min = Long.MIN_VALUE;
					max = value;
				} else if (condition.getConditionSymbol().getName().equals(ConditionSymbol.LSS.getName())) {
					min = Long.MIN_VALUE;
					max = value - 1;
				} else if (condition.getConditionSymbol().getName().equals(ConditionSymbol.NEQ.getName())) {
					max = value + 1;
					min = value - 1;
				}
				return LongPoint.newRangeQuery(condition.getName(), min, max);
			} else {
				String value = toString(condition);
				String max = null;
				String min = null;
				boolean includeLower = false;
				boolean includeUpper = false;
				if (condition.getConditionSymbol().getName().equals(ConditionSymbol.GEQ.getName())) {
					min = value;
					includeLower = true;
				} else if (condition.getConditionSymbol().getName().equals(ConditionSymbol.GTR.getName())) {
					min = value;
				} else if (condition.getConditionSymbol().getName().equals(ConditionSymbol.LEQ.getName())) {
					max = value;
					includeUpper = true;
				} else if (condition.getConditionSymbol().getName().equals(ConditionSymbol.LSS.getName())) {
					max = value;
				} else if (condition.getConditionSymbol().getName().equals(ConditionSymbol.NEQ.getName())) {
					max = value;
					min = value;
				}
				return new TermRangeQuery(condition.getName(), min == null ? null : new BytesRef(min),
						max == null ? null : new BytesRef(max), includeLower, includeUpper);
			}
		}
	}

	@Override
	public Query createQuery(Operation operation, Elements<? extends Condition> conditions) {
		BooleanQuery.Builder builder = new BooleanQuery.Builder();
		for (Condition condition : conditions) {
			Occur occur = parseOccur(condition.getRelationshipSymbol());
			Query query = toQuery(operation, condition);
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
	 * @param relationshipSymbol
	 * @param condition
	 * @return
	 */
	private Occur parseOccur(RelationshipSymbol relationshipSymbol) {
		if (relationshipSymbol == null) {
			return null;
		}
		if (relationshipSymbol.getName().equals(RelationshipSymbol.AND.getName())) {
			return Occur.MUST;
		} else if (relationshipSymbol.getName().equals(RelationshipSymbol.OR.getName())) {
			return Occur.SHOULD;
		} else if (relationshipSymbol.getName().equals(RelationshipSymbol.NOT.getName())) {
			return Occur.MUST_NOT;
		}
		return Occur.valueOf(relationshipSymbol.getName());
	}

	@Override
	public Sort createSort(Operation operation, Elements<? extends io.basc.framework.data.repository.Sort> orders) {
		if (CollectionUtils.isEmpty(orders)) {
			return null;
		}

		SortField[] sortFields = orders.map((e) -> toSortField(e)).toArray(new SortField[0]);
		return new Sort(sortFields);
	}

	private SortField toSortField(io.basc.framework.data.repository.Sort sort) {
		Parameter column = sort.getExpression();
		Type type;
		if (NumberUtils.isNumber(column.getTypeDescriptor().getType())) {
			if (NumberUtils.isInteger(column.getTypeDescriptor().getType())) {
				type = Type.LONG;
			} else {
				type = Type.DOUBLE;
			}
		} else {
			type = Type.STRING;
		}
		return new SortField(column.getName(), type, sort.getOrder().getName().equals(SortOrder.ASC.getName()));
	}

	@Override
	public Document createDocument(Operation operation, Elements<? extends Expression> columns) {
		Document document = new Document();
		for (Expression expression : columns) {
			for (Field field : resolve(expression)) {
				document.add(field);
			}
		}
		return document;
	}
}
