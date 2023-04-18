package io.basc.framework.orm.repository.adapter;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.Ordered;
import io.basc.framework.core.reflect.MethodInvoker;
import io.basc.framework.data.DataException;
import io.basc.framework.env.Sys;
import io.basc.framework.lang.NotFoundException;
import io.basc.framework.lang.UnsupportedException;
import io.basc.framework.mapper.Parameter;
import io.basc.framework.mapper.ParameterDescriptor;
import io.basc.framework.mapper.ParameterUtils;
import io.basc.framework.orm.repository.Condition;
import io.basc.framework.orm.repository.ConditionKeywords;
import io.basc.framework.orm.repository.Conditions;
import io.basc.framework.orm.repository.OperationKeywords;
import io.basc.framework.orm.repository.OrderColumn;
import io.basc.framework.orm.repository.RelationshipKeywords;
import io.basc.framework.orm.repository.Repository;
import io.basc.framework.orm.repository.RepositoryTemplate;
import io.basc.framework.orm.repository.WithCondition;
import io.basc.framework.util.CharSequenceSplitSegment;
import io.basc.framework.util.Elements;
import io.basc.framework.util.Keywords;
import io.basc.framework.util.Pair;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.comparator.Sort;
import io.basc.framework.util.page.Pagination;
import io.basc.framework.util.page.Paginations;

public class CustomRepositoryMethodAdapter extends CurdRepositoryMethodAdapter implements Ordered {
	private static final Keywords ORDER_BY_KEYWORDS = new Keywords(Keywords.HUMP, "orderBy");
	private final OperationKeywords operationKeywords = new OperationKeywords();
	private final Keywords orderByKeywords = new Keywords(ORDER_BY_KEYWORDS, Keywords.HUMP);
	private ConversionService conversionService;
	private int order = Ordered.LOWEST_PRECEDENCE;

	public OperationKeywords getOperationKeywords() {
		return operationKeywords;
	}

	@Override
	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public Keywords getOrderByKeywords() {
		return orderByKeywords;
	}

	public ConversionService getConversionService() {
		return conversionService == null ? Sys.getEnv().getConversionService() : conversionService;
	}

	public void setConversionService(ConversionService conversionService) {
		this.conversionService = conversionService;
	}

	@Override
	protected boolean test(Method method, String methodName, Class<?>[] parameterTypes) {
		return true;
	}

	@Override
	protected Object intercept(RepositoryTemplate template, MethodInvoker invoker, Object[] args, Class<?> entityClass,
			TypeDescriptor resultsTypeDescriptor, String methodName) throws Throwable {
		String express = methodName;
		String keywords = operationKeywords.startsWith(express);
		if (keywords == null) {
			throw new UnsupportedException(invoker.toString());
		}

		express = express.substring(keywords.length());
		return process(template.getRepository(), keywords, express, invoker, args, entityClass, resultsTypeDescriptor,
				template.getRepository().getMapper().getConditionKeywords(),
				template.getRepository().getMapper().getRelationshipKeywords());
	}

	private List<Parameter> parseColumns(MethodInvoker invoker, Object[] args) {
		List<Parameter> columns = new ArrayList<Parameter>();
		ParameterDescriptor[] parameterDescriptors = ParameterUtils.getParameters(invoker.getMethod());
		for (int i = 0; i < parameterDescriptors.length; i++) {
			columns.add(new Parameter(parameterDescriptors[i].getName(), args[i],
					new TypeDescriptor(parameterDescriptors[i])));
		}
		return columns;
	}

