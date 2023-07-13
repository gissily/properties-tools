package xyz.opcal.tools.command;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.FileInputStream;
import java.util.Properties;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.ResourceUtils;

@SpringBootTest(args = { "set", "./target/test-classes/test.properties", "new-key", "12345" })
class SetTests {

	
	static {
		System.out.println("----------");
		System.out.println("set value");
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
