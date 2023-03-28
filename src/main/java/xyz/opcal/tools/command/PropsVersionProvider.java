package xyz.opcal.tools.command;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import picocli.CommandLine.IVersionProvider;

@Component
public class PropsVersionProvider implements IVersionProvider {

	static final String SUFFIX = "-SNAPSHOT";

	@Value("${info.app.version}")
	private String version;

	@Override
	public String[] getVersion() throws Exception {
		String commandVersion = version;
		if (StringUtils.endsWithIgnoreCase(version, SUFFIX)) {
			commandVersion = StringUtils.replace(version, SUFFIX, "");
		}
		return new String[] { commandVersion };
	}

}
