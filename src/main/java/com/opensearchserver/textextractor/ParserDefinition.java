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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.opensearchserver.textextractor.Link.Method;

@JsonInclude(Include.NON_EMPTY)
public class ParserDefinition {

	@JsonProperty("_link1")
	public final Link getParse;

	@JsonProperty("_link2")
	public final Link put;

	public final ParserField[] returnedFields;

	public ParserDefinition() {
		getParse = null;
		put = null;
		returnedFields = null;
	}

	public ParserDefinition(String resourcePath, ParserAbstract parser) {
		ParserField[] parameters = parser.getParameters();
		ParserField[] getParserFields = new ParserField[parameters == null ? 1
				: 1 + parameters.length];
		getParserFields[0] = ParserField.newString("path",
				"path to the local file");
		if (parameters != null)
			System.arraycopy(parameters, 0, getParserFields, 1,
					parameters.length);
		getParse = new Link(Method.GET, "parse local file", resourcePath,
				getParserFields);
		put = new Link(Method.PUT, "upload", resourcePath, parameters);
		returnedFields = parser.getFields();
	}
}
