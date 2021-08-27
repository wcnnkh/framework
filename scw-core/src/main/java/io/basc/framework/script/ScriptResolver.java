package io.basc.framework.script;

public interface ScriptResolver<T> {
	boolean isSupport(String script);

	T eval(ScriptEngine<T> engine, String script) throws ScriptException;
}
