package core.color;

import java.util.Set;

/**
 * Created by maxus on 20.02.16.
 */
public interface IColor<F, G> {

	public Set<F> getlibs(G group);
	public Set<F> getnodes(G group);
	public Set<F> allstones();
	public G getgroup(F node);
	public default boolean contains(F node) {
		return allstones().contains(node);
	}

	public Set<F> remgroup(G group);
	public G addstone(F node);


}
