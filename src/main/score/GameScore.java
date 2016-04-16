package score;

import algos.immortal.ImmortalScore;
import algos.tree.TreeScore;
import core.MoveNotAllowed;
import core.board.IGame;

import java.util.Map;

/**
 * Created by maxus on 31.03.16.
 */
public class GameScore {

	public static <F> Map<F, Double> getScore(Score score, IGame<F, ?> game) {

		try {
			switch (score) {
				case IMMORTAL:
					return ImmortalScore.score(game.fork());
				case SCORE:
					return TreeScore.score(game.fork());
				default: throw new RuntimeException();
			}
		} catch (MoveNotAllowed e) {
			throw new RuntimeException(e);
		}

	}

}
