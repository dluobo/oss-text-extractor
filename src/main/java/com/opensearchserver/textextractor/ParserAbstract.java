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

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.io.IOUtils;

public abstract class ParserAbstract {

	private final List<ParserDocument> documents;
	protected MultivaluedMap<String, String> parameters;

	protected ParserAbstract() {
		documents = new ArrayList<ParserDocument>(0);
		parameters = null;
	}

	protected ParserDocument getNewParserDocument() {
		ParserDocument document = new ParserDocument();
		documents.add(document);
		return document;
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
	 * @throws Exception
	 *             Read a document and fill the ParserDocument list.
	 * 
	 * @param inputStream
	 * @throws IOException
	 * @throws
	 */
	protected abstract void parseContent(InputStream inputStream)
			throws Exception;

	/**
	 * Read a document and fill the ParserDocument list.
	 * 
	 * @param file
	 * @throws IOException
	 */
	protected void parseContent(File file) throws Exception {
		InputStream is = null;
		try {
			is = new FileInputStream(file);
			parseContent(is);
		} finally {
			if (is != null)
				IOUtils.closeQuietly(is);
		}
	}

	final ParserResult doParsing(UriInfo uriInfo, InputStream inputStream)
			throws Exception {
		setUriParameters(uriInfo);
		ParserResult result = new ParserResult();
		parseContent(inputStream);
		result.done(documents);
		return result;
	}

	final ParserResult doParsing(UriInfo uriInfo, File file) throws Exception {
		setUriParameters(uriInfo);
		ParserResult result = new ParserResult();
		parseContent(file);
		result.done(documents);
		return result;
	}

	final private void setUriParameters(UriInfo uriInfo) {
		parameters = uriInfo == null ? null : uriInfo.getQueryParameters();
	}

	final List<String> getParameters(ParserField parserField) {
		if (parameters == null)
			return null;
		return parameters.get(parserField.name);
	}

	/**
	 * Submit the content if of a field to language detection
	 * 
	 * @param source
	 *            The field to submit
	 * @param maxLength
	 *            The maximum number of characters
	 * @return
	 */
	protected final String languageDetection(ParserField source, int maxLength) {
		StringBuilder sb = new StringBuilder();
		for (ParserDocument document : documents) {
			List<Object> objectList = document.fields.get(source.name);
			for (Object object : objectList) {
				if (object == null)
					continue;
				sb.append(object.toString());
				sb.append(' ');
				if (sb.length() > maxLength)
					Language.quietDetect(sb.toString(), maxLength);
			}
		}
		return Language.quietDetect(sb.toString(), maxLength);
	}

}
