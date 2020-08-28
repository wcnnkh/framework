package scw.context;

public interface ContextExecute<V> {
	V execute(Context context) throws Throwable;
}
