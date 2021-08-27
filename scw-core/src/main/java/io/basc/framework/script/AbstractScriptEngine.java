package io.basc.framework.script;

import java.util.LinkedList;

public abstract class AbstractScriptEngine<T> implements ScriptEngine<T> {
	protected final LinkedList<ScriptResolver<T>> resolvers = new LinkedList<ScriptResolver<T>>();

	public T eval(String script) throws ScriptException {
		for (ScriptResolver<T> resolver : getResolvers()) {
			if (resolver.isSupport(script)) {
				return resolver.eval(this, script);
			}
		}

		return evalInternal(script);
	}

	public LinkedList<ScriptResolver<T>> getResolvers() {
		return resolvers;
	}

	protected T evalInternal(String script) throws ScriptException{
		throw new ScriptException(script);
	}
}
