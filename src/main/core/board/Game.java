package core.board;

import java.util.List;
import java.util.Set;

/**
 * Created by maxus on 03.04.16.
 */
public interface Game<F, G> {
	public StateView<F, G> getview();
	public List<F> moves();
	public Set<F> put(F field);
	public F undo();
}
