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
public class Safety<F, G> {

	private Game<F, G> board;
	private F field;
	private Stone stone;
	private int maxlibs;
	private int maxoverflow;

	final private static Logger logger = LoggerFactory.getLogger(Safety.class.getName());

	public Safety(Game<F, G> board, Stone stone, F field, int maxlibs, int maxoverflow) {
		this.board = board;
		this.stone = stone;
		this.field = field;
		this.maxlibs = maxlibs;
		this.maxoverflow = maxoverflow;
	}

	public static <F, G> HashMap<F, Integer> safety(Game<F, G> board, int maxlibs, int itercount, int maxoverflow) {

		HashMap<F, Integer> result = new HashMap<>();

		ColorView<F, G> wview = board.getview().gettable().getview(Stone.WHITE);
		ColorView<F, G> bview = board.getview().gettable().getview(Stone.BLACK);
		Set<G> wgs = wview.getgroups();
		Set<G> bgs = bview.getgroups();

		for(G group: bgs) {
			F field = bview.getanynode(group);
			int score = get(new Safety<>(board, Stone.BLACK, field, maxlibs, maxoverflow), group, itercount);
			for(F f: bview.getnodes(group))
				result.put(f, score);
		}
		for(G group: wgs) {
			F field = wview.getanynode(group);
			int score = get(new Safety<>(board, Stone.WHITE, field, maxlibs, maxoverflow), group, itercount);
			for(F f: wview.getnodes(group))
				result.put(f, score);
		}

		return result;

	}

	// iteration has 3 endpoints - immortality, death and liberties count exceeding maximum
	// and 1 exception - atari, with check limit - maxcountoverflow
	private static <F, G> int get(Safety<F, G> safety, G group, int itercount) {

		TableView<F, G> table = safety.board.getview().gettable();
		Set<F> libs = table.getlibs(safety.stone, group);

		int result;
		if (safety.board.getview().getcurrentstone() == safety.stone) {
			if (libs.size() > safety.maxlibs)
				return libs.size();
			if (libs.size() > 1 && Immortal.immortals(table.getadjacency(), table.getview(safety.stone)).groups.contains(group))
				return Integer.MAX_VALUE;
			result = savemove(safety, libs, itercount);
		}
		else
			result = killmove(safety, libs, itercount);

		return result;


	}
	private static <F, G> int killmove(Safety<F, G> safety, Set<F> libs, int itercount) {

		itercount -= 1;
		int libsize = libs.size();
		int leastlibs = -1;
		boolean overflow = itercount<0;

		if (libsize>1) { //check for endpoint 1
			TableView<F, G> view = safety.board.getview().gettable();
			ColorView<F, G> cview = view.getview(safety.stone);
			G group = cview.getgroup(safety.field);
			if (Immortal.immortals(view.getadjacency(), cview).groups.contains(group)) {
				return Integer.MAX_VALUE;
			}
		}

		if (libsize > safety.maxlibs) //check for endpoint 3
			return libsize;

		if (overflow && libsize>2) // check for exception
			return libsize;

		if (itercount < - safety.maxoverflow) // check for exception limit
			return libsize;



		for(F lib: libs) {

			int result;

			Set<F> killed = safety.board.put(lib);
			if (killed == null) continue;

			if (killed.contains(safety.field)) { // check for endpoint 2
				safety.board.undo();
				return 0;
			}

			Set<F> newlibs = new HashSet<>(libs);
			newlibs.remove(lib);
			result = savemove(safety, newlibs, itercount);

			safety.board.undo();

			if(leastlibs == -1 || result < leastlibs)
				leastlibs = result;
			if (leastlibs == 0) // check for endpoint 2
				return 0;

		}

		if(leastlibs == -1) {
			//TODO: choose other fields?
			Set<F> killed = safety.board.put(null);
			if (killed == null) return libsize;
			leastlibs = savemove(safety, libs, itercount);
			safety.board.undo();
		}

		return leastlibs;

	}

	private static <F, G> int savemove(Safety<F, G> safety, Set<F> libs, int itercount) {

		itercount -= 1;
		int mostlib = -1;
		int libsize = libs.size();
		boolean overflow = itercount<0;

		if (overflow && libsize>1) // check for exception
			return libs.size();

		if (itercount < -safety.maxoverflow) // check for exception limit
			return libs.size();

		Set<F> worthwhile_fields = new HashSet<>(libs);

		TableView<F, G> tableview = safety.board.getview().gettable();
		ColorView<F, G> enemyview = tableview.getview(safety.stone.opposite());
		ColorView<F, G> playerview = tableview.getview(safety.stone);
		G group = playerview.getgroup(safety.field);

		for (F adjacent: playerview.getadjacent(group)) { //find attackers that can be killed instantly
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
				Set<F> killed = safety.board.put(wfield);
				if (killed == null) continue;

				TableView<F, G> newview = safety.board.getview().gettable();
				ColorView<F, G> newcview = newview.getview(safety.stone);
				group = newcview.getgroup(safety.field);
				Set<F> newlibs = newview.getlibs(safety.stone, group);

				result = killmove(safety, newlibs, itercount);

				safety.board.undo();
			} catch (MoveNotAllowed e) {continue;}

			if(mostlib == -1 || result > mostlib)
				mostlib = result;
			if(mostlib > safety.maxlibs) { // checkpoint 1 and 3
				return mostlib;
			}

		}

		if (mostlib == -1) {
			//TODO: choose other fields
			Set<F> killed = safety.board.put(null);
			if (killed == null) return libsize; // cannot do anything and is not immortal - what should be returned?
			mostlib = killmove(safety, libs, itercount);
			safety.board.undo();
		}

		return mostlib;

	}

}
