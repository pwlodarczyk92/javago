package implementations;

import core.board.Game;
import core.board.IState;
import core.board.LogGame;
import core.color.Color;
import core.table.Table;
import score.GameScore;
import api.GameExtView;
import api.GoAPI;
import api.Parser;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by maxus on 15.04.16.
 */
abstract class Core {

	public final Fields fields;
	private final Parser<Integer> parser;

	public Core(int size) {
		this.fields = new Fields(size);
		this.parser = new Parser<>(fields::field, size);
	}

	public abstract Color<Integer> makecolor();
	public Table<Integer, Integer> maketable() {
		return new Table<>(fields.fields, fields::adjacent, makecolor(), makecolor());
	}
	public Game<Integer, Integer> makegame() {
		return new Game<>(maketable());
	}
	public Game<Integer, Integer> makeloggame(Function<IState<Integer, Integer>, String> statelogger, Supplier<Boolean> loggingstate) {
		return new LogGame<>(maketable(), statelogger, loggingstate);
	}
	public GameExtView<Integer> makegameview() {
		return new GameExtView<>(makegame(), parser, GameScore::getscores);
	}
	public GoAPI<GameExtView<Integer>> makeapi() {
		return new GoAPI<>(this::makegameview);
	}

}
