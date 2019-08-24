package scw.login;

import scw.core.utils.XUtils;

public abstract class AbstractLoginFactory implements LoginFactory {
	protected String generatorId(String prefix) {
		return prefix + XUtils.getUUID();
	}
}
