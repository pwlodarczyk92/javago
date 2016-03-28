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

	//--interface--
	@Override
	public Field getgroup(Field node) {
		return roots.get(node);
	}
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
	public Set<Field> getgroups() {
		return families.keySet();
	}
	@Override
	public boolean contains(Field node) {
		return roots.containsKey(node);
	}

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

		Field maxroot = null;
		int maxgroupsize = 0;
		HashSet<Field> checkedroots = new HashSet<>();

		// for every group adjacent to a new stone
		// find biggest one and its root
		// save all these roots to checkedroots

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

		for (Field adjroot: checkedroots) {

			maxliberties.addAll(liberties.get(adjroot));

			HashSet<Field> nowfam = families.get(adjroot);
			maxfamily.addAll(nowfam);

			for (Field n: nowfam)
				roots.put(n, maxroot);

		}

		// include new node in merged groups
		// include adjacent empty nodes in liberties
		// exclude new node from liberties

		for (Field adjacent: adjacency.apply(node)) {
			if (!this.contains(adjacent))
				maxliberties.add(adjacent);
		}
		maxliberties.remove(node);
		maxfamily.add(node);
		roots.put(node, maxroot);

		return node;

	}

}
