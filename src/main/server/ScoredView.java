package server;

import bot.GameScore;
import bot.Score;
import core.board.Board;
import core.board.Game;
import core.views.Parser;
import core.views.ScoredState;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by maxus on 31.03.16.
 */
public class ScoredView extends GameExtView {

	private final GameScore gamescore;

	public <F, G> ScoredView(Game<F, G> game, Game<F, G> helpergame, Parser<F> parser) {
		super(game, parser);
		gamescore = new GameScore<>(game, helpergame);
	}

	public final ScoredState getview(Score score) {
		ScoredState state = new ScoredState();
		parser.update(state, game.getview());
		state.scores = new ArrayList<>();
		Map<?, Double> scorevalues = gamescore.getscores(score);
		for(int i=0; i<nteen; i++) {
			ArrayList<Double> column = new ArrayList<>();
			for(int j=0; j< nteen; j++) {
				column.add(scorevalues.get(parser.fields.apply(i, j)));
			}
			state.scores.add(column);
		}
		return state;
	}

}
