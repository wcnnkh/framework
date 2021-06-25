package scw.swagger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "测试一下")
@Path("abc")
public class ApiTest {
	@Tag(name = "hello world")
	@GET
	@Path("/gef")
	public String a() {
		return "";
	}
}
