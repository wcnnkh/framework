package scw.util.alias;

public interface AliasFactory {
	/**
	 * Determine whether this given name is defines as an alias
	 * (as opposed to the name of an actually registered component).
	 * @param name the name to check
	 * @return whether the given name is an alias
	 */
	boolean isAlias(String name);
	
	/**
	 * Determine whether the given name has the given alias registered.
	 * 
	 * @param name
	 *            the name to check
	 * @param alias
	 *            the alias to look for
	 */
	boolean hasAlias(String name, String alias);

	/**
	 * Return the aliases for the given name, if defined.
	 * @param name the name to check for aliases
	 * @return the aliases, or an empty array if none
	 */
	String[] getAliases(String name);
}
