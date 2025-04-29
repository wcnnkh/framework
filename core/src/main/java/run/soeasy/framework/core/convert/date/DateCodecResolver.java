package run.soeasy.framework.core.convert.date;

import java.util.Date;

import lombok.NonNull;
import run.soeasy.framework.codec.Codec;
import run.soeasy.framework.core.convert.SourceDescriptor;

public interface DateCodecResolver {
	Codec<Date, String> resolveDateCodec(@NonNull SourceDescriptor valueDescriptor);
}
