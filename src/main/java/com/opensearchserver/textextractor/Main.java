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

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import org.jboss.resteasy.plugins.server.undertow.UndertowJaxrsServer;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

@ApplicationPath("/")
public class Main extends Application {

	@Override
	public Set<Class<?>> getClasses() {
		HashSet<Class<?>> classes = new HashSet<Class<?>>();
		classes.add(JacksonJsonProvider.class);
		classes.add(ParserService.class);
		return classes;
	}

	public static void main(String[] args) throws IOException {
		UndertowJaxrsServer server = new UndertowJaxrsServer().start(Undertow
				.builder().addHttpListener(9091, "localhost"));
		server.deploy(Main.class);
	}
}
