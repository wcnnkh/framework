package run.soeasy.framework.core.exchange;

public interface ListenableStage<V> extends Stage<V>, ListenableSignal<Stage<V>> {
	@Override
	default ListenableStage<V> listenable() {
		return this;
	}
}
