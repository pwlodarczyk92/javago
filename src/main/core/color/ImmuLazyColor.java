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
 * common (lazily copied) 'roots', 'fields' and 'adjacents' references - threadsafe.
 */

public class ImmuLazyColor<Field> extends ImmuColor<Field> {

	//--lazy ref count and lock--
	private static class ReferenceWatch {
		private int count = 1;
		private Lock lock = new ReentrantLock();
	}

	private ReferenceWatch watch;
	//--lazy ref count and lock--

	public ImmuLazyColor(Function<Field, Collection<Field>> adjacency) {
		this(adjacency,
				new HashMap<>(),
				new HashMap<>(),
				new HashMap<>(),
				new ReferenceWatch());
	}

	protected ImmuLazyColor(Function<Field, Collection<Field>> adjacency,
						HashMap<Field, Field> roots,
						HashMap<Field, Set<Field>> families,
						HashMap<Field, Set<Field>> liberties,
						ReferenceWatch watch) {
		super(adjacency, roots, families, liberties);
		this.watch = watch;
	}

	private void replicate() {

		ReferenceWatch newWatch = new ReferenceWatch();
		newWatch.lock.lock();
		ReferenceWatch oldWatch = watch;
		watch = newWatch;

		this.roots = new HashMap<>(this.roots);
		this.fields = new HashMap<>(this.fields);
		this.adjacents = new HashMap<>(this.adjacents);

		oldWatch.count -= 1;
		oldWatch.lock.unlock();

	}

	@Override
	public Set<Field> removeGroup(Field root) {

		watch.lock.lock();
		if (watch.count > 1) replicate();
		Set<Field> result = super.removeGroup(root);
		watch.lock.unlock();

		return result;
	}

	@Override
	public Field addStone(Field field) {

		watch.lock.lock();
		if (watch.count > 1) replicate();
		Field result = super.addStone(field);
		watch.lock.unlock();

		return result;
	}

	@Override
	public ImmuLazyColor<Field> fork() {

		watch.lock.lock();
		watch.count += 1;
		ImmuLazyColor<Field> result = new ImmuLazyColor<>(adjacency, roots, fields, adjacents, watch);
		watch.lock.unlock();

		return result;
	}
}
