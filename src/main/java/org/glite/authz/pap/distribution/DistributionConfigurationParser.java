package org.glite.authz.pap.distribution;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.glite.authz.pap.common.PAP;
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
	//private static final String lineRegex = "\"()\"\\s\"([\\w\\.]+)\"";
	private static final String lineRegex = "\"(.+)\"\\s\"(.+)\"";
	private static final Pattern linePattern = Pattern.compile(lineRegex);

	public static DistributionConfigurationParser getInstance() {
		return new DistributionConfigurationParser();
	}

	private int lineCounter = 0;

	private List<PAP> remotePAPList = null;

	private DistributionConfigurationParser() {
		remotePAPList = new LinkedList<PAP>();
	}

	public List<PAP> parse(File file) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));

			String line = null;
			lineCounter = 0;
			do {
				line = reader.readLine();
				if (lineIsMeaningful(line)) {
					PAP pap = parseLine(line);
					if (pap == null) {
						log.error("Syntax error at line " + lineCounter + " of file " + file.getAbsolutePath() + " (line skipped)");
					} else {
						remotePAPList.add(pap);
					}
				}
				lineCounter++;
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

	protected PAP parseLine(String line) {
		Matcher lineMatcher = linePattern.matcher(line);
		if (lineMatcher.matches()) {
			return new PAP(lineMatcher.group(1), lineMatcher.group(2));
		} 
		return null;
	}
}
