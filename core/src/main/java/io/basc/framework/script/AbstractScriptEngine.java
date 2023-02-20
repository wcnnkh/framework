package io.basc.framework.script;

import java.util.LinkedList;
import java.util.List;

public abstract class AbstractScriptEngine<T> implements ScriptEngine<T> {
	protected final List<ScriptResolver<T>> resolvers = new LinkedList<ScriptResolver<T>>();

	public T eval(String script) throws ScriptException {
		for (ScriptResolver<T> resolver : getResolvers()) {
			if (resolver.isSupport(script)) {
				return resolver.eval(this, script);
			}
		}

		return evalInternal(script);
	}

	/**
	 * 可操作的对象
	 * 
	 * @return
	 */
	public List<ScriptResolver<T>> getResolvers() {
		return resolvers;
	}

	protected T evalInternal(String script) throws ScriptException {
		throw new ScriptException(script);
	}
}
