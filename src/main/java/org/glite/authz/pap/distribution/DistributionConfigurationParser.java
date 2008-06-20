package org.glite.authz.pap.distribution;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.glite.authz.pap.distribution.exceptions.DistributionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DistributionConfigurationParser {
	private static final Logger log = LoggerFactory
			.getLogger(DistributionConfigurationParser.class);

	private static final String emptyLineRegex = "\\s*$";
	private static final Pattern emptyLinePattern = Pattern
			.compile(emptyLineRegex);
	private static final String commentRegex = "^#.*$";
	private static final Pattern commentPattern = Pattern.compile(commentRegex);
	private static final String dnRegex = "^\"((/[^=]+=([^/]|\\s)+)+)\"\\s*";
	private static final Pattern dnPattern = Pattern.compile(dnRegex);
	private static final String endpointRegex = "^\"((/[^=]+=([^/]|\\s)+)+)\"\\s*";
	private static final Pattern endpointPattern = Pattern
			.compile(endpointRegex);
	private static final String remotePAPRegex = "\"()\"\\s\"([\\w\\.]+)\"";
	private static final Pattern remotePAPPattern = Pattern
			.compile(remotePAPRegex);

	public static DistributionConfigurationParser getInstance() {
		return new DistributionConfigurationParser();
	}

	private int lineCounter = 0;

	private List<RemotePAP> remotePAPList = null;

	private DistributionConfigurationParser() {
		remotePAPList = new LinkedList<RemotePAP>();
	}

	public List<RemotePAP> parse(File file) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));

			String line = null;
			lineCounter = 0;
			do {
				line = reader.readLine();
				lineCounter++;
				if (lineIsMeaningful(line)) {
					remotePAPList.add(parseLine(line));
				}
			} while (line != null);

			return remotePAPList;

		} catch (IOException e) {
			throw new DistributionException(e);
		}
	}

	protected boolean lineIsMeaningful(String line) {
		if (line == null) {
			return false;
		}
		if (line.length() == 0) {
			return false;
		}

		Matcher commentMatcher = commentPattern.matcher(line);
		Matcher emptyLineMatcher = emptyLinePattern.matcher(line);
		if (commentMatcher.matches()) {
			return false;
		}
		if (emptyLineMatcher.matches()) {
			return false;
		}
		return true;
	}

	protected RemotePAP parseLine(String line) {
		Matcher remotePAPMatcher = remotePAPPattern.matcher(line);
		if (remotePAPMatcher.matches()) {
			return new RemotePAP(null, "YES: " + remotePAPMatcher.group(2));
		}
		return new RemotePAP(null, "NO");
	}

}
