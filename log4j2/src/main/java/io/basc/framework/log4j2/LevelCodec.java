package io.basc.framework.log4j2;

import java.util.logging.Level;

import io.basc.framework.codec.Codec;
import io.basc.framework.codec.DecodeException;
import io.basc.framework.codec.EncodeException;
import io.basc.framework.logger.CustomLevel;

public class LevelCodec implements Codec<Level, org.apache.logging.log4j.Level> {
	public static final LevelCodec INSTANCE = new LevelCodec();

	@Override
	public org.apache.logging.log4j.Level encode(Level source) throws EncodeException {
		org.apache.logging.log4j.Level lv = org.apache.logging.log4j.Level.getLevel(source.getName());
		if (lv == null) {
			lv = org.apache.logging.log4j.Level.forName(source.getName(), source.intValue());
		}
		return lv;
	}

	@Override
	public Level decode(org.apache.logging.log4j.Level source) throws DecodeException {
		Level level = Level.parse(source.name());
		if (level == null) {
			level = new CustomLevel(source.name(), source.intLevel());
		}
		return level;
	}

}
