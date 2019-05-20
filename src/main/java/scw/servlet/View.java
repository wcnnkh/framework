package scw.servlet;

public interface View {
	void render(Request request, Response response) throws Exception;
}
