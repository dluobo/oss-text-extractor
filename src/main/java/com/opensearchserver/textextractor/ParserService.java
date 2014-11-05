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

import java.io.File;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

@Path("/")
public class ParserService {

	private static final Logger logger = Logger.getLogger(ParserService.class
			.getName());

	public final static String APPLICATION_JSON_UTF8 = "application/json;charset=UTF-8";

	@GET
	@Produces(APPLICATION_JSON_UTF8)
	public Map<String, ResourceLink> list(@Context UriInfo uriInfo) {
		Set<String> parserList = ParserList.getList();
		Map<String, ResourceLink> map = new LinkedHashMap<String, ResourceLink>(
				parserList.size());
		for (String parserName : parserList)
			map.put(parserName,
					new ResourceLink(ResourceLink.join(uriInfo.getPath(),
							parserName)));
		return map;
	}

	private void throwError(Status status, String msg) {
		throw new WebApplicationException(Response.status(status)
				.entity("Error. " + msg).build());
	}

	private void throwError(Exception e) {
		logger.log(Level.WARNING, e.getMessage(), e);
		throwError(Status.INTERNAL_SERVER_ERROR, e.getMessage());
	}

	private ParserAbstract getParser(UriInfo uriInfo, String parserName) {
		Class<? extends ParserAbstract> parserClass = ParserList
				.findParserClass(parserName);
		if (parserClass == null)
			throwError(Status.NOT_FOUND, "Unknown parser: " + parserName);
		try {
			return parserClass.newInstance();
		} catch (InstantiationException | IllegalAccessException
				| IllegalArgumentException | SecurityException e) {
			throwError(e);
			return null;
		}
	}

	@GET
	@Path("/{name}")
	@Produces(APPLICATION_JSON_UTF8)
	public Object get(@Context UriInfo uriInfo,
			@PathParam("name") String parserName,
			@QueryParam("path") String path) {
		ParserAbstract parser = getParser(uriInfo, parserName);
		if (path == null)
			return new ParserDefinition(uriInfo.getPath(), parser);
		File file = new File(path);
		if (!file.exists())
			throwError(Status.NOT_FOUND, "File not found: " + path);
		try {
			return parser.doParsing(uriInfo, file);
		} catch (Exception e) {
			throwError(e);
			return null;
		}
	}

	@PUT
	@Path("/{name}")
	@Produces(APPLICATION_JSON_UTF8)
	public ParserResult put(@Context UriInfo uriInfo,
			@PathParam("name") String parserName, InputStream inputStream) {
		ParserAbstract parser = getParser(uriInfo, parserName);
		try {
			return parser.doParsing(uriInfo, inputStream);
		} catch (Exception e) {
			throwError(e);
			return null;
		}
	}

}
