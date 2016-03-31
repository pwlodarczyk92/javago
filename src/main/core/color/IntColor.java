package core.color;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.function.Function;

/**
 * Created by maxus on 28.02.16.
 */
public class IntColor extends Color<Integer> {

	public IntColor(Function<Integer, Collection<Integer>> adjacency) {
		super(adjacency);
	}

	protected IntColor(Function<Integer, Collection<Integer>> adjacency,
					HashMap<Integer, Integer> roots,
					HashMap<Integer, HashSet<Integer>> families,
					HashMap<Integer, HashSet<Integer>> liberties) {
		super(adjacency, roots, families, liberties);
	}

	@Override
	public IntColor copy() {
		HashMap<Integer, HashSet<Integer>> nf = new HashMap<>();
		HashMap<Integer, HashSet<Integer>> nl = new HashMap<>();
		for (Map.Entry<Integer, HashSet<Integer>> f: families.entrySet()) {
			nf.put(f.getKey(), new HashSet<>(f.getValue()));
		}
		for (Map.Entry<Integer, HashSet<Integer>> l: liberties.entrySet()) {
			nl.put(l.getKey(), new HashSet<>(l.getValue()));
		}
		return new IntColor(adjacency,
				new HashMap<>(roots),
				nf,
				nl) {
		};
	}

}
