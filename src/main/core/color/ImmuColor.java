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
	public Field addStone(Field field) {

		assert !roots.containsKey(field);

		Field maxRoot = null;
		int maxGroupSize = 0;
		HashSet<Field> traversedRoots = new HashSet<>();

		// for every group adjacent to a new stone
		// find biggest one and its root
		// save all traversed roots to 'traversedRoots'

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
			Set<Field> newFields = ImmutableSet.of(field);
			fields.put(field, newFields);
			adjacents.put(field, ImmutableSet.copyOf(adjacency.apply(field)));
			roots.put(field, field);
			return field;
		}

		// else - make one big group from neighbors and new field;
		// create new sets of fields and adjacents that represent
		// newly created group; set new root to biggest neighbor group's root

		ImmutableSet.Builder<Field> fieldsBuilder = ImmutableSet.builder();
		ImmutableSet.Builder<Field> adjacentsBuilder = ImmutableSet.builder();

		// add field (the one from args) and its adjacents to new sets

		fieldsBuilder.add(field);

		for (Field adjacent: adjacency.apply(field))
			if (!this.contains(adjacent))
				adjacentsBuilder.add(adjacent);

		// add surrounding groups' nodes and adjacents to new sets, set new roots

		for (Field subRoot: traversedRoots) {
			Set<Field> subFields = fields.remove(subRoot);
			fieldsBuilder.addAll(subFields);
			if (subRoot != maxRoot)
				for (Field subField: subFields)
					roots.put(subField, maxRoot);

			Set<Field> subAdjacents = adjacents.remove(subRoot);
			for (Field subAdjacent: subAdjacents)
				if (!subAdjacent.equals(field))
					adjacentsBuilder.add(subAdjacent);

		}

		// build the sets, modify object fields to represent the changes

		Set<Field> newFields = fieldsBuilder.build();
		Set<Field> newAdjacents = adjacentsBuilder.build();

		roots.put(field, maxRoot);
		fields.put(maxRoot, newFields);
		adjacents.put(maxRoot, newAdjacents);

		return maxRoot;

	}
	// --modifiers--

	@Override
	public ImmuColor<Field> fork() {
		return new ImmuColor<Field>(adjacency,
				new HashMap<>(roots),
				new HashMap<>(fields),
				new HashMap<>(adjacents)) {
		};
	}

}
