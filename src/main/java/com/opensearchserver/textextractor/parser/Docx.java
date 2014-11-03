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
package com.opensearchserver.textextractor.parser;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.poi.POIXMLProperties.CoreProperties;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import com.opensearchserver.textextractor.ParserAbstract;
import com.opensearchserver.textextractor.ParserDocument;
import com.opensearchserver.textextractor.ParserField;

public class Docx extends ParserAbstract {

	final protected static ParserField TITLE = ParserField.newString("title",
			"The title of the document");

	final protected static ParserField CREATOR = ParserField.newString(
			"creator", "The name of the creator");

	final protected static ParserField DESCRIPTION = ParserField.newString(
			"description", null);

	final protected static ParserField KEYWORDS = ParserField.newString(
			"keywords", null);

	final protected static ParserField SUBJECT = ParserField.newString(
			"subject", "The subject of the document");

	final protected static ParserField CONTENT = ParserField.newString(
			"content", "The content of the document");

	final protected static ParserField LANG_DETECTION = ParserField.newString(
			"lang_detection", "Detection of the language");

	final protected static ParserField[] FIELDS = { TITLE, CREATOR,
			DESCRIPTION, KEYWORDS, SUBJECT, CONTENT, LANG_DETECTION };

	public Docx() {
	}

	@Override
	protected ParserField[] getParameters() {
		return null;
	}

	@Override
	protected ParserField[] getFields() {
		return FIELDS;
	}

	@Override
	protected void parseContent(InputStream inputStream) throws IOException {

		ParserDocument parserDocument = getNewParserDocument();

		XWPFDocument document = new XWPFDocument(inputStream);
		XWPFWordExtractor word = null;
		try {
			word = new XWPFWordExtractor(document);

			CoreProperties info = word.getCoreProperties();
			if (info != null) {
				parserDocument.add(TITLE, info.getTitle());
				parserDocument.add(CREATOR, info.getCreator());
				parserDocument.add(SUBJECT, info.getSubject());
				parserDocument.add(DESCRIPTION, info.getDescription());
				parserDocument.add(KEYWORDS, info.getKeywords());
			}
			parserDocument.add(CONTENT, word.getText());
			parserDocument.add(LANG_DETECTION,
					languageDetection(CONTENT, 10000));
		} finally {
			IOUtils.closeQuietly(word);
		}
	}
}
