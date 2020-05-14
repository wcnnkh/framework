package scw.data.file;

import scw.core.Converter;
import scw.net.http.HttpUtils;
import scw.net.message.SerializableInputMessage;

public class SerializableHttpInputMessageCacheConvert implements
		Converter<String, SerializableInputMessage> {

	public SerializableInputMessage convert(String url) throws Exception {
		return HttpUtils.getHttpClient().getSerializableHttpInputMessage(url,
				null);
	}
}
