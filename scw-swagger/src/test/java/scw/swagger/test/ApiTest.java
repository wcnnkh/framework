package scw.swagger.test;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Path("abc")
@Tag(name="用户信息", description="用于获取用户基本信息")
public class ApiTest {
	@GET
	@Path("/gef")
	@Operation(description="用于获取名称", summary="获取名称", method="methodaaadasdfasdf")
	public String a() {
		return "";
	}
	
	@POST
	@Operation(description="获取手机号")
	@Path("/def")
	@Consumes("text/xml")
	public String b(String a, String b) {
		return "hello";
	}
}
