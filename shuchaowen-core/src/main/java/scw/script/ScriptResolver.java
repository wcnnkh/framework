package scw.script;

public interface ScriptResolver<T> {
	boolean isSupport(String script);

	T eval(ScriptEngine<T> engine, String script) throws ScriptException;
}
