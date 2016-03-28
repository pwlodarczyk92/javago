package core.color;

import java.util.Set;

/**
 * Created by maxus on 20.02.16.
 */
public interface IColor<F, G> extends ColorView<F, G> {

	public Set<F> getlibs(G group);
	public Set<F> remgroup(G group);
	public G addstone(F node);

}
