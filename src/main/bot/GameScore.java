package bot;

import bot.helpers.GameManager;
import bot.helpers.Scoring;
import core.board.Game;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by maxus on 31.03.16.
 */
public class GameScore<F> {

	final private static Logger logger = LoggerFactory.getLogger(GameScore.class.getName());
	private final GameManager<F, ?> boardManager;
	private final Scoring scoring = new Scoring();


	public<G> GameScore(Game<F, G> basegame, Game<F, G> cleangame) {
		this.boardManager = new GameManager<>(basegame, cleangame);
	}

	public synchronized Map<F, Double> getscores(Score score) {
		switch (score) {
			case IMMORTAL:
				return scoring.immortals(boardManager.board());
			case SAFETY:
				return scoring.safety(boardManager.board());
			default: throw new RuntimeException();
		}
	}

}
