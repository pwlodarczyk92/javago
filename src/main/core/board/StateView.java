package core.board;

import core.primitives.Stone;
import core.table.TableView;

/**
 * Created by maxus on 03.04.16.
 */
public interface StateView<F, G>{

	public Integer getpasscount();
	public Integer getwhitepoints();
	public Integer getblackpoints();
	public Stone getcurrentstone();
	public TableView<F, G> gettable();

}
