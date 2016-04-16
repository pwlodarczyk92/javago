package core.color;

import java.util.Set;

/**
 * Created by maxus on 20.02.16.
 */
public interface IColor<F, G> extends ColorView<F, G> {

	public Set<F> removeGroup(G group);
	public G addStone(F node);

}
