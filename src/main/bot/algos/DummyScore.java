package bot.algos;

import core.board.Board;
import core.primitives.MoveNotAllowed;
import core.primitives.Stone;

import java.util.HashMap;

/**
 * Created by maxus on 30.03.16.
 */
public class DummyScore {

	private static final Double covconst = 0.3;

	public static <F> HashMap<F, Double> get(Board<F, ?> board) {

		Double maxabs = 0.0;
		Double basescore = score(board);

		HashMap<F, Double> result = new HashMap<>();

		for (F field: board.tableview().getfields()) {
			try {
				Double val = trymove(board, field) - basescore;
				maxabs = Math.max(maxabs, Math.abs(val));
				result.put(field, val);
			} catch (MoveNotAllowed e) { continue; }
		}

		if(maxabs != 0) {
			for (F field : result.keySet()) {
				result.put(field, result.get(field) / maxabs);
			}
		}

		return result;

	}

	private static <F> Double trymove(Board<F, ?> board, F field) {

		board.put(field);
		Double result = score(board);
		board.undo();
		return result;

	}

	private static <F> Double score(Board<F, ?> board) {

		Integer rounds = 3;
		Double zonefactor = rounds <= 0 ? 1.0 : 1.0/(rounds*(rounds+1)*2);

		HashMap<F, Double> control = Control.get(board.tableview(), rounds);
		Double contrresult = 0.0;
		for(Double i: control.values())
			contrresult += i;
		contrresult *= zonefactor;

		Integer pointresult = board.points(Stone.WHITE) - board.points(Stone.BLACK);

		return contrresult*covconst + pointresult;

	}
}
