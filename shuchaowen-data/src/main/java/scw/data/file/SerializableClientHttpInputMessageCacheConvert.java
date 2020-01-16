package scw.data.file;

import scw.core.Converter;
import scw.net.http.HttpUtils;
import scw.net.http.client.SerializableClientHttpInputMessage;

public class SerializableClientHttpInputMessageCacheConvert implements Converter<String, SerializableClientHttpInputMessage> {

	public SerializableClientHttpInputMessage convert(String url) throws Exception {
		return HttpUtils.getHttpClient().getToSerializableInputMessage(url);
	}
}
