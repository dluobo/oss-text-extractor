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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;

public abstract class ParserAbstract {

	private final List<ParserDocument> documents;

	protected ParserAbstract() {
		documents = new ArrayList<ParserDocument>(0);
	}

	protected ParserDocument getNewParserDocument() {
		ParserDocument document = new ParserDocument();
		documents.add(document);
		return document;
	}

	public List<ParserDocument> getDocuments() {
		return documents;
	}

	/**
	 * The parameters of the parser
	 * 
	 * @return
	 */
	protected abstract ParserField[] getParameters();

	/**
	 * The fields returned by this parser
	 * 
	 * @return
	 */
	protected abstract ParserField[] getFields();

	/**
	 * Read a document a populate the ParserDocument list.
	 * 
	 * @param inputStream
	 * @throws IOException
	 */
	protected abstract void parseContent(InputStream inputStream)
			throws IOException;

	protected void parseContent(File file) throws IOException {
		InputStream is = null;
		try {
			is = new FileInputStream(file);
			parseContent(is);
		} finally {
			if (is != null)
				IOUtils.closeQuietly(is);
		}
	}

	// TODO implement
	public void setParameter(String parameter, String value) {
	}

}
