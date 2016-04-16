package core.color;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
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

public class ImmuLazyColor<Field> extends ImmuColor<Field> {

	//--lazy ref count and lock--
	private static class Refwatch {
		private int count = 1;
		private Lock copylock = new ReentrantLock();
	}

	private Refwatch refwatch;
	//--lazy ref count and lock--

	public ImmuLazyColor(Function<Field, Collection<Field>> adjacency) {
		this(adjacency,
				new HashMap<>(),
				new HashMap<>(),
				new HashMap<>(),
				new Refwatch());
	}

	protected ImmuLazyColor(Function<Field, Collection<Field>> adjacency,
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
		this.families = new HashMap<>(this.families);
		this.liberties = new HashMap<>(this.liberties);

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
	public ImmuLazyColor<Field> fork() {

		refwatch.copylock.lock();
		refwatch.count += 1;
		ImmuLazyColor<Field> result = new ImmuLazyColor<>(adjacency, roots, families, liberties, refwatch);
		refwatch.copylock.unlock();

		return result;
	}
}
