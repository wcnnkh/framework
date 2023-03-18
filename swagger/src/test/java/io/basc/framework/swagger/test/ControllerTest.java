package io.basc.framework.swagger.test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import io.basc.framework.mvc.annotation.Controller;
import io.basc.framework.util.XUtils;

@Controller
@Path("")
public class ControllerTest {

	@Path("")
	@GET
	public String test() {
		return XUtils.getUUID();
	}

}
