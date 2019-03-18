package scw.net.response;

import java.io.InputStream;

import scw.net.AbstractResponse;

public final class BodyResponse extends AbstractResponse<Body> {

	@Override
	public Body doInput(InputStream is) throws Throwable {
		return new Body(is);
	}

}
