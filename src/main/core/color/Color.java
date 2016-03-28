package core.color;

import java.util.*;
import java.util.function.Function;

/**
 * Created by maxus on 20.02.16.
 */

public abstract class Color<Field> implements IColor<Field, Field>{

	//--internal structure--
	protected final HashMap<Field, Field> roots;
	protected final HashMap<Field, HashSet<Field>> families;
	protected final HashMap<Field, HashSet<Field>> liberties;
	protected final HashMap<Field, Integer> ranks;
	//--internal structure--

	//--Field interface--
	protected final Function<Field, Collection<Field>> adjacency;
	//--Field interface--

	public Color(Function<Field, Collection<Field>> adjacency) {
		this(adjacency,
				new HashMap<>(),
				new HashMap<>(),
				new HashMap<>(),
				new HashMap<>());
	}

	protected Color(Function<Field, Collection<Field>> adjacency,
					HashMap<Field, Field> roots,
					HashMap<Field, HashSet<Field>> families,
					HashMap<Field, HashSet<Field>> liberties,
					HashMap<Field, Integer> ranks) {
		this.adjacency = adjacency;
		this.roots = roots;
		this.families = families;
		this.liberties = liberties;
		this.ranks = ranks;
	}

	//--interface--
	@Override
	public Set<Field> getlibs(Field root) {
		return liberties.get(root);
	}
	@Override
	public Set<Field> getnodes(Field root) {
		return families.get(root);
	}
	@Override
	public Set<Field> allstones() {
		return roots.keySet();
	}
	@Override
	public Field getgroup(Field node) {
		Field nroot = this.roots.get(node);
		Field froot = this.roots.get(nroot);
		if (froot != nroot) {
			froot = this.getgroup(froot);
			this.roots.put(node, froot);
		}
		//assert this.getgroup(froot) == froot;
		//yo dawg, i herd you like recursion
		return froot;
	}
	@Override
	public boolean contains(Field node) {
		return this.roots.containsKey(node);
	}

	@Override
	public Set<Field> remgroup(Field root) {

		assert this.roots.get(root) == root;

		HashSet<Field> family = this.families.remove(root);
		family.stream().forEach(this.roots::remove);
		this.liberties.remove(root);
		this.ranks.remove(root);

		return family;

	}
	@Override
	public Field addstone(Field node) {

		Field root = node;
		makeSingleton(node);

		for (Field adjacent: adjacency.apply(node)) {
			if (!this.contains(adjacent))
				continue;

			Field aroot = this.roots.get(adjacent);
			if (!aroot.equals(root)) {
				root = merge(aroot, root);
			}
		}

		HashSet<Field> libs = this.liberties.get(root);
		libs.remove(node);

		assert this.getgroup(root) == root;
		return root;

	}

	//--interface--

	// --union--
	// compose, merge and makeSingleton should be considered together with add:
	// invoking these functions separately leaves inconsistent state

	// merges disjoint sets that are supposed to be connected
	private Field merge(Field root1, Field root2) {

		assert !root1.equals(root2);
		assert this.roots.get(root1) == root1;
		assert this.roots.get(root2) == root2;

		Field nroot;
		Integer rank1 = this.ranks.get(root1);
		Integer rank2 = this.ranks.get(root2);
		if (rank1 <= rank2) {
			nroot = compose(root1, root2);
		} else {
			nroot = compose(root2, root1);
		}
		if(rank1.equals(rank2)) {
			this.ranks.put(nroot, rank1+1);
		}
		return nroot;
	}

	private Field compose(Field subroot, Field superroot) {
		this.roots.put(subroot, superroot);
		this.families.get(superroot).addAll(this.families.get(subroot));
		this.liberties.get(superroot).addAll(this.liberties.get(subroot));

		this.families.remove(subroot);
		this.liberties.remove(subroot);
		this.ranks.remove(subroot);
		return superroot;
	}

	// creates single element disjoint set, assuming it's not connected
	private void makeSingleton(Field node) {

		assert !roots.containsKey(node);

		HashSet<Field> family = new HashSet<>();
		family.add(node);

		HashSet<Field> liberty = new HashSet<>();
		for(Field adj: adjacency.apply(node)) {
			if (!this.contains(adj))
				liberty.add(adj);
		}

		roots.put(node, node);
		families.put(node, family);
		liberties.put(node, liberty);
		ranks.put(node, 0);

	}
	// --union--

}
