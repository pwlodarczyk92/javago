package core.color;

import java.util.*;
import java.util.function.Function;

/**
 * Created by maxus on 20.02.16.
 */

public class Color<Field> implements IColor<Field, Field>{

	//--internal structure--
	protected HashMap<Field, Field> roots;
	protected HashMap<Field, Set<Field>> families;
	protected HashMap<Field, Set<Field>> liberties;
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
					HashMap<Field, Set<Field>> families,
					HashMap<Field, Set<Field>> liberties) {
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
	public Set<Field> getstones(Field root) {
		return families.get(root);
	}
	@Override
	public Set<Field> getadjacent(Field root) {
		return liberties.get(root);
	}
	@Override
	public Field getanystone(Field group) {
		return group;
	}

	@Override
	public Set<Field> getallstones() {
		return roots.keySet();
	}
	@Override
	public Set<Field> getallgroups() {
		return families.keySet();
	}

	@Override
	public boolean contains(Field node) {
		return roots.containsKey(node);
	}
	@Override
	public Collection<Field> adjacency(Field node) { return adjacency.apply(node); }
	//--accessors--

	//--modifiers--
	@Override
	public Set<Field> remgroup(Field root) {

		assert roots.get(root) == root;

		Set<Field> family = families.remove(root);
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
		Set<Field> maxfamily = families.get(maxroot);
		Set<Field> maxliberties = liberties.get(maxroot);

		for (Field subroot: checkedroots) {

			maxliberties.addAll(liberties.remove(subroot));
			Set<Field> nowfam = families.remove(subroot);
			maxfamily.addAll(nowfam);

			for (Field n: nowfam)
				roots.put(n, maxroot);

		}

		// include node (the one from args) related changes in the sets

		for (Field adjacent: adjacency.apply(node)) {
			if (!this.contains(adjacent))
				maxliberties.add(adjacent);
		}

		maxliberties.remove(node);
		maxfamily.add(node);
		roots.put(node, maxroot);

		return maxroot;

	}
	// --modifiers--

	// --identity, equality--
	@Override
	public Color<Field> fork() {
		HashMap<Field, Set<Field>> nf = new HashMap<>(families.size());
		HashMap<Field, Set<Field>> nl = new HashMap<>(liberties.size());
		for (Map.Entry<Field, Set<Field>> f: families.entrySet()) {
			nf.put(f.getKey(), new HashSet<>(f.getValue()));
		}
		for (Map.Entry<Field, Set<Field>> l: liberties.entrySet()) {
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

		Color color = (Color) o;

		return color.roots.keySet().equals(roots.keySet());
	}

	@Override
	public int hashCode() {
		return roots.keySet().hashCode();
	}
	// --identity, equality--

}
