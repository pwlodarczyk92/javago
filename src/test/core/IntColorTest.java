package core;

import com.google.common.collect.Collections2;
import core.table.color.Copyable;
import core.table.color.IColor;
import org.junit.Before;

import java.util.*;
import java.util.function.Function;

/**
 * Created by maxus on 21.02.16.
 */
public abstract class IntColorTest<G, C extends IColor<Integer, G> & Copyable<C>>{

	private static class LinAdj implements Function<Integer, Collection<Integer>> {
		@Override
		public Collection<Integer> apply(Integer integer) {
			return Arrays.asList(integer - 1, integer + 1);
		}
	}

	private Function<Integer, Collection<Integer>> adj = new LinAdj();
	private C instance;

	protected abstract C createInstance(Function<Integer, Collection<Integer>> adjacency);

	@Before
	public final void setUp() {
		this.instance = createInstance(new LinAdj());
	}

	@org.junit.Test
	public final void singletonTest() {

		instance.addstone(3);
		G group = instance.getgroup(3);

		assert instance.allstones().equals(mkset(3));
		assert instance.getnodes(group).equals(mkset(3));
		assert instance.getlibs(group).equals(mkset(2, 4));
	}

	@org.junit.Test
	public final void singleGroupTest() {

		HashSet<Integer> group = mkset(3, 4, 5, 6);

		for(List<Integer> perm: Collections2.permutations(group)) {

			C inst = createInstance(adj);

			for (Integer i: perm) {
				inst.addstone(i);
			}

			assert inst.allstones().equals(group);

			G root1 = inst.getgroup(perm.get(0));
			for (Integer i: perm) {
				assert inst.getgroup(i).equals(root1);
			}

			inst.remgroup(root1);
			assert inst.allstones().isEmpty();

		}
	}

	@org.junit.Test
	public final void twoGroupTest() {

		HashSet<Integer> groups = mkset(3, 4, 7, 6);

		for(List<Integer> perm: Collections2.permutations(groups)) {

			C inst = createInstance(adj);
			for (Integer i: perm) {
				inst.addstone(i);
			}

			assert inst.allstones().equals(groups);

			assert Objects.equals(inst.getgroup(3), inst.getgroup(4));
			assert Objects.equals(inst.getgroup(6), inst.getgroup(7));
			assert !Objects.equals(inst.getgroup(7), inst.getgroup(4));
			assert !Objects.equals(inst.getgroup(7), inst.getgroup(3));
			assert !Objects.equals(inst.getgroup(6), inst.getgroup(4));
			assert !Objects.equals(inst.getgroup(6), inst.getgroup(3));

			inst.remgroup(inst.getgroup(3));
			assert inst.allstones().equals(mkset(6, 7));

		}
	}

	@org.junit.Test
	public final void copyTest() {

		HashSet<Integer> groups = mkset(3, 4, 7, 6);

		for(List<Integer> perm: Collections2.permutations(groups)) {

			C inst = createInstance(adj);
			for (Integer i: perm) {
				inst.addstone(i);
			}

			C instc = inst.copy();

			assert inst.allstones().equals(groups);
			assert instc.allstones().equals(groups);

			assert Objects.equals(inst.getgroup(3), inst.getgroup(4));
			assert Objects.equals(inst.getgroup(6), inst.getgroup(7));
			assert !Objects.equals(inst.getgroup(7), inst.getgroup(4));
			assert !Objects.equals(inst.getgroup(7), inst.getgroup(3));
			assert !Objects.equals(inst.getgroup(6), inst.getgroup(4));
			assert !Objects.equals(inst.getgroup(6), inst.getgroup(3));

			assert Objects.equals(instc.getgroup(3), instc.getgroup(4));
			assert Objects.equals(instc.getgroup(6), instc.getgroup(7));
			assert !Objects.equals(instc.getgroup(7), instc.getgroup(4));
			assert !Objects.equals(instc.getgroup(7), instc.getgroup(3));
			assert !Objects.equals(instc.getgroup(6), instc.getgroup(4));
			assert !Objects.equals(instc.getgroup(6), instc.getgroup(3));

			inst.remgroup(inst.getgroup(3));
			assert inst.allstones().equals(mkset(6, 7));
			assert instc.allstones().equals(groups);

		}
	}

	@SafeVarargs
	private final <F> HashSet<F> mkset(F... elems) {
		return new HashSet<>(Arrays.asList(elems));
	}

}
