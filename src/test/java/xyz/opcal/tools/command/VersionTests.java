package xyz.opcal.tools.command;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.FileNotFoundException;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.ResourceUtils;

@SpringBootTest(args = { "--version" })
class VersionTests {

	private static Logger commandConsole = LoggerFactory.getLogger("COMMAND_CONSOLE");

	static {
		commandConsole.info("----------");
		commandConsole.info("version check");
	}

	@Test
	void keys() throws FileNotFoundException {
		var file = ResourceUtils.getFile("./target/test-classes/test.properties");
		assertTrue(file.exists());
	}

}
