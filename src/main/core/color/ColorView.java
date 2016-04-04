package core.color;

import java.util.Set;

/**
 * Created by maxus on 28.03.16.
 */
public interface ColorView<F, G> {

	public G getgroup(F node);
	public Set<G> getgroups();

	public Set<F> getnodes(G group);
	public Set<F> getadjacent(G group);

	public Set<F> getallnodes();
	public default F getanynode(G group) {
		return getnodes(group).iterator().next();
	}

	public default boolean contains(F node) {
		return getallnodes().contains(node);
	}

}
