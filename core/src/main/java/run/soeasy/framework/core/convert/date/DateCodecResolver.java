package run.soeasy.framework.core.convert.date;

import java.util.Date;

import lombok.NonNull;
import run.soeasy.framework.core.convert.SourceDescriptor;
import run.soeasy.framework.util.codec.Codec;

public interface DateCodecResolver {
	Codec<Date, String> resolveDateCodec(@NonNull SourceDescriptor valueDescriptor);
}
