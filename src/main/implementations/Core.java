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
		this.parser = new Parser<>(fields::getField, size);
	}

	public abstract Color<Integer> makeColor();
	public Table<Integer, Integer> makeTable() {
		return new Table<>(fields.fields, fields::getAdjacency, makeColor(), makeColor());
	}
	public Game<Integer, Integer> makeGame() {
		return new Game<>(makeTable());
	}
	public Game<Integer, Integer> makeLogGame(Function<IState<Integer, Integer>, String> statelogger, Supplier<Boolean> loggingstate) {
		return new LogGame<>(makeTable(), statelogger, loggingstate);
	}
	public GameExtView<Integer> makeGameView() {
		return new GameExtView<>(makeGame(), parser, GameScore::getScore);
	}
	public GoAPI<GameExtView<Integer>> makeGameApi() {
		return new GoAPI<>(this::makeGameView);
	}

}
