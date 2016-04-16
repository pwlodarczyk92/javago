package algos.immortal;

import core.color.ColorView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Predicate;

/**
 * Created by maxus on 29.03.16.
 */
public class Immortal {

	public static class Result<F, G> {
		public Set<G> groups;
		public Set<F> points;

		private Result (Set<G> groups, List<Set<F>> points) {
			this.groups = groups;
			this.points = new HashSet<>();
			points.forEach(this.points::addAll);
		}

	}

	private static class KillGroup<F> {
		Set<F> fields;
		Set<F> liberties;
		private KillGroup(Set<F> fields, Set<F> liberties) {
			this.fields = fields;
			this.liberties = liberties;
		}
	}

	final private static Logger logger = LoggerFactory.getLogger(Immortal.class.getName());

	// to kill a group, it must be surrounded by a collection of enemy's stones.
	// they can be grouped by adjacency just like other stones on the board
	// we call these groups kill groups
	// and all fields adjacent to kill group minus fields of the group that is to be killed - liberties.

	// killgrouplibs returns list of killgroups and their liberties for given group that is to be killed

	private static <F, G> List<KillGroup<F>> _getKillGroups(ColorView<F, G> colorView, G group) {

		Set<F> liberties = colorView.getAdjacents(group);
		Set<F> fields = colorView.getFields(group);

		Set<F> leftLiberties = new HashSet<>(liberties);
		List<KillGroup<F>> killGroups = new ArrayList<>();

		while (!leftLiberties.isEmpty()) {

			F seed = leftLiberties.iterator().next();

			Set<F> killGroupFrontier = new HashSet<>();
			Set<F> killGroupFields = new HashSet<>();
			killGroupFrontier.add(seed);
			killGroupFields.add(seed);

			Set<F> killGroupLiberties = new HashSet<>();
			killGroups.add(new KillGroup<>(killGroupFields, killGroupLiberties));

			while(!killGroupFrontier.isEmpty()) { // disjoint-set traversal

				F killField = killGroupFrontier.iterator().next();
				killGroupFrontier.remove(killField);
				leftLiberties.remove(killField);

				for (F killFieldAdjacent : colorView.getAdjacency(killField)) {

					if (killGroupFields.contains(killFieldAdjacent)) // node already traversed
						continue;

					if (!liberties.contains(killFieldAdjacent)) { // node not adjacent to group
						if (!fields.contains(killFieldAdjacent)) // node 2 fields away from group - killgroup liberty
							killGroupLiberties.add(killFieldAdjacent);
						continue;
					}

					killGroupFields.add(killFieldAdjacent);
					killGroupFrontier.add(killFieldAdjacent);
				}
			}

		}

		return killGroups;

	}

	// isimmortal checks if liberties of kill groups are not completely blocked
	// by other groups that are supposed to be immortal.
	// if more than one kill group is blocked this way, there is no way to kill the group

	private static <F, G> List<KillGroup<F>> _deadKillGroups(List<KillGroup<F>> killGroups, ColorView<F, G> colorView, Predicate<G> isNotImmortal) {

		List<KillGroup<F>> result = new ArrayList<>(killGroups);

		for (Iterator<KillGroup<F>> killGroupIterator = result.iterator(); killGroupIterator.hasNext();) {
			KillGroup<F> killGroup = killGroupIterator.next();
			Set<F> liberties = killGroup.liberties;

			for (F liberty: liberties) {
				if (!colorView.contains(liberty)) {
					killGroupIterator.remove();
					break;
				}
				G savingGroup = colorView.getGroup(liberty);
				if (isNotImmortal.test(savingGroup)) {
					killGroupIterator.remove();
					break;
				}
			}
		}

		return result;

	}

	//returns set of immortal groups of given color

	public static <F, G> Result<F, G> get(ColorView<F, G> colorView) {

		Set<G> groups = colorView.getGroups();

		HashMap<G, List<KillGroup<F>>> killGroups = new HashMap<>();
		for(G group: groups)
			killGroups.put(group, _getKillGroups(colorView, group));

		Set<G> uncheckedGroups = new HashSet<>(groups);
		Set<G> probablyImmortalGroups = new HashSet<>();
		List<Set<F>> points = new ArrayList<>();
		Predicate<G> isNotImmortal = group -> !uncheckedGroups.contains(group) && !probablyImmortalGroups.contains(group);

		while (!uncheckedGroups.isEmpty()) {

			G group = uncheckedGroups.iterator().next();
			uncheckedGroups.remove(group);

			List<KillGroup<F>> deadKillGroups = _deadKillGroups(killGroups.get(group), colorView, isNotImmortal);

			if (deadKillGroups.size()>1) {

				probablyImmortalGroups.add(group);
				deadKillGroups.forEach(killGroup -> points.add(killGroup.fields));

			} else {

				uncheckedGroups.addAll(probablyImmortalGroups);
				probablyImmortalGroups.clear();
				points.clear();

			}

		}

		return new Result<>(probablyImmortalGroups, points);

	}
}
