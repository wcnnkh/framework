package io.basc.framework.script;

public interface ScriptEngine<T> {
	T eval(String script) throws ScriptException;
}
