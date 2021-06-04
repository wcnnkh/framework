package scw.orm.sql.dialect;

import java.util.Collection;

public interface Where<T extends Where<T>> {
	T eq(String name, Expression expression);

	T neq(String name, Expression expression);

	T gt(String name, Expression expression);

	T lt(String name, Expression expression);

	T gte(String name, Expression expression);

	T lte(String name, Expression expression);

	T between(Expression start, Expression end);

	T like(Expression expression);

	T in(Collection<Expression> expressions);
}
