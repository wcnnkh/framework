package io.basc.framework.data.repository;

public class ExpressionSymbol extends RepositorySymbol {
	private static final long serialVersionUID = 1L;
	public static final ExpressionSymbol EMPTY = new ExpressionSymbol("");
	public static final ExpressionSymbol MAX = new ExpressionSymbol("max");
	public static final ExpressionSymbol MIN = new ExpressionSymbol("min");
	public static final ExpressionSymbol IF = new ExpressionSymbol("if");

	public ExpressionSymbol(String name) {
		super(name);
	}

}
