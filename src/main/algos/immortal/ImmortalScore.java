package algos.immortal;

import core.Stone;
import core.board.IGame;
import core.color.ColorView;

import java.util.HashMap;

/**
 * Created by maxus on 11.04.16.
 */
public class ImmortalScore {

	public static <F> HashMap<F, Double> score(IGame<F, ?> board) {
		return _score(board);
	}

	private static <F, G> HashMap<F, Double> _score(IGame<F, G> board) {
		ColorView<F, G> whites =  board.getState().getTable().getView(Stone.WHITE);
		ColorView<F, G> blacks =  board.getState().getTable().getView(Stone.BLACK);
		Immortal.Result<F, G> whitesResult = Immortal.get(whites);
		Immortal.Result<F, G> blacksResult = Immortal.get(blacks);
		HashMap<F, Double> result = new HashMap<>();

		for (F f: whitesResult.points)
			result.put(f, 1.0);
		for (F f: blacksResult.points)
			result.put(f, -1.0);
		for (G g: whitesResult.groups)
			whites.getFields(g).forEach(f -> result.put(f, 1.0));
		for (G g: blacksResult.groups)
			blacks.getFields(g).forEach(f -> result.put(f, -1.0));
		return result;
	}
}
