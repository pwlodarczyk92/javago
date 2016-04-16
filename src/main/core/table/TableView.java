package core.table;

import core.Stone;
import core.color.ColorView;
import utils.Forkable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by maxus on 28.03.16.
 */
public interface TableView<F, G> extends Forkable<ITable<F, G>> {

	public Collection<F> getFields();
	public Collection<F> getAdjacency(F node);

	public ColorView<F, G> getView(Stone s);
	public Stone getStone(F field);
	public Set<F> getLiberties(Stone s, G group);

	public default Set<F> getAllLiberties() {
		Set<F> result = new HashSet<>();
		getView(Stone.WHITE).getGroups().stream().forEach(g -> result.addAll(getLiberties(Stone.WHITE, g)));
		getView(Stone.BLACK).getGroups().stream().forEach(g -> result.addAll(getLiberties(Stone.BLACK, g)));
		return result;
	}

	public default Set<F> getMooreLiberties() {
		Set<F> result = new HashSet<>();
		Set<F> emptyAdjacents = new HashSet<>();
		Set<F> liberties = getAllLiberties();

		for(F l: liberties) {
			for (F a : getAdjacency(l)) {
				if (getStone(a) != Stone.EMPTY) continue;
				if (!emptyAdjacents.contains(a)) emptyAdjacents.add(a);
				else result.add(a);
			}
		}
		result.addAll(liberties);

		return result;
	}
}
