package core.color;

import java.util.*;
import java.util.function.Function;

/**
 * Created by maxus on 20.02.16.
 */

public class Color<Field> implements IColor<Field, Field>{

	//--internal structure--
	protected final HashMap<Field, Field> roots;
	protected final HashMap<Field, HashSet<Field>> families;
	protected final HashMap<Field, HashSet<Field>> liberties;
	//--internal structure--

	//--Field interface--
	protected final Function<Field, Collection<Field>> adjacency;
	//--Field interface--

	public Color(Function<Field, Collection<Field>> adjacency) {
		this(adjacency,
				new HashMap<>(),
				new HashMap<>(),
				new HashMap<>());
	}

	protected Color(Function<Field, Collection<Field>> adjacency,
					HashMap<Field, Field> roots,
					HashMap<Field, HashSet<Field>> families,
					HashMap<Field, HashSet<Field>> liberties) {
		this.adjacency = adjacency;
		this.roots = roots;
		this.families = families;
		this.liberties = liberties;
	}

	//--accessors--
	@Override
	public Field getgroup(Field node) {
		return roots.get(node);
	}
	@Override
	public Set<Field> getadjacent(Field root) {
		return liberties.get(root);
	}
	@Override
	public Set<Field> getnodes(Field root) {
		return families.get(root);
	}
	@Override
	public Set<Field> getallnodes() {
		return roots.keySet();
	}
	@Override
	public Field getanynode(Field group) {
		return group;
	}
	@Override
	public Set<Field> getgroups() {
		return families.keySet();
	}
	@Override
	public boolean contains(Field node) {
		return roots.containsKey(node);
	}
	//--accessors--

	//--modifiers--
	@Override
	public Set<Field> remgroup(Field root) {

		assert roots.get(root) == root;

		HashSet<Field> family = families.remove(root);
		family.stream().forEach(roots::remove);
		liberties.remove(root);

		return family;

	}

	@Override
	public Field addstone(Field node) {

		assert !roots.containsKey(node);

		Field maxroot = null;
		int maxgroupsize = 0;
		HashSet<Field> checkedroots = new HashSet<>();

		// for every group adjacent to a new stone
		// find biggest one and its root
		// save all traversed roots to <checkedroots>

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
			HashSet<Field> family = new HashSet<>();
			family.add(node);
			families.put(node, family);
			liberties.put(node, new HashSet<>(adjacency.apply(node)));
			roots.put(node, node);
			return node;
		}

		// else - make one big group from neighbors and new node;
		// merge families and liberties sets by adding
		// families and liberties of smaller groups to maxroot's ones

		checkedroots.remove(maxroot);
		HashSet<Field> maxfamily = families.get(maxroot);
		HashSet<Field> maxliberties = liberties.get(maxroot);

		for (Field subroot: checkedroots) {

			maxliberties.addAll(liberties.remove(subroot));
			HashSet<Field> nowfam = families.remove(subroot);
			maxfamily.addAll(nowfam);

			for (Field n: nowfam)
				roots.put(n, maxroot);

		}

		// include new node in a new group
		// include adjacent empty nodes in liberties
		// exclude new node from liberties
		// set root for a new stone

		for (Field adjacent: adjacency.apply(node)) {
			if (!this.contains(adjacent))
				maxliberties.add(adjacent);
		}
		maxliberties.remove(node);
		maxfamily.add(node);
		roots.put(node, maxroot);

		return node;

	}
	// --modifiers--

	// --identity, equality--
	@Override
	public Color<Field> copy() {
		HashMap<Field, HashSet<Field>> nf = new HashMap<>();
		HashMap<Field, HashSet<Field>> nl = new HashMap<>();
		for (Map.Entry<Field, HashSet<Field>> f: families.entrySet()) {
			nf.put(f.getKey(), new HashSet<>(f.getValue()));
		}
		for (Map.Entry<Field, HashSet<Field>> l: liberties.entrySet()) {
			nl.put(l.getKey(), new HashSet<>(l.getValue()));
		}
		return new Color<Field>(adjacency,
				new HashMap<>(roots),
				nf,
				nl) {
		};
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Color)) return false;

		Color intColor = (Color) o;

		return intColor.roots.keySet().equals(roots.keySet());
	}

	@Override
	public int hashCode() {
		return roots.size() + 31 * families.size();
	}
	// --identity, equality--

}
