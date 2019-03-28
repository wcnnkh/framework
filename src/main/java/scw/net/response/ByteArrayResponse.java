package scw.net.response;

import java.io.InputStream;

import scw.common.ByteArray;
import scw.common.utils.IOUtils;
import scw.net.AbstractResponse;

public final class ByteArrayResponse extends AbstractResponse<ByteArray> {

	@Override
	public ByteArray doInput(InputStream is) throws Throwable {
		return IOUtils.read(is, 1024, -1);
	}

}