	private Object process(Repository repository, String operation, String express, MethodInvoker invoker,
			Object[] args, Class<?> entityClass, TypeDescriptor responseTypeDescriptor,
			ConditionKeywords conditionKeywords, RelationshipKeywords relationshipKeywords) throws Throwable {
		Pair<String, Integer> orderBy = orderByKeywords.indexOf(express);
		List<OrderColumn> orders = new ArrayList<OrderColumn>();
		if (orderBy != null) {
			List<CharSequenceSplitSegment> list = StringUtils
					.split(express.subSequence(orderBy.getValue() + orderBy.getKey().length(), express.length()),
							"Desc", "Aes")
					.collect(Collectors.toList());
			if (list.size() == 1) {
				CharSequenceSplitSegment segment = list.get(0);
				orders.add(new OrderColumn(segment.toString(), segment.getSeparator() == null ? Sort.DESC
						: Sort.valueOf(segment.getSeparator().toString().toUpperCase()), null));
			} else {
				for (CharSequenceSplitSegment segment : list) {
					orders.add(new OrderColumn(segment.toString(), segment.getSeparator() == null ? Sort.DESC
							: Sort.valueOf(segment.getSeparator().toString().toUpperCase()), null));
				}
			}
			express = express.substring(0, orderBy.getValue());
		}

		List<Parameter> repositoryColumns = parseColumns(invoker, args);
		Conditions conditions;
		int byIndex = express.indexOf("By");
		String columnsExpress = byIndex == -1 ? null : express.substring(0, byIndex);
		if (byIndex != -1) {
			express = express.substring(byIndex + 2);
		}

		Pair<String, Integer> lastIndex = relationshipKeywords.indexOf(express);
		if (lastIndex == null) {
			Condition condition = parseCondition(express, repositoryColumns, conditionKeywords);
			conditions = new Conditions(condition, null);
		} else {
			Condition root = parseCondition(express.substring(0, lastIndex.getValue()), repositoryColumns,
					conditionKeywords);
			List<WithCondition> withConditions = new ArrayList<WithCondition>();
			express = express.substring(lastIndex.getValue() + lastIndex.getKey().length());
			while (true) {
				Pair<String, Integer> pair = relationshipKeywords.indexOf(express);
				if (pair == null) {
					Condition condition = parseCondition(express, repositoryColumns, conditionKeywords);
					// 不进行嵌套处理
					withConditions.add(new WithCondition(lastIndex.getKey(), new Conditions(condition, null)));
					break;
				} else {
					Condition condition = parseCondition(express.substring(pair.getValue()), repositoryColumns,
							conditionKeywords);
					withConditions.add(new WithCondition(lastIndex.getKey(), new Conditions(condition, null)));
					express = express.substring(pair.getValue() + pair.getKey().length());
					lastIndex = pair;
				}
			}
			conditions = new Conditions(root, withConditions);
		}

		List<Parameter> columns = new ArrayList<Parameter>();
		if (columnsExpress != null) {
			String name = StringUtils.toLowerCase(columnsExpress, 0, 1);
			Parameter column = repositoryColumns.stream().filter((e) -> StringUtils.equals(e.getName(), name))
					.findFirst().orElseThrow(() -> new NotFoundException(name));
			columns.add(column);
		}

		if (operationKeywords.getQueryKeywords().exists(operation)) {
			// query
			if (responseTypeDescriptor.getType() == Paginations.class
					|| responseTypeDescriptor.getType() == Pagination.class) {
				return repository.query(responseTypeDescriptor.getGeneric(0), entityClass, conditions, orders);
			} else if (responseTypeDescriptor.isArray() || responseTypeDescriptor.isCollection()) {
				Elements<?> cursor = repository
						.query(responseTypeDescriptor.getElementTypeDescriptor(), entityClass, conditions, orders)
						.getElements();
				if (responseTypeDescriptor.isArray()) {
					return cursor.toList().toArray();
				} else {
					return cursor.toList();
				}
			} else {
				return repository.query(responseTypeDescriptor, entityClass, conditions, orders).getElements().first();
			}
		} else {
			long value;
			if (operationKeywords.getDeleteKeywords().exists(operation)) {
				// delete
				value = repository.delete(entityClass, conditions);
			} else if (operationKeywords.getSaveKeywords().exists(operation)) {
				value = repository.saveColumns(entityClass, columns);
			} else if (operationKeywords.getUpdateKeywords().exists(operation)) {
				value = repository.update(entityClass, columns, conditions);
			} else {
				throw new UnsupportedException(operation + "=" + invoker.toString());
			}

			return getConversionService().convert(value, TypeDescriptor.valueOf(long.class), responseTypeDescriptor);
		}
	}

	private Condition parseCondition(String express, List<Parameter> repositoryColumns,
			ConditionKeywords conditionKeywords) {
		Pair<String, Integer> pair = conditionKeywords.indexOf(express);
		if (pair == null) {
			// 没有条件
			String name = StringUtils.toLowerCase(express, 0, 1);
			Parameter column = repositoryColumns.stream().filter((e) -> StringUtils.equals(e.getName(), name))
					.findFirst().orElseThrow(() -> new NotFoundException(name));
			return new Condition(null, column);
		}

		if (pair.getValue() == 0) {
			String name = express.substring(pair.getValue() + pair.getKey().length());
			Parameter column = repositoryColumns.stream().filter((e) -> StringUtils.equals(e.getName(), name))
					.findFirst().orElseThrow(() -> new NotFoundException(name));
			return new Condition(pair.getKey(), column);
		}
		throw new DataException(express);
	}
}
