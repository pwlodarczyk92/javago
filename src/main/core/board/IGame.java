package core.board;

import utils.Forkable;

import java.util.List;
import java.util.Set;

/**
 * Created by maxus on 03.04.16.
 */
public interface IGame<F, G> extends Forkable<IGame<F, G>> {
	public Set<F> put(F field);
	public F undo();

	public IState<F, G> getState();
	public List<F> getMoves();
	public boolean isKoViolatedBy(IState<F, G> state);
}
