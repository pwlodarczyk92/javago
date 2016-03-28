package core.color;

import java.util.Set;

/**
 * Created by maxus on 28.03.16.
 */
public interface ColorView<F, G> {

	public G getgroup(F node);
	public Set<F> getnodes(G group);
	public Set<F> allstones();
	public Set<G> getgroups();
	public default boolean contains(F node) {
		return allstones().contains(node);
	}

}
