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

	public Collection<F> getfields();
	public Collection<F> adjacency(F node);

	public ColorView<F, G> getview(Stone s);
	public Stone getstone(F field);
	public Set<F> getlibs(Stone s, G group);

	public default Set<F> getalllibs() {
		Set<F> result = new HashSet<>();
		getview(Stone.WHITE).getallgroups().stream().forEach(g -> result.addAll(getlibs(Stone.WHITE, g)));
		getview(Stone.BLACK).getallgroups().stream().forEach(g -> result.addAll(getlibs(Stone.BLACK, g)));
		return result;
	}

	public default Set<F> getmoorelibs() {
		Set<F> result = new HashSet<>();
		Set<F> empty = new HashSet<>();
		Set<F> libs = getalllibs();

		for(F f: libs) {
			for (F field : adjacency(f)) {
				if (getstone(field) != Stone.EMPTY) continue;
				if (!empty.contains(field)) empty.add(field);
				else result.add(field);
			}
		}
		result.addAll(libs);

		return result;
	}
}
