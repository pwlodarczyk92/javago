import server.GoAPI;
import server.GoMainAPI;

import static spark.Spark.*;

/**
 * Created by maxus on 20.02.16.
 */
public class Server {

	//--singleton--
	private static Server server = null;
	public static Server getServer() {
		if (server == null) {
			server = new Server();
		}
		return server;
	}
	private Server() {
	}
	//--singleton

	public static class Config {
		public int port = 8123;
		public boolean cors = false;
		public String path = "/game";
	}

	private GoAPI api = new GoMainAPI();
	private Config config = new Config();
	private boolean running = false;

	public void configure(Server.Config config) {
		if (running)
			throw new RuntimeException();
		this.config = config;
	}

	private static void enableCORS(final String origin, final String methods, final String headers) {
		before((request, response) -> {
			response.header("Access-Control-Allow-Origin", origin);
			response.header("Access-Control-Allow-Methods", methods);
			response.header("Access-Control-Allow-Headers", headers);
		});
	}

	public void run() {

		running = true;
		port(config.port);
		if (!config.cors) enableCORS("*", "GET, POST, PUT, DELETE", "*");
		get(config.path, api::get);
		put(config.path, api::put);
		post(config.path, api::post);
		delete(config.path, api::delete);
		options(config.path, (req, res) -> "{}");
	}

}
