package core.color;

import com.google.common.collect.ImmutableSet;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

/**
 * Created by maxus on 14.04.16.
 */
public class ImmuColor<Field> extends Color<Field> {

	public ImmuColor(Function<Field, Collection<Field>> adjacency) {
		this(adjacency,
				new HashMap<>(),
				new HashMap<>(),
				new HashMap<>());
	}

	protected ImmuColor(Function<Field, Collection<Field>> adjacency,
					HashMap<Field, Field> roots,
					HashMap<Field, Set<Field>> families,
					HashMap<Field, Set<Field>> liberties) {
		super(adjacency, roots, families, liberties);
	}

	// --modifiers--
	@Override
	public Field addstone(Field node) {

		assert !roots.containsKey(node);

		Field maxroot = null;
		int maxgroupsize = 0;
		HashSet<Field> checkedroots = new HashSet<>();

		// for every group adjacent to a new stone
		// find biggest one and its root
		// save all traversed roots to 'checkedroots'

		for (Field adjacent: adjacency.apply(node)) {
			if (!this.contains(adjacent))
				continue;

			Field adjroot = this.roots.get(adjacent);
			if (checkedroots.contains(adjroot))
				continue;
			checkedroots.add(adjroot);

			int nowgroupsize = families.get(adjroot).size();
			if (maxgroupsize < nowgroupsize) {
				maxroot = adjroot;
				maxgroupsize = nowgroupsize;
			}
		}

		// no neighbors - make and return simple singleton

		if (maxroot == null) {
			Set<Field> family = ImmutableSet.of(node);
			families.put(node, family);
			liberties.put(node, ImmutableSet.copyOf(adjacency.apply(node)));
			roots.put(node, node);
			return node;
		}

		// else - make one big group from neighbors and new node;
		// create new sets of families and liberties that represent
		// newly created group; set new root to biggest neighbor group's root

		ImmutableSet.Builder<Field> familybuilder = ImmutableSet.builder();
		ImmutableSet.Builder<Field> libertybuilder = ImmutableSet.builder();

		// add node (the one from args) and its liberties to new sets

		familybuilder.add(node);

		for (Field adjacent: adjacency.apply(node))
			if (!this.contains(adjacent))
				libertybuilder.add(adjacent);

		// add surrounding groups' nodes and liberties to new sets, set new roots

		for (Field subroot: checkedroots) {
			Set<Field> fam = families.remove(subroot);
			familybuilder.addAll(fam);
			if (subroot != maxroot)
				for (Field n: fam)
					roots.put(n, maxroot);

			Set<Field> lib = liberties.remove(subroot);
			for (Field n: lib)
				if (!n.equals(node))
					libertybuilder.add(n);

		}

		// build the sets, modify object fields to represent the changes

		Set<Field> newfamily = familybuilder.build();
		Set<Field> newliberties = libertybuilder.build();

		roots.put(node, maxroot);
		families.put(maxroot, newfamily);
		liberties.put(maxroot, newliberties);

		return maxroot;

	}
	// --modifiers--

	@Override
	public ImmuColor<Field> fork() {
		return new ImmuColor<Field>(adjacency,
				new HashMap<>(roots),
				new HashMap<>(families),
				new HashMap<>(liberties)) {
		};
	}

}
