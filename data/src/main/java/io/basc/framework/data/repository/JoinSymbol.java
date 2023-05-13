package io.basc.framework.data.repository;

public class JoinSymbol extends RepositorySymbol {
	private static final long serialVersionUID = 1L;

	public static final JoinSymbol LEFT_JOIN = new JoinSymbol("leftJoin");

	public JoinSymbol(String name) {
		super(name);
	}

}
