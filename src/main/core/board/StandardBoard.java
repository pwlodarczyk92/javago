package core.board;

import core.primitives.Stone;
import core.table.IntTable;
import core.color.IntColor;
import server.GameExtView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

/**
 * Created by maxus on 06.03.16.
 */
public class StandardBoard extends IntBoard implements GameExtView {

	private final static int nineteen = 19;

	private static ArrayList<Integer> fields = new ArrayList<>();
	private static Integer[] x = new Integer[nineteen*nineteen];
	private static Integer[] y = new Integer[nineteen*nineteen];

	private static HashMap<Integer, List<Integer>> adjacents = new HashMap<>();


	static {
		int[] xdifs = {1, 0, -1, 0};
		int[] ydifs = {0, 1, 0, -1};
		for(int i=0; i<nineteen; i++) {
			for(int j=0; j<nineteen; j++) {

				Integer f = field(i, j);
				fields.add(f);
				x[f] = i;
				y[f] = j;

				ArrayList<Integer> adjs = new ArrayList<>();
				for(int l=0; l<4; l++) {
					int x = i+xdifs[l];
					int y = j+ydifs[l];
					if (0<=x && x<nineteen && 0<=y && y<nineteen) {
						adjs.add(field(x, y));
					}
				}
				adjacents.put(field(i, j), adjs);
			}
		}
	}

	public StandardBoard() {
		super(new IntTable(fields, adjacents::get, new IntColor(adjacents::get), new IntColor(adjacents::get)));
	}

	public static Integer field(int x, int y) {
		return x*nineteen+y;
	}

	public static int x(Integer field) { return x[field]; }

	public static int y(Integer field) { return y[field]; }

	public static Function<Integer, Collection<Integer>> adjacency() {return adjacents::get;}


	public Stone getstone(int x, int y) {
		return getstone(field(x, y));
	}

	public Stone getstone(Integer field) {
		return tableview().getstone(field);
	}


	@Override
	public List<List<Stone>> getstones() {
		List<List<Stone>> columns = new ArrayList<>();
		for(int i=0; i<nineteen; i++) {
			ArrayList<Stone> column = new ArrayList<>();
			for (int j = 0; j < nineteen; j++) {
				column.add(getstone(i, j));
			}
			columns.add(column);
		}
		return columns;
	}

	@Override
	public Stone current() {
		return currentstone();
	}

	@Override
	public Integer passcount() {
		return passes();
	}

	@Override
	public void undo() {
		undoput();
	}

	@Override
	public int blackpts() {
		return points(Stone.BLACK);
	}

	@Override
	public int whitepts() {
		return points(Stone.WHITE);
	}


	@Override
	public void put(int x, int y) {
		put(field(x, y));
	}

	@Override
	public void pass() {
		put(null);
	}


}
