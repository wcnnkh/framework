package io.basc.framework.core.convert.support.date;

import java.util.Date;

import io.basc.framework.core.convert.SourceDescriptor;
import io.basc.framework.util.codec.Codec;
import lombok.NonNull;

public interface DateCodecResolver {
	Codec<Date, String> resolveDateCodec(@NonNull SourceDescriptor valueDescriptor);
}
