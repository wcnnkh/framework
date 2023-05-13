package io.basc.framework.data.repository;

import java.io.Serializable;

import io.basc.framework.util.Elements;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 条件
 * <p>
 * {relationshipSymbol} {expression}
 * 
 * @author wcnnkh
 *
 */
@Data
@AllArgsConstructor
public class Condition implements Serializable {
	private static final long serialVersionUID = 1L;
	/**
	 * 和上一个条件的关系
	 */
	private final RelationshipSymbol relationshipSymbol;

	/**
	 * 左边的表达式
	 */
	private final Expression leftExpression;

	/**
	 * 表达式的条件
	 */
	private final ConditionSymbol conditionSymbol;

	/**
	 * 右边的表达式
	 */
	private final Expression rightExpression;

	/**
	 * 关联条件
	 */
	private final Elements<? extends Condition> withConditions;

	public Condition(Expression leftExpression, ConditionSymbol conditionSymbol, Expression rightExpression) {
		this(RelationshipSymbol.AND, leftExpression, conditionSymbol, rightExpression, null);
	}

	public Condition(RelationshipSymbol relationshipSymbol, Expression leftExpression, ConditionSymbol conditionSymbol,
			Expression rightExpression) {
		this(relationshipSymbol, leftExpression, conditionSymbol, rightExpression, null);
	}
}
