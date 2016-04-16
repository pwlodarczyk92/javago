package algos.tree;

import core.Stone;
import core.board.IState;
import core.color.ColorView;
import core.table.TableView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by maxus on 10.04.16.
 */

public class TreeImpl {

	final static protected Logger logger = LoggerFactory.getLogger(TreeImpl.class.getName());

	public static double pow(int val) {
		switch (val) {
			case 0: return -1.0;
			case 1: return -1/2.0;
			case 2: return -1/4.0;
			case 3: return -1/8.0;
			default: return 0;
		}
	}

	public static double expfunc(double libs) {
		if (libs <= 0.0) return -1.0;
		if (libs >= 4.0) return 0.0;

		int low = (int) Math.floor(libs);
		int high = (int) Math.ceil(libs);
		if (low == high)
			return pow(low);
		if (high-low != 1) throw new RuntimeException();

		double lowcoef = high-libs;
		double highcoef = libs-low;
		return pow(low) * lowcoef + pow(high) * highcoef;
	}

	public static <F, G> double scorefunc(IState<F, G> state) {

		double result = 0;

		TableView<F, G> view = state.gettable();
		ColorView<F, G> wview = view.getview(Stone.WHITE);
		ColorView<F, G> bview = view.getview(Stone.BLACK);

		for (G group: wview.getallgroups()) {
			int libsize = view.getlibs(Stone.WHITE, group).size();
			result += wview.getstones(group).size() * expfunc(libsize);
		}
		for (G group: bview.getallgroups()) {
			int libsize = view.getlibs(Stone.BLACK, group).size();
			result -= bview.getstones(group).size() * expfunc(libsize);
		}
		return result + (state.getwhitepoints() - state.getblackpoints());

	}

}
