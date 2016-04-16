package algos.immortal;

import core.board.IGame;
import core.color.ColorView;
import core.Stone;

import java.util.HashMap;

/**
 * Created by maxus on 11.04.16.
 */
public class ImmortalScore {

	public static <F> HashMap<F, Double> score(IGame<F, ?> board) {
		return helper(board);
	}

	private static <F, G> HashMap<F, Double> helper(IGame<F, G> board) {
		ColorView<F, G> whites =  board.getstate().gettable().getview(Stone.WHITE);
		ColorView<F, G> blacks =  board.getstate().gettable().getview(Stone.BLACK);
		Immortal.Result<F, G> wr = Immortal.get(whites);
		Immortal.Result<F, G> br = Immortal.get(blacks);
		HashMap<F, Double> result = new HashMap<>();

		for (F f: wr.points)
			result.put(f, 1.0);
		for (F f: br.points)
			result.put(f, -1.0);
		for (G g: wr.groups)
			whites.getstones(g).forEach(f -> result.put(f, 1.0));
		for (G g: br.groups)
			blacks.getstones(g).forEach(f -> result.put(f, -1.0));
		return result;
	}
}
