package io.basc.framework.data.repository;

import io.basc.framework.util.Symbol;
import io.basc.framework.util.collection.Elements;

/**
 * 关系符号
 * 
 * @author wcnnkh
 *
 */
public class RelationshipSymbol extends Symbol {
	private static final long serialVersionUID = 1L;

	public static final RelationshipSymbol AND = new RelationshipSymbol("And");

	public static final RelationshipSymbol OR = new RelationshipSymbol("Or");

	public static final RelationshipSymbol NOT = new RelationshipSymbol("Not");

	public RelationshipSymbol(String name) {
		super(name);
	}

	public static Elements<RelationshipSymbol> getRelationshipSymbol() {
		return getSymbols(RelationshipSymbol.class);
	}

	public static Elements<RelationshipSymbol> getRelationshipSymbol(String name) {
		return getSymbols(RelationshipSymbol.class, name);
	}
}
