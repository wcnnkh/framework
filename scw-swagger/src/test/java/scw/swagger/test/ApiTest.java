package scw.swagger.test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import io.swagger.v3.oas.annotations.tags.Tag;

@Path("abc")
public class ApiTest {
	@GET
	@Tag(description = "这是描述啊", name = "测试一下")
	@Path("/gef")
	public String a() {
		return "";
	}
}
