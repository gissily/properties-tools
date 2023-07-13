package xyz.opcal.tools;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportRuntimeHints;

import picocli.CommandLine;
import picocli.CommandLine.IFactory;
import xyz.opcal.tools.command.PropertiesCommand;
import xyz.opcal.tools.hints.AppRuntimeHints;

@ImportRuntimeHints({ AppRuntimeHints.class })
@SpringBootApplication
public class Commander implements CommandLineRunner, ExitCodeGenerator {

	private int exitCode;
	private IFactory factory;
	private PropertiesCommand propertiesCommand;

	public Commander(IFactory factory, PropertiesCommand propertiesCommand) {
		this.factory = factory;
		this.propertiesCommand = propertiesCommand;
	}

	@Override
	public int getExitCode() {
		return exitCode;
	}

	@Override
	public void run(String... args) throws Exception {
		exitCode = new CommandLine(propertiesCommand, factory).execute(args);
	}

	public static void main(String[] args) {
		System.exit(SpringApplication.exit(SpringApplication.run(Commander.class, args)));
	}

}
