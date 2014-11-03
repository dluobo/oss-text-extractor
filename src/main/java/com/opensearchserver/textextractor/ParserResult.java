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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_EMPTY)
public class ParserResult {

	public long time_elapsed;

	public HashMap<String, List<Object>> metas;

	public List<HashMap<String, List<Object>>> documents;

	ParserResult() {
		time_elapsed = System.currentTimeMillis();
		documents = null;
		metas = null;
	}

	void done(ParserDocument parserMetas, List<ParserDocument> parserDocuments) {
		// Calculate the time elapsed
		time_elapsed = System.currentTimeMillis() - time_elapsed;

		// Extract the metas
		metas = parserMetas == null ? null : parserMetas.fields;

		// Extract the documents found
		if (parserDocuments != null) {
			documents = new ArrayList<HashMap<String, List<Object>>>(
					parserDocuments.size());
			for (ParserDocument parserDocument : parserDocuments)
				documents.add(parserDocument.fields);
		}
	}
}
