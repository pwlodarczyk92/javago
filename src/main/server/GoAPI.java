package server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import core.primitives.MoveNotAllowed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;

import java.util.HashMap;

/**
 * Created by maxus on 08.03.16.
 */
public abstract class GoAPI<G extends GameExtView> {

	final protected Logger logger = LoggerFactory.getLogger(this.getClass());
	final protected HashMap<String, G> games = new HashMap<>();
	final protected Gson parser = new Gson();
	final protected HashMap<String, Action<G>> actions = new HashMap<>();
	protected static class Position {
		public int x;
		public int y;
	}

	protected static interface Action<G extends GameExtView> {
		public String apply(Request req, Response res, G game, JsonObject jbody);
	}

	public GoAPI() {
		actions.put("undo", this::undo);
		actions.put("pass", this::pass);
		actions.put("move", this::move);
	}

	protected String pass(Request req, Response res, G game, JsonObject jbody) {
		game.pass();
		return "{}";
	}
	protected String undo(Request req, Response res, G game, JsonObject jbody) {
		game.undo();
		return "{}";
	}
	protected String move(Request req, Response res, G game, JsonObject jbody) {
		Position pos = parser.fromJson(jbody.get("position"), Position.class);
		game.put(pos.x, pos.y);
		return "{}";
	}


	public String get(Request req, Response res) {

		logger.info("GET: {}", req.queryString());
		String id = req.queryParams("id");

		if (id == null || !games.containsKey(id)) {
			res.status(404);
			return "{}";
		}

		G game = games.get(id);

		return get(req, res, game);

	}

	protected String get(Request req, Response res, G game) {
		return parser.toJson(game.getview());
	}

	public String put(Request req, Response res) {

		logger.info("PUT: {}", req.queryString());
		String id = req.queryParams("id");
		if (id == null || games.containsKey(id)) {
			res.status(id == null ? 404 : 200);
			return "{}";
		}

		return put(req, res, id);

	}

	protected abstract String put(Request req, Response res, String id);

	public String delete(Request req, Response res) {

		logger.info("DELETE: {}", req.queryString());
		String id = req.queryParams("id");
		if (id == null || !games.containsKey(id)) {
			res.status(404);
			return "{}";
		}

		return delete(req, res, id);

	}

	protected String delete(Request req, Response res, String id) {
		games.remove(id);
		return "{}";
	}

	public String post(Request req, Response res) {

		String body = req.body();
		logger.info("POST: {}", req.queryString());
		logger.info("body: {}", body);

		String id = req.queryParams("id");
		if (id == null || !games.containsKey(id)) {
			res.status(404);
			return "{}";
		}

		G game = games.get(id);

		return post(req, res, game, body);
	}

	protected String post(Request req, Response res, G game, String body) {

		JsonObject jbody = parser.fromJson(body, JsonObject.class);
		String action = parser.fromJson(jbody.get("action"), String.class);
		try {
			return actions.get(action).apply(req, res, game, jbody);
		} catch (MoveNotAllowed e) {
			res.status(400);
		}
		return "{}";

	}


}
