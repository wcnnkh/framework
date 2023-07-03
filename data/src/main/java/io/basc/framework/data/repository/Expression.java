package io.basc.framework.data.repository;

import java.io.Serializable;

import io.basc.framework.util.Elements;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 表达式
 * <p>
 * column
 * <p>
 * max(column)
 * <p>
 * if(condition, column1, column2)
 * 
 * @author wcnnkh
 *
 */
@Data
@AllArgsConstructor
public class Expression implements Serializable {
	private static final long serialVersionUID = 1L;
	/**
	 * 表达式符号
	 */
	private final ExpressionSymbol expressionSymbol;

	/**
	 * 表达式条件
	 */
	private final Elements<? extends Condition> conditions;

	/**
	 * 表达式参数
	 */
	private final Elements<? extends Column> parameters;

	/**
	 * 别名
	 */
	private final String aliasName;

	public Expression(Repository repository, String name) {
		this(new Column(repository, name));
	}

	public Expression(Column column) {
		this(null, null, Elements.singleton(column), null);
	}
}
