package core.board;

import core.Stone;
import core.table.TableView;
import utils.Forkable;

import java.util.Map;
import java.util.Set;

/**
 * Created by maxus on 13.04.16.
 */
public interface IState<F, G> extends Forkable<IState<F, G>> {

	public Integer getpasscount();
	public Integer getwhitepoints();
	public Integer getblackpoints();
	public Stone getcurrentstone();
	public TableView<F, G> gettable();

	public Map.Entry<? extends Set<F>, ? extends IState<F, G>> put(F field);

}
