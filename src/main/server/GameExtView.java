package server;

import core.board.Game;
import core.views.Parser;
import core.views.State;

/**
 * Created by maxus on 07.03.16.
 */
public class GameExtView implements ExtView {

	protected static final int nteen = 19;
	protected final Game game;
	protected final Parser parser;

	public <F> GameExtView(Game<F, ?> game, Parser<F> parser) {
		this.game = game;
		this.parser = parser;
	}

	@Override
	public void put(int x, int y) { game.put(parser.fields.apply(x, y)); }
	@Override
	public void pass() { game.put(null); }
	@Override
	public void undo() { game.undo(); }
	@Override
	public final State getview() {
		State state = new State();
		parser.update(state, game.getview());
		return state;
	}

}
