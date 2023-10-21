package xyz.opcal.tools.command;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.FileInputStream;
import java.util.Properties;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.ResourceUtils;

@SpringBootTest(args = { "set", "./target/test-classes/test.properties", "new-key", "12345" })
class SetTests {

	private static Logger commandConsole = LoggerFactory.getLogger("COMMAND_CONSOLE");

	static {
		commandConsole.info("----------");
		commandConsole.info("set value");
	}

	@Test
	void set() throws Exception {
		var file = ResourceUtils.getFile("./target/test-classes/test.properties");
		Properties properties = new Properties();
		try (var fileInputStream = new FileInputStream(file)) {
			properties.load(fileInputStream);
		}
		assertNotNull(properties.getProperty("new-key"));
	}

}
