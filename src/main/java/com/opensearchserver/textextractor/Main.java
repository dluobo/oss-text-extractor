/**
 * Copyright 2014 OpenSearchServer Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.opensearchserver.textextractor;

import io.undertow.Undertow;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.jboss.resteasy.plugins.server.undertow.UndertowJaxrsServer;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

@ApplicationPath("/")
public class Main extends Application {

	@Override
	public Set<Class<?>> getClasses() {
		HashSet<Class<?>> classes = new HashSet<Class<?>>();
		classes.add(JacksonConfig.class);
		classes.add(JacksonJsonProvider.class);
		classes.add(ParserService.class);
		return classes;
	}

	public static void main(String[] args) throws IOException, ParseException {
		Logger.getLogger("").setLevel(Level.WARNING);
		Options options = new Options();
		options.addOption("help", false, "print this message");
		options.addOption("port", true, "TCP port");
		CommandLineParser parser = new GnuParser();
		CommandLine cmd = parser.parse(options, args);
		if (cmd.hasOption("help")) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("java -jar target/oss-text-extractor.jar",
					options);
			return;
		}
		int port = cmd.hasOption("port") ? Integer.parseInt(cmd
				.getOptionValue("port")) : 9091;
		UndertowJaxrsServer server = new UndertowJaxrsServer().start(Undertow
				.builder().addHttpListener(port, "localhost"));
		server.deploy(Main.class);
	}
}
