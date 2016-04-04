package bot.algos;

import core.board.Game;
import core.color.ColorView;
import core.primitives.MoveNotAllowed;
import core.primitives.Stone;
import core.table.TableView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by maxus on 01.04.16.
 */
public class Safety<F1, G1> {

	private Game<F1, G1> board;
	private F1 field;
	private Stone stone;
	private int max;
	private int maxoverflow;

	final private static int elevation = 14;

	final private static Logger logger = LoggerFactory.getLogger(Safety.class.getName());
	final private static HashMap<Integer, Integer> depths = new HashMap<>();

	public Safety(Game<F1, G1> board, Stone stone, F1 field, int max, int maxcountover) {
		this.board = board;
		this.stone = stone;
		this.field = field;
		this.max = max;
		this.maxoverflow = maxcountover;
	}

	public static <F, G> HashMap<F, Integer> safety(Game<F, G> board, int max, int count, int maxcountover) {

		HashMap<F, Integer> result = new HashMap<>();

		ColorView<F, G> wview = board.getview().gettable().getview(Stone.WHITE);
		ColorView<F, G> bview = board.getview().gettable().getview(Stone.BLACK);
		Set<G> wgs = wview.getgroups();
		Set<G> bgs = bview.getgroups();

		for(G group: bgs) {
			F field = bview.getanynode(group);
			int score = get(new Safety<>(board, Stone.BLACK, field, max, maxcountover), group, count);
			for(F f: bview.getnodes(group))
				result.put(f, score);
		}
		for(G group: wgs) {
			F field = wview.getanynode(group);
			int score = get(new Safety<>(board, Stone.WHITE, field, max, maxcountover), group, count);
			for(F f: wview.getnodes(group))
				result.put(f, score);
		}

		return result;

	}

	// iteration has 3 endpoints - immortality, death and liberties count exceeding maximum
	// and 1 exception - atari, with check limit - maxcountoverflow
	private static <F, G> int get(Safety<F, G> safety, G group, int count) {

		TableView<F, G> table = safety.board.getview().gettable();
		Set<F> libs = table.getlibs(safety.stone, group);

		int result;
		if (safety.board.getview().getcurrentstone() == safety.stone) {
			if (libs.size() > safety.max)
				return libs.size();
			if (libs.size() > 1 && Immortal.immortals(table.getadjacency(), table.getview(safety.stone)).groups.contains(group))
				return Integer.MAX_VALUE;
			result = savemove(safety, group, libs, count);
		}
		else
			result = killmove(safety, group, libs, count);

		Integer sum = 0;
		Integer cum = 0;
		ArrayList<Integer> cts = new ArrayList<>(depths.keySet());
		Collections.sort(cts);
		for (Integer i: cts) sum += depths.get(i);
		for (Integer i: cts) {
			cum += depths.get(i);
			logger.info("depth count: {} : {}", i, cum/(double)sum);
		}
		logger.info("iterations: {}", sum);
		logger.info("");
		depths.clear();
		return result;


	}
	private static <F, G> int killmove(Safety<F, G> safety, G group, Set<F> libs, int count) {

		count -= 1;
		if (!depths.containsKey(count)) depths.put(count, 1);
		else depths.put(count, depths.get(count)+1);

		Integer leastlibs = null;
		Integer libsize = libs.size();
		boolean overflow = count<0;

		if (libsize>1) { //check for endpoint 1
			TableView<F, G> view = safety.board.getview().gettable();
			ColorView<F, G> cview = view.getview(safety.stone);
			if (Immortal.immortals(view.getadjacency(), cview).groups.contains(group)) {
				return Integer.MAX_VALUE;
			}
		}

		if (libsize > safety.max) //check for endpoint 3
			return libsize;

		if (overflow && libsize>2) // check for exception
			return libsize;

		if (count < - safety.maxoverflow) // check for exception limit
			return libsize;

		for(F lib: libs) {

			int result;

			try {
				Set<F> killed = safety.board.put(lib);
				TableView<F, G> view = safety.board.getview().gettable();

				if (killed.contains(safety.field)) { // check for endpoint 2
					safety.board.undo();
					return 0;
				}

				Set<F> newlibs = view.getlibs(safety.stone, group);
				result = savemove(safety, group, newlibs, count);

				safety.board.undo();
			} catch (MoveNotAllowed e) {continue;}

			if(leastlibs == null || result < leastlibs)
				leastlibs = result;
			if (leastlibs == 0) // check for endpoint 2
				return 0;

		}

		if(leastlibs == null) {
			try {
				//TODO: choose other fields?
				safety.board.put(null);
				leastlibs = savemove(safety, group, libs, count);
				safety.board.undo();
			} catch (MoveNotAllowed e) {return libsize;} // is not immortal, but can do nothing with it right now
		}

		return leastlibs;

	}

	private static <F, G> int savemove(Safety<F, G> safety, G group, Set<F> libs, int count) {

		count -= 1;
		if (!depths.containsKey(count)) depths.put(count, 1);
		else depths.put(count, depths.get(count)+1);

		Integer mostlib = null;
		Integer libsize = libs.size();
		boolean overflow = count<0;

		if (overflow && libsize>1) // check for exception
			return libs.size();

		if (count < -safety.maxoverflow) // check for exception limit
			return libs.size();

		Set<F> worthwhile_fields = new HashSet<>(libs);

		TableView<F, G> tableview = safety.board.getview().gettable();
		ColorView<F, G> enemyview = tableview.getview(safety.stone.opposite());

		for (F adjacent: tableview.getview(safety.stone).getadjacent(group)) { //find attackers that can be killed instantly
			if (libs.contains(adjacent))
				continue;

			G attackgroup = enemyview.getgroup(adjacent);
			Set<F> attacklibs = tableview.getlibs(safety.stone.opposite(), attackgroup);
			Integer attacklibssize = attacklibs.size();

			if (attacklibssize == 1)
				worthwhile_fields.addAll(attacklibs);
			else if (attacklibssize == 2 && 2 <= libsize && !overflow)
				worthwhile_fields.addAll(attacklibs);

		}

		for(F wfield: worthwhile_fields) {

			int result;

			try {
				safety.board.put(wfield);
				TableView<F, G> view = safety.board.getview().gettable();
				ColorView<F, G> cview = view.getview(safety.stone);
				group = cview.getgroup(safety.field);
				libs = view.getlibs(safety.stone, group);

				result = killmove(safety, group, libs, count);

				safety.board.undo();
			} catch (MoveNotAllowed e) {continue;}

			if(mostlib == null || result > mostlib)
				mostlib = result;
			if(mostlib > safety.max) { // checkpoint 1 and 3
				return mostlib;
			}

		}

		if (mostlib == null) {
			//TODO: choose other fields
			try {
				safety.board.put(null);
				mostlib = killmove(safety, group, libs, count);
				safety.board.undo();
			} catch (MoveNotAllowed e) { return libsize; } // cannot do anything and is not immortal - what should be returned?
		}

		return mostlib;

	}

}
