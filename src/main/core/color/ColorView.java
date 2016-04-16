package core.color;

import utils.Forkable;

import java.util.Collection;
import java.util.Set;

/**
 * Created by maxus on 28.03.16.
 */
public interface ColorView<F, G> extends Forkable<IColor<F, G>> {

	public G getGroup(F field);
	public Collection<F> getAdjacency(F field);
	public default boolean contains(F field) {
		return getFields().contains(field);
	}

	public Set<F> getFields(G group);
	public Set<F> getAdjacents(G group);
	public default F getAnyField(G group) {
		return getFields(group).iterator().next();
	}

	public Set<F> getFields();
	public Set<G> getGroups();



}
