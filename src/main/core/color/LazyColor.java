package core.color;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;

/**
 * Created by maxus on 14.04.16.
 */

/**
 * Concurrent modification can cause inconsistent results
 * just like with regular Color instances.
 * Locks are used to make modification of different LazyColor instances containing
 * common (lazily copied) 'roots', 'families' and 'liberties' references - threadsafe.
 */

public class LazyColor<Field> extends Color<Field>{

	//--lazy ref count and lock--
	private static class Refwatch {
		private int count = 1;
		private Lock copylock = new ReentrantLock();
	}

	private Refwatch refwatch;
	//--lazy ref count and lock--

	public LazyColor(Function<Field, Collection<Field>> adjacency) {
		this(adjacency,
				new HashMap<>(),
				new HashMap<>(),
				new HashMap<>(),
				new Refwatch());
	}

	protected LazyColor(Function<Field, Collection<Field>> adjacency,
						HashMap<Field, Field> roots,
						HashMap<Field, Set<Field>> families,
						HashMap<Field, Set<Field>> liberties,
						Refwatch refwatch) {
		super(adjacency, roots, families, liberties);
		this.refwatch = refwatch;
	}

	private void substitute() {

		Refwatch newwatch = new Refwatch();
		newwatch.copylock.lock();
		Refwatch oldwatch = refwatch;
		refwatch = newwatch;

		this.roots = new HashMap<>(this.roots);
		HashMap<Field, Set<Field>> nf = new HashMap<>(families.size());
		for (Map.Entry<Field, Set<Field>> f : families.entrySet())
			nf.put(f.getKey(), new HashSet<>(f.getValue()));
		this.families = nf;
		HashMap<Field, Set<Field>> nl = new HashMap<>(liberties.size());
		for (Map.Entry<Field, Set<Field>> l : liberties.entrySet())
			nl.put(l.getKey(), new HashSet<>(l.getValue()));
		this.liberties = nl;

		oldwatch.count -= 1;
		oldwatch.copylock.unlock();

	}

	@Override
	public Set<Field> remgroup(Field root) {

		refwatch.copylock.lock();
		if (refwatch.count > 1) substitute();
		Set<Field> result = super.remgroup(root);
		refwatch.copylock.unlock();

		return result;
	}

	@Override
	public Field addstone(Field node) {

		refwatch.copylock.lock();
		if (refwatch.count > 1) substitute();
		Field result = super.addstone(node);
		refwatch.copylock.unlock();

		return result;
	}

	@Override
	public LazyColor<Field> fork() {

		refwatch.copylock.lock();
		refwatch.count += 1;
		LazyColor<Field> result = new LazyColor<>(adjacency, roots, families, liberties, refwatch);
		refwatch.copylock.unlock();

		return result;
	}

}
