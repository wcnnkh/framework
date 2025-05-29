package run.soeasy.framework.codec.lang;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.IntFunction;

import run.soeasy.framework.codec.Codec;
import run.soeasy.framework.codec.DecodeException;
import run.soeasy.framework.codec.EncodeException;
import run.soeasy.framework.codec.binary.ToBytesCodec;
import run.soeasy.framework.core.Assert;
import run.soeasy.framework.core.io.Bits;

public class MapRecordCodec<K, V> implements ToBytesCodec<Map<K, V>> {
	private final RecordCodec<K> keyCodec;
	private final RecordCodec<V> valueCodec;
	private final IntFunction<Map<K, V>> mapSupplier;

	public MapRecordCodec(Codec<K, byte[]> keyCodec, Codec<V, byte[]> valueCodec) {
		this(keyCodec, valueCodec, (size) -> new LinkedHashMap<K, V>(size));
	}

	public MapRecordCodec(Codec<K, byte[]> keyCodec, Codec<V, byte[]> valueCodec, IntFunction<Map<K, V>> mapSupplier) {
		Assert.requiredArgument(keyCodec != null, "keyCodec");
		Assert.requiredArgument(valueCodec != null, "valueCodec");
		Assert.requiredArgument(mapSupplier != null, "mapSupplier");
		this.keyCodec = new RecordCodec<K>(keyCodec);
		this.valueCodec = new RecordCodec<V>(valueCodec);
		this.mapSupplier = mapSupplier;
	}

	@Override
	public void encode(Map<K, V> source, OutputStream target) throws IOException, EncodeException {
		Bits.writeInt(source == null ? 0 : source.size(), target);
		if (source != null) {
			for (Entry<K, V> entry : source.entrySet()) {
				keyCodec.encode(entry.getKey(), target);
				valueCodec.encode(entry.getValue(), target);
			}
		}
	}

	@Override
	public Map<K, V> decode(InputStream source, int bufferSize) throws IOException, DecodeException {
		int size = Bits.readInt(source);
		if (size == 0) {
			return Collections.emptyMap();
		}

		Map<K, V> map = mapSupplier.apply(size);
		for (int i = 0; i < size; i++) {
			map.put(keyCodec.decode(source, bufferSize), valueCodec.decode(source, bufferSize));
		}
		return map;
	}

}
