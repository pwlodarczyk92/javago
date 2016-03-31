package core.color;

import utils.Copyable;

import java.util.Set;

/**
 * Created by maxus on 20.02.16.
 */
public interface IColor<F, G> extends ColorView<F, G>, Copyable<IColor<F, G>> {

	public Set<F> remgroup(G group);
	public G addstone(F node);
	public IColor<F, G> copy();

}
