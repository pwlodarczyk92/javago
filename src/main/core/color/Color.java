package core.color;

import java.util.*;
import java.util.function.Function;

/**
 * Created by maxus on 20.02.16.
 */

public class Color<Field> implements IColor<Field, Field>{

	//--internal structure--
	protected HashMap<Field, Field> roots;
	protected HashMap<Field, Set<Field>> fields;
	protected HashMap<Field, Set<Field>> adjacents;
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
					HashMap<Field, Set<Field>> fields,
					HashMap<Field, Set<Field>> adjacents) {
		this.adjacency = adjacency;
		this.roots = roots;
		this.fields = fields;
		this.adjacents = adjacents;
	}

	//--accessors--
	@Override
	public Field getGroup(Field field) {
		return roots.get(field);
	}
	@Override
	public Collection<Field> getAdjacency(Field field) { return adjacency.apply(field); }

	@Override
	public Set<Field> getFields(Field root) {
		return fields.get(root);
	}
	@Override
	public Set<Field> getAdjacents(Field root) {
		return adjacents.get(root);
	}
	@Override
	public Field getAnyField(Field group) {
		return group;
	}

	@Override
	public Set<Field> getFields() {
		return roots.keySet();
	}
	@Override
	public Set<Field> getGroups() {
		return fields.keySet();
	}

	@Override
	public boolean contains(Field field) {
		return roots.containsKey(field);
	}

	//--accessors--

	//--modifiers--
	@Override
	public Set<Field> removeGroup(Field root) {

		assert roots.get(root) == root;

		Set<Field> newFields = this.fields.remove(root);
		newFields.stream().forEach(roots::remove);
		adjacents.remove(root);

		return newFields;

	}

	@Override
	public Field addStone(Field field) {

		assert !roots.containsKey(field);

		Field maxRoot = null;
		int maxGroupSize = 0;
		HashSet<Field> traversedRoots = new HashSet<>();

		// for every group adjacent to a new stone
		// find biggest one and its root
		// save all traversed roots to <traversedRoots>

		for (Field adjacent: adjacency.apply(field)) {
			if (!this.contains(adjacent))
				continue;

			Field adjacentsRoot = this.roots.get(adjacent);
			if (traversedRoots.contains(adjacentsRoot))
				continue;
			traversedRoots.add(adjacentsRoot);

			int adjacentGroupSize = fields.get(adjacentsRoot).size();
			if (maxGroupSize < adjacentGroupSize) {
				maxRoot = adjacentsRoot;
				maxGroupSize = adjacentGroupSize;
			}
		}

		// no neighbors - make and return simple singleton

		if (maxRoot == null) {
			HashSet<Field> newFields = new HashSet<>();
			newFields.add(field);
			fields.put(field, newFields);
			adjacents.put(field, new HashSet<>(adjacency.apply(field)));
			roots.put(field, field);
			return field;
		}

		// else - make one big group from neighbors and new field;
		// merge fields and adjacents sets by adding
		// fields and adjacents of smaller groups to maxRoot's ones

		traversedRoots.remove(maxRoot);
		Set<Field> maxFields = fields.get(maxRoot);
		Set<Field> maxAdjacents = adjacents.get(maxRoot);

		for (Field subRoot: traversedRoots) {

			maxAdjacents.addAll(adjacents.remove(subRoot));
			Set<Field> subFields = fields.remove(subRoot);
			maxFields.addAll(subFields);

			for (Field n: subFields)
				roots.put(n, maxRoot);

		}

		// include field (the one from args) related changes in the sets

		for (Field adjacent: adjacency.apply(field)) {
			if (!this.contains(adjacent))
				maxAdjacents.add(adjacent);
		}

		maxAdjacents.remove(field);
		maxFields.add(field);
		roots.put(field, maxRoot);

		return maxRoot;

	}
	// --modifiers--

	// --identity, equality--
	@Override
	public Color<Field> fork() {

		HashMap<Field, Set<Field>> newFields = new HashMap<>(fields.size());
		for (Map.Entry<Field, Set<Field>> f: fields.entrySet())
			newFields.put(f.getKey(), new HashSet<>(f.getValue()));

		HashMap<Field, Set<Field>> newAdjacents = new HashMap<>(adjacents.size());
		for (Map.Entry<Field, Set<Field>> l: adjacents.entrySet())
			newAdjacents.put(l.getKey(), new HashSet<>(l.getValue()));

		return new Color<Field>(adjacency,
				new HashMap<>(roots),
				newFields,
				newAdjacents) {
		};
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof IColor)) return false;

		IColor color = (IColor) o;

		return color.getFields().equals(getFields());
	}

	@Override
	public int hashCode() {
		return roots.keySet().hashCode();
	}
	// --identity, equality--

}
