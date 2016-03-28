package core.board;

import core.table.IntTable;
import core.table.Table;
import core.table.TableView;

/**
 * Created by maxus on 29.03.16.
 */
public class IntBoard extends Board<Integer, Integer, IntTable, TableView<Integer, Integer>> {
	public IntBoard(IntTable table) {
		super(table);
	}
}
