package win.hellobro.web;

import team.balam.exof.App;
import team.balam.exof.Container;
import team.balam.exof.environment.SystemSetting;

import java.util.Map;

public class WebService implements Container {
	private static Map<String, Object> config;

	@Override
	public String getName() {
		return "web-service-container";
	}

	@Override
	public void start() throws Exception {
		config = SystemSetting.getExternal();
	}

	@Override
	public void stop() throws Exception {

	}

	@SuppressWarnings("unchecked")
	public static <T> T getConfig(String key) {
		return (T) config.get(key);
	}

	public static void main(String[] a) {
		App.start();
	}
}
