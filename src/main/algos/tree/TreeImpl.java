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

	private static double discreteLibScore(int val) {
		switch (val) {
			case 0: return -1.0;
			case 1: return -1/2.0;
			case 2: return -1/4.0;
			case 3: return -1/8.0;
			default: return 0;
		}
	}

	public static double libertyScore(double libs) {
		if (libs <= 0.0) return -1.0;
		if (libs >= 4.0) return 0.0;

		int low = (int) Math.floor(libs);
		int high = (int) Math.ceil(libs);
		if (low == high)
			return discreteLibScore(low);
		if (high-low != 1) throw new RuntimeException();

		double lowCoef = high-libs;
		double highCoef = libs-low;
		return discreteLibScore(low) * lowCoef + discreteLibScore(high) * highCoef;
	}

	public static <F, G> double scoreFunction(IState<F, G> state) {

		double result = 0;

		TableView<F, G> view = state.getTable();
		ColorView<F, G> wview = view.getView(Stone.WHITE);
		ColorView<F, G> bview = view.getView(Stone.BLACK);

		for (G group: wview.getGroups()) {
			int libsize = view.getLiberties(Stone.WHITE, group).size();
			result += wview.getFields(group).size() * libertyScore(libsize);
		}
		for (G group: bview.getGroups()) {
			int libsize = view.getLiberties(Stone.BLACK, group).size();
			result -= bview.getFields(group).size() * libertyScore(libsize);
		}
		return result + (state.getWhitePoints() - state.getBlackPoints());

	}

}
