package xyz.opcal.tools.command;

import java.io.File;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.StreamSupport;

import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.BuilderParameters;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.PropertiesBuilderParametersImpl;
import org.apache.commons.configuration2.builder.fluent.FileBasedBuilderParameters;
import org.apache.commons.configuration2.builder.fluent.PropertiesBuilderParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.stereotype.Component;

import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;
import xyz.opcal.tools.PropertiesCommandConstants;

@Component
@Command(mixinStandardHelpOptions = true, description = "properties file update command", versionProvider = PropsVersionProvider.class)
public class PropertiesCommand {

	private static Logger commandConsole = LoggerFactory.getLogger("COMMAND_CONSOLE");

	@Spec
	CommandSpec spec;

	static final Class<?>[] interfaces = new Class<?>[] { PropertiesBuilderParameters.class, FileBasedBuilderParameters.class };

	static {
		builderProperties();
	}
	
	/**
	 * native support
	 * 
	 * @return
	 */
	static PropertiesBuilderParameters builderProperties() {
		ClassLoader classLoader = ProxyFactory.class.getClassLoader();
		InvocationHandler handler = new ParametersIfcInvocationHandler(new PropertiesBuilderParametersImpl());
		var newProxyInstance = (PropertiesBuilderParameters) Proxy.newProxyInstance(classLoader, interfaces, handler);
		new org.apache.commons.configuration2.builder.fluent.Parameters().getDefaultParametersManager().initializeParameters(newProxyInstance);
		return newProxyInstance;
	}

	@Command(name = "list", description = "list all properties")
	public void list(@Parameters(index = "0", description = "properties file path") File file) {
		if (file == null || !file.exists()) {
			throw new ParameterException(spec.commandLine(), PropertiesCommandConstants.ErrorConstants.FILE_NOT_EXSIT);
		}

		var builder = new FileBasedConfigurationBuilder<>(PropertiesConfiguration.class).configure(builderProperties().setFile(file));
		try {
			var configuration = builder.getConfiguration();
			configuration.getKeys().forEachRemaining(key -> commandConsole.info("{}={}", key, configuration.getProperty(key)));
		} catch (Exception e) {
			throw new ParameterException(spec.commandLine(), PropertiesCommandConstants.ErrorConstants.PROPERTIES_LOAD_EXCEPTION, e);
		}
	}

	@Command(name = "keys", description = "list all keys")
	public void keys(@Parameters(index = "0", description = "properties file path") File file, @Option(names = "--line", defaultValue = "false") boolean line) {
		if (file == null || !file.exists()) {
			throw new ParameterException(spec.commandLine(), PropertiesCommandConstants.ErrorConstants.FILE_NOT_EXSIT);
		}

		var builder = new FileBasedConfigurationBuilder<>(PropertiesConfiguration.class).configure(builderProperties().setFile(file));
		try {
			var configuration = builder.getConfiguration();
			var lineSeparator = line ? "\n" : " ";
			var keys = StreamSupport.stream(Spliterators.spliteratorUnknownSize(configuration.getKeys(), Spliterator.ORDERED), false)
					.reduce(new StringBuilder(), (keyString, key) -> keyString.append(key).append(lineSeparator), (t, u) -> u).toString();
			commandConsole.info(keys);
		} catch (Exception e) {
			throw new ParameterException(spec.commandLine(), PropertiesCommandConstants.ErrorConstants.PROPERTIES_LOAD_EXCEPTION, e);
		}

	}

	@Command(name = "value", description = "get property value by key")
	public void getValue(@Parameters(index = "0", description = "properties file path") File file,
			@Parameters(index = "1", description = "properties key") String key) {
		if (file == null || !file.exists()) {
			throw new ParameterException(spec.commandLine(), PropertiesCommandConstants.ErrorConstants.FILE_NOT_EXSIT);
		}

		var builder = new FileBasedConfigurationBuilder<>(PropertiesConfiguration.class).configure(builderProperties().setFile(file));
		try {
			var configuration = builder.getConfiguration();
			commandConsole.info("{}", configuration.getProperty(key));
		} catch (Exception e) {
			throw new ParameterException(spec.commandLine(), PropertiesCommandConstants.ErrorConstants.PROPERTIES_LOAD_EXCEPTION, e);
		}
	}

	@Command(name = "set", description = "set property value by key")
	public void setValue(@Parameters(index = "0", description = "properties file path") File file,
			@Parameters(index = "1", description = "properties key") String key, @Parameters(index = "2", description = "properties value") String value) {
		if (file == null || !file.exists()) {
			throw new ParameterException(spec.commandLine(), PropertiesCommandConstants.ErrorConstants.FILE_NOT_EXSIT);
		}

		var builder = new FileBasedConfigurationBuilder<>(PropertiesConfiguration.class).configure(builderProperties().setFile(file));
		try {
			var configuration = builder.getConfiguration();
			configuration.getLayout().setGlobalSeparator("=");
			configuration.getLayout().setFooterComment(null);
			configuration.setProperty(key, value);
			builder.save();
			commandConsole.info("{}={}", key, configuration.getProperty(key));
		} catch (Exception e) {
			throw new ParameterException(spec.commandLine(), PropertiesCommandConstants.ErrorConstants.PROPERTIES_LOAD_EXCEPTION, e);
		}
	}

	/**
	 * native support
	 */
	private static class ParametersIfcInvocationHandler implements InvocationHandler {
		private final Object target;

		public ParametersIfcInvocationHandler(final Object targetObj) {
			target = targetObj;
		}

		@Override
		public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
			final Object result = method.invoke(target, args);
			return isFluentResult(method) ? proxy : result;
		}

		private static boolean isFluentResult(final Method method) {
			final Class<?> declaringClass = method.getDeclaringClass();
			return declaringClass.isInterface() && !declaringClass.equals(BuilderParameters.class);
		}
	}
}
