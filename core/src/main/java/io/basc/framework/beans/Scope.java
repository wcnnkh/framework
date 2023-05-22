package io.basc.framework.beans;

import io.basc.framework.util.Symbol;

public class Scope extends Symbol {
	private static final long serialVersionUID = 1L;
	public static final Scope SINGLETON = new Scope("singleton");

	public Scope(String name) {
		super(name);
	}

}
