package core;

import java.util.*;
import java.util.function.Function;

/**
 * Created by maxus on 20.02.16.
 */

public class Groups<Field> implements IGroups<Field>{

	//--internal structure--
	private int size = 0;
	private final HashMap<Field, Field> roots = new HashMap<>();
	private final HashMap<Field, HashSet<Field>> families = new HashMap<>();
	private final HashMap<Field, Integer> ranks = new HashMap<>();
	//--internal structure--

	//Field interface
	private final Function<Field, Iterable<Field>> adjacency;

	//constructor
	public Groups(Function<Field, Iterable<Field>> adjacency) {
		this.adjacency = adjacency;
	}

	//--interface--
	@Override
	public int size() {
		return size;
	}
	@Override
	public boolean contains(Field node) {
		return this.roots.containsKey(node);
	}
	@Override
	public Field root(Field node) {
		Field nroot = this.roots.get(node);
		if (nroot != node) {
			nroot = this.root(nroot);
			this.roots.put(node, nroot);
		}
		return nroot;
	}
	@Override
	public void remove(Field root) {

		assert this.roots.get(root) == root;

		Set<Field> family = this.families.get(root);
		this.size -= family.size();
		for (Field field: family) {
			ranks.remove(field);
			roots.remove(field);
			families.remove(field);
		}

	}
	@Override
	public Field add(Field node) {

		Field root = node;
		makeSingleton(node);

		for (Field adjacent: adjacency.apply(node)) {
			if (!roots.containsKey(adjacent))
				continue;

			Field aroot = this.root(adjacent);
			if (!aroot.equals(root))
				root = merge(aroot, root);
		}
		return root;

	}
	//--interface--

	// --union--
	// compose, merge and makeSingleton should be considered together with add:
	// invoking these functions separately leaves inconsistent state
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
		return superroot;
	}
	private void makeSingleton(Field node) {

		assert !roots.containsKey(node);

		this.size += 1;
		roots.put(node, node);
		HashSet<Field> family = new HashSet<>();
		family.add(node);
		families.put(node, family);
		ranks.put(node, 0);

	}
	// --union--

}
