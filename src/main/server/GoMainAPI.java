package server;

import spark.Request;
import spark.Response;
import standard.Implementation;

/**
 * Created by maxus on 31.03.16.
 */
public class GoMainAPI extends GoAPI<GameExtView> {

	protected String put(Request req, Response res, String id) {
		games.put(id, Implementation.gameExtView());
		res.status(201);
		return "{}";
	}

}
