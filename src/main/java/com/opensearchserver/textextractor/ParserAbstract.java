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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.io.IOUtils;

import com.opensearchserver.textextractor.util.Language;

public abstract class ParserAbstract {

	protected final ParserDocument metas;
	private final List<ParserDocument> documents;
	protected MultivaluedMap<String, String> parameters;

	protected ParserAbstract() {
		documents = new ArrayList<ParserDocument>(0);
		metas = new ParserDocument();
		parameters = null;
	}

	protected ParserDocument getNewParserDocument() {
		ParserDocument document = new ParserDocument();
		documents.add(document);
		return document;
	}

	protected String getParameterValue(ParserField param, int position) {
		if (parameters == null)
			return null;
		List<String> values = parameters.get(param.name);
		if (values == null)
			return null;
		if (position >= values.size())
			return null;
		return values.get(position);
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

	protected final static File createTempFile(InputStream inputStream,
			String extension) throws IOException {
		File tempFile = File.createTempFile("oss-text-extractor", extension);
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(tempFile);
			IOUtils.copy(inputStream, fos);
			fos.close();
			fos = null;
			return tempFile;
		} finally {
			if (fos != null)
				IOUtils.closeQuietly(fos);
		}
	}

	final ParserResult doParsing(UriInfo uriInfo, InputStream inputStream)
			throws Exception {
		setUriParameters(uriInfo);
		ParserResult result = new ParserResult();
		parseContent(inputStream);
		result.done(metas, documents);
		return result;
	}

	final ParserResult doParsing(UriInfo uriInfo, File file) throws Exception {
		setUriParameters(uriInfo);
		ParserResult result = new ParserResult();
		parseContent(file);
		result.done(metas, documents);
		return result;
	}

	final private void setUriParameters(UriInfo uriInfo) {
		parameters = uriInfo == null ? null : uriInfo.getQueryParameters();
	}

	/**
	 * Submit the content of a field to language detection. It checks all the
	 * document.
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
			if (objectList == null)
				continue;
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

	/**
	 * Submit the content if of a field to language detection.
	 * 
	 * @param document
	 * @param source
	 * @param maxLength
	 * @return
	 */
	protected final String languageDetection(ParserDocument document,
			ParserField source, int maxLength) {
		StringBuilder sb = new StringBuilder();
		List<Object> objectList = document.fields.get(source.name);
		if (objectList == null)
			return null;
		for (Object object : objectList) {
			if (object == null)
				continue;
			sb.append(object.toString());
			sb.append(' ');
			if (sb.length() > maxLength)
				Language.quietDetect(sb.toString(), maxLength);
		}
		return Language.quietDetect(sb.toString(), maxLength);
	}

}
