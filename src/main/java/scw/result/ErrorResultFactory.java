package scw.result;

public interface ErrorResultFactory {
	<T extends Result> T error();

	<T extends Result> T error(int code, String msg);

	<T extends Result> T error(int code);

	<T extends Result> T error(String msg);
}
