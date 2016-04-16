package core.color;

import com.google.common.collect.Collections2;
import core.color.IColor;
import org.junit.Before;

import java.util.*;
import java.util.function.Function;

/**
 * Created by maxus on 21.02.16.
 */
public abstract class IntColorTest<G>{

	private static class LinAdj implements Function<Integer, Collection<Integer>> {
		@Override
		public Collection<Integer> apply(Integer integer) {
			return Arrays.asList(integer - 1, integer + 1);
		}
	}

	private Function<Integer, Collection<Integer>> adj = new LinAdj();
	private IColor<Integer, G> instance;

	protected abstract IColor<Integer, G> createInstance(Function<Integer, Collection<Integer>> adjacency);

	@Before
	public final void setUp() {
		this.instance = createInstance(new LinAdj());
	}

	@org.junit.Test
	public final void singletonTest() {

		instance.addstone(3);
		G group = instance.getgroup(3);

		assert instance.getallstones().equals(mkset(3));
		assert instance.getstones(group).equals(mkset(3));
		assert instance.getadjacent(group).equals(mkset(2, 4));
	}

	@org.junit.Test
	public final void singleGroupTest() {

		HashSet<Integer> group = mkset(3, 4, 5, 6);

		for(List<Integer> perm: Collections2.permutations(group)) {

			IColor<Integer, G> inst = createInstance(adj);

			for (Integer i: perm) {
				inst.addstone(i);
			}

			assert inst.getallstones().equals(group);

			G root1 = inst.getgroup(perm.get(0));
			for (Integer i: perm) {
				assert inst.getgroup(i).equals(root1);
			}

			inst.remgroup(root1);
			assert inst.getallstones().isEmpty();

		}
	}

	@org.junit.Test
	public final void twoGroupTest() {

		HashSet<Integer> groups = mkset(3, 4, 7, 6);

		for(List<Integer> perm: Collections2.permutations(groups)) {

			IColor<Integer, G> inst = createInstance(adj);
			for (Integer i: perm) {
				inst.addstone(i);
			}

			assert inst.getallstones().equals(groups);

			assert Objects.equals(inst.getgroup(3), inst.getgroup(4));
			assert Objects.equals(inst.getgroup(6), inst.getgroup(7));
			assert !Objects.equals(inst.getgroup(7), inst.getgroup(4));
			assert !Objects.equals(inst.getgroup(7), inst.getgroup(3));
			assert !Objects.equals(inst.getgroup(6), inst.getgroup(4));
			assert !Objects.equals(inst.getgroup(6), inst.getgroup(3));

			inst.remgroup(inst.getgroup(3));
			assert inst.getallstones().equals(mkset(6, 7));

		}
	}

	@org.junit.Test
	public final void copyTest() {

		HashSet<Integer> groups = mkset(3, 4, 7, 6);

		for(List<Integer> perm: Collections2.permutations(groups)) {

			IColor<Integer, G> inst = createInstance(adj);
			for (Integer i: perm) {
				inst.addstone(i);
			}

			IColor<Integer, G> instc = inst.fork();

			assert inst.getallstones().equals(groups);
			assert instc.getallstones().equals(groups);

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
			assert inst.getallstones().equals(mkset(6, 7));
			assert instc.getallstones().equals(groups);

		}
	}

	@SafeVarargs
	private final <F> HashSet<F> mkset(F... elems) {
		return new HashSet<>(Arrays.asList(elems));
	}

}
