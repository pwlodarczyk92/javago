package api;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import core.MoveNotAllowed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import score.Score;
import score.ScoreNotAllowed;
import spark.Request;
import spark.Response;

import java.util.HashMap;
import java.util.function.Supplier;

import static spark.Spark.halt;

/**
 * Created by maxus on 08.03.16.
 */
public class GoAPI<G extends GameExtView> {

	final protected Logger logger = LoggerFactory.getLogger(this.getClass());
	final protected HashMap<String, G> games = new HashMap<>();
	final protected Gson parser = new Gson();
	final protected HashMap<String, Action<G>> actions = new HashMap<>();
	final protected Supplier<? extends G> factory;

	protected static class Position {
		public int x;
		public int y;
	}

	protected static interface Action<G extends GameExtView> {
		public String apply(Request req, Response res, G game, JsonObject jbody);
	}

	public GoAPI(Supplier<? extends G> gamefactory) {
		actions.put("undo", this::undo);
		actions.put("pass", this::pass);
		actions.put("move", this::move);
		factory = gamefactory;
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

	public String put(Request req, Response res) {

		String id = req.queryParams("id");
		if (id == null)
			halt(404, "{}");

		if (games.containsKey(id) && !"true".equals(req.queryParams("force")))
			halt(409, "{}");

		games.put(id, factory.get());
		res.status(201);
		return "{}";

	}

	public String delete(Request req, Response res) {

		String id = req.queryParams("id");
		if (id == null || !games.containsKey(id))
			halt(404, "{}");

		games.remove(id);
		res.status(200);
		return "{}";

	}


	public String get(Request req, Response res) {

		String id = req.queryParams("id");
		if (id == null || !games.containsKey(id))
			halt(404, "{}");

		G game = games.get(id);
		return get(req, res, game);

	}

	public String post(Request req, Response res) {

		String id = req.queryParams("id");
		if (id == null || !games.containsKey(id))
			halt(404, "{}");

		G game = games.get(id);
		return post(req, res, game);
	}

	protected String get(Request request, Response response, GameExtView game) {

		String rawscore = request.queryParams("score");
		if (rawscore == null) {
			return parser.toJson(game.getView());
		} else try {
			Score scoretype;
			try { scoretype = Score.valueOf(rawscore); }
			catch (IllegalArgumentException e) { throw new ScoreNotAllowed(e); }
			return parser.toJson(game.getView(scoretype));
		} catch (ScoreNotAllowed e) {
			logger.warn("score argument is probably malformed", e);
			halt(422, "{}");
			return "{}";
		}
	}

	protected String post(Request req, Response res, G game) {

		JsonObject jbody = parser.fromJson(req.body(), JsonObject.class);
		String action = parser.fromJson(jbody.get("action"), String.class);
		try {
			return actions.get(action).apply(req, res, game, jbody);
		} catch (MoveNotAllowed e) {
			logger.warn("this move probably breaks rules", e);
			halt(422, "{}");
			return "{}";
		}

	}


}
