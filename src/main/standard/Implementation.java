package standard;

import com.google.common.collect.HashBasedTable;
import com.google.gson.Gson;
import core.board.*;
import core.color.Color;
import core.color.IColor;
import core.color.IntColor;
import core.table.ITable;
import core.table.IntTable;
import core.table.Table;
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
	private static List<Integer> xvals = new ArrayList<>();
	private static List<Integer> yvals = new ArrayList<>();
	private static List<List<Integer>> adjacents = new ArrayList<>();
	private static HashBasedTable<Integer, Integer, Integer> fieldscache = HashBasedTable.create();
	private static final Parser<Integer> parser = new Parser<>(Implementation::field);

	static {
		int[] xdifs = {1, 0, -1, 0};
		int[] ydifs = {0, 1, 0, -1};
		for (int x = 0; x < nteen; x++) {
			for (int y = 0; y < nteen; y++) {
				fields.add(field(x, y));
				xvals.add(x);
				yvals.add(y);
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
	public static Integer x(int f) {
		return xvals.get(f);
	}
	public static Integer y(int f) {
		return yvals.get(f);
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

	public static Color<Integer> icolor() {
		return new Color<>(adjacents::get);
	}
	public static Table<Integer, Integer> itable() {
		return new Table<>(fields, adjacents::get, icolor(), icolor());
	}
	public static Board<Integer, Integer> igame() {
		return new Board<>(itable());
	}
	public static LogBoard<Integer, Integer> logBoard(Function<StateView<Integer, Integer>, String> statelogger, Supplier<Boolean> loggingstate) {
		return new LogBoard<>(table(), statelogger, loggingstate);
	}

	public static GameExtView gameExtView() {
		return new GameExtView(igame(), parser);
	}
	public static ScoredView scoredView() {
		return new ScoredView(igame(), igame(), parser);
	}
	public static ScoredView scoredLoggedView(Supplier<Boolean> loggingstate) {
		Function<StateView<Integer, Integer>, String> statelogger = state -> {
			State result = new State();
			parser.update(result, state);
			return gsonParser.toJson(result);
		};
		return new ScoredView(igame(), logBoard(statelogger, loggingstate), parser);
	}
}
