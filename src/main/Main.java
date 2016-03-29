import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import java.io.*;
import java.net.URL;
import java.util.Properties;

/**
 * Created by maxus on 29.03.16.
 */
public class Main {

	public static void main(String[] args) throws IOException {

		Properties props = new Properties();
		String input = Resources.toString(Resources.getResource("server.config"), Charsets.UTF_8);
		props.load(new StringReader(input));

		Server.Config config = new Server.Config();
		config.port = Integer.parseInt(props.getProperty("PORT"));
		config.cors = Boolean.parseBoolean(props.getProperty("CORS"));
		config.path = props.getProperty("PATH" +
				"");

		Server s = Server.getServer();
		s.configure(config);
		s.run();

	}

}
