package core.color;

import utils.Forkable;

import java.util.Collection;
import java.util.Set;

/**
 * Created by maxus on 28.03.16.
 */
public interface ColorView<F, G> extends Forkable<IColor<F, G>> {

	public G getgroup(F node);

	public Set<F> getstones(G group);
	public Set<F> getadjacent(G group);
	public default F getanystone(G group) {
		return getstones(group).iterator().next();
	}

	public Set<F> getallstones();
	public Set<G> getallgroups();

	public default boolean contains(F node) {
		return getallstones().contains(node);
	}
	public Collection<F> adjacency(F node);


}
