package standard;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.gson.Gson;
import core.board.IntBoard;
import core.board.LogBoard;
import core.board.StateView;
import core.color.IntColor;
import core.table.IntTable;
import core.views.Parser;
import core.views.State;
import server.GameExtView;
import server.ScoredView;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by maxus on 03.04.16.
 */
public class Implementation {

	private static Gson gsonParser = new Gson();

	private static int nteen = 19;
	private static List<Integer> fields = new ArrayList<>();
	private static List<List<Integer>> adjacents = new ArrayList<>();
	private static Table<Integer, Integer, Integer> fieldscache = HashBasedTable.create();
	private static final Parser<Integer> parser = new Parser<>(Implementation::field);

	static {
		int[] xdifs = {1, 0, -1, 0};
		int[] ydifs = {0, 1, 0, -1};
		for (int x = 0; x < nteen; x++) {
			for (int y = 0; y < nteen; y++) {
				fields.add(field(x, y));
				fieldscache.put(x, y, field(x, y));

				ArrayList<Integer> adjs = new ArrayList<>();
				for(int l=0; l<4; l++) {
					int xa = x+xdifs[l];
					int ya = y+ydifs[l];

					if (0<=xa && xa<nteen && 0<=ya && ya<nteen)
						adjs.add(field(xa, ya));
				}
				adjacents.add(adjs);
			}
		}
	}

	public static Integer field(int x, int y) {
		return x*19+y;
	}
	public static IntColor color() {
		return new IntColor(adjacents::get);
	}
	public static IntTable table() {
		return new IntTable(fields, adjacents::get, color(), color());
	}

	public static IntBoard intBoard() {
		return new IntBoard(table());
	}
	public static LogBoard<Integer, Integer> logBoard(Function<StateView<Integer, Integer>, String> statelogger, Supplier<Boolean> loggingstate) {
		return new LogBoard<>(table(), statelogger, loggingstate);
	}

	public static GameExtView gameExtView() {
		return new GameExtView(intBoard(), parser);
	}

	public static ScoredView scoredView() {
		return new ScoredView(intBoard(), intBoard(), parser);
	}

	public static ScoredView scoredLoggedView(Supplier<Boolean> loggingstate) {
		Function<StateView<Integer, Integer>, String> statelogger = state -> {
			State result = new State();
			parser.update(result, state);
			return gsonParser.toJson(result);
		};
		return new ScoredView(intBoard(), logBoard(statelogger, loggingstate), parser);
	}
}
