package api;

import core.MoveNotAllowed;
import core.board.IGame;
import score.Score;
import score.ScoreNotAllowed;

import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;

/**
 * Created by maxus on 07.03.16.
 */
public class GameExtView<F> implements ExtView {

	protected final IGame<F, ?> game;
	protected final Parser<F> parser;
	private final BiFunction<Score, IGame<F, ?>, Map<F, Double>> scorer;

	public GameExtView(IGame<F, ?> game, Parser<F> parser) {
		this(game, parser, null);
	}

	public GameExtView(IGame<F, ?> game, Parser<F> parser, BiFunction<Score, IGame<F, ?>, Map<F, Double>> scorer) {
		this.game = game;
		this.parser = parser;
		this.scorer = scorer;
	}

	@Override
	public synchronized void put(int x, int y) {
		Set<F> result = game.put(parser.fields.apply(x, y));
		if (result == null) throw new MoveNotAllowed();
	}

	@Override
	public synchronized void pass() {
		Set<F> result = game.put(null);
		if (result == null) throw new MoveNotAllowed();
	}

	@Override
	public synchronized void undo() { game.undo(); }

	@Override
	public synchronized Snap getView() {
		return getView(null);
	}

	@Override
	public synchronized Snap getView(Score score) {
		Snap state = new Snap();
		parser.update(state, game.getState());
		if (score != null && scorer == null)
			throw new ScoreNotAllowed();
		if (score != null)
			state.scores = parser.toList(scorer.apply(score, game));
		return state;
	}
}
