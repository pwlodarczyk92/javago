package core.board;

import core.primitives.Stone;
import core.board.Board;
import core.table.IntTable;
import core.table.Table;
import core.color.IntColor;
import core.table.TableView;
import game.GameExtView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by maxus on 06.03.16.
 */
public class StandardBoard extends IntBoard implements GameExtView {

	private static HashMap<Integer, List<Integer>> adjacents = new HashMap<>();
	private final static int nineteen = 19;

	static {
		int[] xdifs = {1, 0, -1, 0};
		int[] ydifs = {0, 1, 0, -1};
		for(int i=0; i<nineteen; i++) {
			for(int j=0; j<nineteen; j++) {
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
		super(new IntTable(adjacents::get, new IntColor(adjacents::get), new IntColor(adjacents::get)));
	}

	private static Integer field(int x, int y) {
		return x*nineteen+y;
	}

	public Stone get(int x, int y) {
		return tableview().getstone(field(x, y));
	}

	@Override
	public List<List<Stone>> get() {
		List<List<Stone>> columns = new ArrayList<>();
		for(int i=0; i<nineteen; i++) {
			ArrayList<Stone> column = new ArrayList<>();
			for (int j = 0; j < nineteen; j++) {
				column.add(get(i, j));
			}
			columns.add(column);
		}
		return columns;
	}

	@Override
	public Stone current() {
		return currstone;
	}

	@Override
	public Integer passes() {
		return passcounter;
	}

	@Override
	public void put(int x, int y) {
		put(field(x, y));
	}

	@Override
	public void pass() {
		put(null);
	}

	public void undo() {
		undoput();
	}

}
