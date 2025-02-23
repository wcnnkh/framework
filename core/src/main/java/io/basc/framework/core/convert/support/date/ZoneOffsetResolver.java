package io.basc.framework.core.convert.support.date;

import java.time.ZoneOffset;

import io.basc.framework.core.convert.SourceDescriptor;
import lombok.NonNull;

/**
 * 解析ZoneOffset
 * 
 * @author shuchaowen
 *
 */
public interface ZoneOffsetResolver {
	ZoneOffset resolveZoneOffset(@NonNull SourceDescriptor valueDescriptor);
}
