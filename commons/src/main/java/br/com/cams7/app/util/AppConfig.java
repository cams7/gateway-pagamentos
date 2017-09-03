package br.com.cams7.app.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class AppConfig {

	private static final Logger LOG = Logger.getLogger(AppConfig.class.getSimpleName());

	private static Properties properties;

	static {
		properties = new Properties();
		InputStream input = null;

		try {
			input =  AppConfig.class.getClassLoader().getResourceAsStream("app-config.properties");
			properties.load(input);
		} catch (IOException e1) {
			if (input != null)
				try {
					input.close();
				} catch (IOException e2) {
					LOG.log(Level.SEVERE, "Error: {0}", e2);
				}

			LOG.log(Level.SEVERE, "Error: {0}", e1);
		}
	}

	public static String getProperty(String property) {
		return properties.getProperty(property);
	}

}
