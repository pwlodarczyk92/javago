package algos.atari;

import core.Stone;
import core.board.IGame;
import core.color.ColorView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

/**
 * Created by maxus on 22.04.16.
 */
public class AtariScore {

	private static final Logger logger = LoggerFactory.getLogger(AtariScore.class);
	public static <F> HashMap<F, Double> score(IGame<F, ?> board) {
		return _score(board);
	}

	private static <F, G> HashMap<F, Double> _score(IGame<F, G> board) {
		ColorView<F, G> whites =  board.getState().getTable().getView(Stone.WHITE);
		ColorView<F, G> blacks =  board.getState().getTable().getView(Stone.BLACK);
		HashMap<F, Double> result = new HashMap<>();

		for (G group: whites.getGroups()) {
			F field = whites.getAnyField(group);
			Double value = Atari.isAtari(board, field) ? 1.0 : 0.0;
			for (F groupField: whites.getFields(group))
				result.put(groupField, value);
		}
		for (G group: blacks.getGroups()) {
			F field = blacks.getAnyField(group);
			Double value = Atari.isAtari(board, field) ? -1.0 : 0.0;
			for (F groupField: blacks.getFields(group))
				result.put(groupField, value);
		}
		return result;
	}

}
