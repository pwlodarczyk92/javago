import server.GoAPI;

import static spark.Spark.*;

/**
 * Created by maxus on 20.02.16.
 */
public class Main {

	private GoAPI api = new GoAPI();

	public static void main(String[] args) {
		Main main = new Main();
		main.run();
	}

	private static void enableCORS(final String origin, final String methods, final String headers) {
		before((request, response) -> {
			response.header("Access-Control-Allow-Origin", origin);
			response.header("Access-Control-Request-Method", methods);
			response.header("Access-Control-Allow-Headers", headers);
		});
	}

	public void run() {

		port(8123);
		enableCORS("*", "*", "*");
		get("/game", api::get);
		put("/game", api::put);
		post("/game", api::post);
		delete("/game", api::delete);

	}

}
