package run.soeasy.framework.data;

import run.soeasy.framework.core.streaming.Mapping;

public interface GroupRepository<K, S, T, M extends Mapping<K, T>> extends Repository<K, M> {
}
