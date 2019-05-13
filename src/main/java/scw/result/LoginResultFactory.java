package scw.result;

public interface LoginResultFactory {
	<T extends Result> T loginExpired();
}
