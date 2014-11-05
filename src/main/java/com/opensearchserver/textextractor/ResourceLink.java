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

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.opensearchserver.textextractor.Link.Method;

@JsonInclude(Include.NON_EMPTY)
public class ResourceLink {

	@JsonProperty("_link")
	public final Link get;

	public ResourceLink(String resourcePath) {
		get = new Link(Method.GET, "describe", resourcePath);
	}

	public static String join(String... path) {
		String join = StringUtils.join(path, '/');
		path = StringUtils.split(join, '/');
		return StringUtils.join(path, '/');
	}

}