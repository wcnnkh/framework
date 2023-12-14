package io.basc.framework.data.repository;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.lang.Nullable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 条件表达式
 * 
 * @author wcnnkh
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Condition extends Expression {
	private static final long serialVersionUID = 1L;

	/**
	 * 和上一个条件的关系
	 */
	private final RelationshipSymbol relationshipSymbol;

	/**
	 * 表达式的条件
	 */
	private final ConditionSymbol conditionSymbol;

	public Condition(String name, ConditionSymbol conditionSymbol, @Nullable Object value,
			@Nullable TypeDescriptor valueTypeDescriptor) {
		this(RelationshipSymbol.AND, name, conditionSymbol, value, valueTypeDescriptor);
	}

	public Condition(RelationshipSymbol relationshipSymbol, String name, ConditionSymbol conditionSymbol,
			@Nullable Object value, @Nullable TypeDescriptor valueTypeDescriptor) {
		super(name, value, valueTypeDescriptor);
		this.relationshipSymbol = relationshipSymbol;
		this.conditionSymbol = conditionSymbol;
	}
}
