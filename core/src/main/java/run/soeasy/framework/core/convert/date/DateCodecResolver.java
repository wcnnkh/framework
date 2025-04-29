package run.soeasy.framework.core.convert.date;

import java.util.Date;

import lombok.NonNull;
import run.soeasy.framework.codec.Codec;
import run.soeasy.framework.core.convert.Readable;

public interface DateCodecResolver {
	Codec<Date, String> resolveDateCodec(@NonNull Readable valueDescriptor);
}
