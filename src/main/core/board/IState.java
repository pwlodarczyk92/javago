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

	public Integer getPassCount();
	public Integer getWhitePoints();
	public Integer getBlackPoints();
	public Stone getCurrentStone();
	public TableView<F, G> getTable();

	public Map.Entry<? extends Set<F>, ? extends IState<F, G>> put(F field);

}
