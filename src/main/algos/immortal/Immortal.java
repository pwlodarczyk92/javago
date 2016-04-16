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
		Set<F> nodes;
		Set<F> libs;
		private KillGroup(Set<F> nodes, Set<F> libs) {
			this.nodes = nodes;
			this.libs = libs;
		}
	}

	final private static Logger logger = LoggerFactory.getLogger(Immortal.class.getName());

	// to kill a group, it must be surrounded by a collection of enemy's stones.
	// they can be grouped by adjacency just like other stones on the board
	// we call these groups kill groups
	// and all fields adjacent to kill group minus fields of the group that is to be killed - liberties.

	// killgrouplibs returns list of killgroups and their liberties for given group that is to be killed

	private static <F, G> List<KillGroup<F>> killgrouplibs(ColorView<F, G> colorview, G group) {

		Set<F> grouplibs = colorview.getadjacent(group);
		Set<F> groupnodes = colorview.getstones(group);

		Set<F> leftlibs = new HashSet<>(grouplibs);
		List<KillGroup<F>> kglibs = new ArrayList<>();

		while (!leftlibs.isEmpty()) {

			F seed = leftlibs.iterator().next();

			Set<F> killfront = new HashSet<>();
			Set<F> killgroup = new HashSet<>();
			killfront.add(seed);
			killgroup.add(seed);

			Set<F> killlibs = new HashSet<>();
			kglibs.add(new KillGroup<>(killgroup, killlibs));

			while(!killfront.isEmpty()) { // disjoint-set traversal

				F killfield = killfront.iterator().next();
				killfront.remove(killfield);
				leftlibs.remove(killfield);

				for (F killadj : colorview.adjacency(killfield)) {

					if (killgroup.contains(killadj)) // node already traversed
						continue;

					if (!grouplibs.contains(killadj)) { // node not adjacent to group
						if (!groupnodes.contains(killadj)) // node 2 fields away from group - killgroup liberty
							killlibs.add(killadj);
						continue;
					}

					killgroup.add(killadj);
					killfront.add(killadj);
				}
			}

		}

		return kglibs;

	}

	// isimmortal checks if liberties of kill groups are not completely blocked
	// by other groups that are supposed to be immortal.
	// if more than one kill group is blocked this way, there is no way to kill the group

	private static <F, G> List<KillGroup<F>> deadkillgrouplibs(List<KillGroup<F>> killgrouplibs, ColorView<F, G> colorview, Predicate<G> notimmo) {

		List<KillGroup<F>> dkgs = new ArrayList<>(killgrouplibs);

		for (Iterator<KillGroup<F>> killgroupiter = dkgs.iterator(); killgroupiter.hasNext();) {
			KillGroup<F> kg = killgroupiter.next();
			Set<F> libs = kg.libs;

			for (F lib: libs) {
				if (!colorview.contains(lib)) {
					killgroupiter.remove();
					break;
				}
				G savinggroup = colorview.getgroup(lib);
				if (notimmo.test(savinggroup)) {
					killgroupiter.remove();
					break;
				}
			}
		}

		return dkgs;

	}

	//returns set of immortal groups of given color

	public static <F, G> Result<F, G> get(ColorView<F, G> colorview) {

		Set<G> groups = colorview.getallgroups();

		HashMap<G, List<KillGroup<F>>> kglibs = new HashMap<>();
		for(G group: groups)
			kglibs.put(group, killgrouplibs(colorview, group));

		Set<G> unchecked = new HashSet<>(groups);
		Set<G> probablyimmo = new HashSet<>();
		List<Set<F>> points = new ArrayList<>();
		Predicate<G> notimmo = group -> !unchecked.contains(group) && !probablyimmo.contains(group);

		while (!unchecked.isEmpty()) {

			G group = unchecked.iterator().next();
			unchecked.remove(group);

			List<KillGroup<F>> deadkgs = deadkillgrouplibs(kglibs.get(group), colorview, notimmo);

			if (deadkgs.size()>1) {

				probablyimmo.add(group);
				deadkgs.forEach(kg -> points.add(kg.nodes));

			} else {

				unchecked.addAll(probablyimmo);
				probablyimmo.clear();
				points.clear();

			}

		}

		return new Result<>(probablyimmo, points);

	}
}