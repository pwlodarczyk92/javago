package server;

import core.board.StandardBoard;
import spark.Request;
import spark.Response;

/**
 * Created by maxus on 31.03.16.
 */
public class GoMainAPI extends GoAPI<StandardBoard> {

	protected String put(Request req, Response res, String id) {
		games.put(id, new StandardBoard());
		res.status(201);
		return "{}";
	}

}
