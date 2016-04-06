package bot.helpers;

import bot.algos.Immortal;
import bot.algos.Safety;

import core.board.Game;
import core.color.ColorView;
import core.primitives.Stone;

import java.util.HashMap;

/**
 * Created by maxus on 03.04.16.
 */
public class Scoring {

	private static final int libmax = 4;
	private static final int count = 3;
	private static final int overflow = 15;

	public static <F> HashMap<F, Double> safety(Game<F, ?> board) {

		HashMap<F, Integer> data = Safety.safety(board, libmax, count, overflow);
		HashMap<F, Double> result = new HashMap<>();

		for(F field: data.keySet()) {
			Double factor = board.getview().gettable().getstone(field).equals(Stone.WHITE)?1.0:-1.0;
			Integer val = data.get(field);

			if (val.equals(Integer.MAX_VALUE))
				result.put(field, factor);
			else
				result.put(field, Math.min(0.85, val*0.15)*factor);

		}
		return result;
	}

	public static <F> HashMap<F, Double> immortals(Game<F, ?> board) {
		return helper(board);
	}

	private static <F, G> HashMap<F, Double> helper(Game<F, G> board) {
		ColorView<F, G> whites =  board.getview().gettable().getview(Stone.WHITE);
		ColorView<F, G> blacks =  board.getview().gettable().getview(Stone.BLACK);
		Immortal.Result<F, G> wr = Immortal.immortals(board.getview().gettable().getadjacency(), whites);
		Immortal.Result<F, G> br = Immortal.immortals(board.getview().gettable().getadjacency(), blacks);
		HashMap<F, Double> result = new HashMap<>();

		for (F f: wr.points)
			result.put(f, 1.0);
		for (F f: br.points)
			result.put(f, -1.0);
		for (G g: wr.groups)
			whites.getnodes(g).forEach(f -> result.put(f, 1.0));
		for (G g: br.groups)
			blacks.getnodes(g).forEach(f -> result.put(f, -1.0));
		return result;
	}

}
