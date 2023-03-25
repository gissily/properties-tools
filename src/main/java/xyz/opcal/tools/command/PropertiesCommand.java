package xyz.opcal.tools.command;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

import org.springframework.stereotype.Component;

import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;

@Component
@Command(mixinStandardHelpOptions = true, description = "properties file update command")
public class PropertiesCommand implements Runnable {

	@Spec
	CommandSpec spec;

	@Command(name = "list", description = "list all properties")
	public void list(@Parameters(index = "0", description = "properties file path") File file) {
		if (file == null || !file.exists()) {
			throw new ParameterException(spec.commandLine(), "file does not exist");
		}
		Properties properties = new Properties();
		try (var fileInputStream = new FileInputStream(file)) {
			properties.load(fileInputStream);
		} catch (Exception e) {
			throw new ParameterException(spec.commandLine(), "Properties load exception", e);
		}
		properties.forEach((key, value) -> System.out.println(key + "=" + value));
	}

	@Command(name = "keys", description = "list all keys")
	public void keys(@Parameters(index = "0", description = "properties file path") File file, @Option(names = "--line", defaultValue = "false") boolean line) {
		if (file == null || !file.exists()) {
			throw new ParameterException(spec.commandLine(), "file does not exist");
		}
		Properties properties = new Properties();
		try (var fileInputStream = new FileInputStream(file)) {
			properties.load(fileInputStream);
		} catch (Exception e) {
			throw new ParameterException(spec.commandLine(), "Properties load exception", e);
		}
		var keyString = new StringBuilder();
		var lineSeparator = line ? "\n" : " ";
		properties.keySet().forEach(key -> keyString.append(key).append(lineSeparator));
		System.out.println(keyString.toString());
	}

	@Command(name = "value", description = "get property value by key")
	public void getValue(@Parameters(index = "0", description = "properties file path") File file,
			@Parameters(index = "1", description = "properties key") String key) {
		if (file == null || !file.exists()) {
			throw new ParameterException(spec.commandLine(), "file does not exist");
		}

		Properties properties = new Properties();
		try (var fileInputStream = new FileInputStream(file)) {
			properties.load(fileInputStream);
		} catch (Exception e) {
			throw new ParameterException(spec.commandLine(), "Properties load exception", e);
		}

		System.out.println(properties.getProperty(key));
	}

	@Command(name = "set", description = "set property value by key")
	public void setValue(@Parameters(index = "0", description = "properties file path") File file,
			@Parameters(index = "1", description = "properties key") String key, @Parameters(index = "2", description = "properties value") String value) {
		if (file == null || !file.exists()) {
			throw new ParameterException(spec.commandLine(), "file does not exist");
		}

		Properties properties = new Properties();
		try (var fileInputStream = new FileInputStream(file)) {
			properties.load(fileInputStream);
		} catch (Exception e) {
			throw new ParameterException(spec.commandLine(), "Properties load exception", e);
		}
		properties.setProperty(key, value);

		try (var outStream = new FileOutputStream(file)) {
			properties.store(outStream, "update");
		} catch (Exception e) {
			throw new ParameterException(spec.commandLine(), "Properties save exception", e);
		}
		System.out.println(key + "=" + properties.getProperty(key));
	}

	@Override
	public void run() {
		throw new ParameterException(spec.commandLine(), "Specify a subcommand");
	}

}
