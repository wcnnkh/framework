package scw.core.net.response;

import java.io.InputStream;

import scw.core.ByteArray;
import scw.core.net.AbstractResponse;
import scw.core.utils.IOUtils;

public final class ByteArrayResponse extends AbstractResponse<ByteArray> {

	@Override
	public ByteArray doInput(InputStream is) throws Throwable {
		return IOUtils.read(is, 1024, -1);
	}

}
