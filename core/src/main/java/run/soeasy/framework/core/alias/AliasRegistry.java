package run.soeasy.framework.core.alias;

public interface AliasRegistry extends AliasFactory {

	/**
	 * Given a name, register an alias for it.
	 * 
	 * @param name  the canonical name
	 * @param alias the alias to be registered
	 * @throws IllegalStateException if the alias is already in use and may not be
	 *                               overridden
	 */
	void registerAlias(String name, String alias);
}
