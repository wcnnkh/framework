package scw.script;

public interface ScriptEngine<T> {
	T eval(String script) throws ScriptException;
}
