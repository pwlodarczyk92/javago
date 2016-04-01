package bot.algos;

import core.color.ColorView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Function;
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

	// to kill a group, it must be surrounded by collection of enemy's stones.
	// they can be grouped by adjacency just like other stones on the board
	// we call these groups kill groups
	// and liberties - all stones adjacent to kill group minus group that is to be killed.

	// killgrouplibs returns list of liberties for each kill group for given group to be killed

	private static <F, G> List<KillGroup<F>> killgrouplibs(Function<F, Collection<F>> adjacency, ColorView<F, G> colorview, G group) {

		Set<F> grouplibs = colorview.getlibs(group);
		Set<F> groupnodes = colorview.getnodes(group);

		Set<F> leftlibs = new HashSet<>(grouplibs);
		List<KillGroup<F>> kglibs = new ArrayList<>();

		logger.trace("search for kill groups liberties:");
		logger.trace("group name: no.{}", group.toString());
		logger.trace("group size: {}", groupnodes.size());

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

				for (F killadj : adjacency.apply(killfield)) {

					if (killgroup.contains(killadj)) // node already traversed
						continue;

					if (!grouplibs.contains(killadj)) { // node not adjacent to group
						if (!groupnodes.contains(killadj)) // node 2 fields away from group
							killlibs.add(killadj);
						continue;
					}

					killgroup.add(killadj);
					killfront.add(killadj);
				}
			}

			logger.trace("killgroup size: {}, libs: {}", killgroup.size(), killlibs.size());
		}

		return kglibs;

	}

	// isimmortal checks if liberties of kill groups are not completely blocked
	// by other groups that are supposed to be immortal.
	// if more than one kill group is blocked this way, there is no way to kill the group

	private static <F, G> List<KillGroup<F>> deadkillgrouplibs(List<KillGroup<F>> killgrouplibs, ColorView<F, G> colorview, Predicate<G> notimmo) {

		List<KillGroup<F>> dkgs = new ArrayList<>(killgrouplibs);
		logger.trace("grups number: {}", dkgs.size());

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

		logger.trace("dead kill groups: {}", dkgs.size());
		return dkgs;

	}

	//returns set of immortal groups of given color

	public static <F, G> Result<F, G> immortals(Function<F, Collection<F>> adjacency, ColorView<F, G> colorview) {

		Set<G> groups = colorview.getgroups();

		HashMap<G, List<KillGroup<F>>> kglibs = new HashMap<>();
		for(G group: groups)
			kglibs.put(group, killgrouplibs(adjacency, colorview, group));

		Set<G> unchecked = new HashSet<>(groups);
		Set<G> probablyimmo = new HashSet<>();
		List<Set<F>> points = new ArrayList<>();
		Predicate<G> notimmo = group -> !unchecked.contains(group) && !probablyimmo.contains(group);


		logger.trace("immortality check for groups:");
		logger.trace("{}", unchecked.toString());

		while (!unchecked.isEmpty()) {

			G group = unchecked.iterator().next();
			unchecked.remove(group);

			logger.trace("group check: no.{}", group.toString());
			List<KillGroup<F>> deadkgs = deadkillgrouplibs(kglibs.get(group), colorview, notimmo);

			if (deadkgs.size()>1) {
				logger.trace("group immortal: no.{}", group.toString());
				probablyimmo.add(group);
				deadkgs.forEach(kg -> points.add(kg.nodes));
				logger.trace("points taken: {}", points.size());

			} else {

				unchecked.addAll(probablyimmo);
				logger.trace("group killed: no.{}", group.toString());
				logger.trace("starting new immortality check for groups");
				logger.trace("{}", unchecked.toString());
				probablyimmo.clear();
				points.clear();

			}

		}


		logger.debug("finally points taken: {}", points.size());
		return new Result<>(probablyimmo, points);

	}
}
