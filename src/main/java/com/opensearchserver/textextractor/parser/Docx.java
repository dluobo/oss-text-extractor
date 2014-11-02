/**   
 * License Agreement for OpenSearchServer
 *
 * Copyright (C) 2010-2013 Emmanuel Keller / Jaeksoft
 * 
 * http://www.open-search-server.com
 * 
 * This file is part of OpenSearchServer.
 *
 * OpenSearchServer is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 * OpenSearchServer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with OpenSearchServer. 
 *  If not, see <http://www.gnu.org/licenses/>.
 **/

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

	final protected static ParserField[] FIELDS = { TITLE, CREATOR,
			DESCRIPTION, KEYWORDS, SUBJECT, CONTENT };

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

		} finally {
			IOUtils.closeQuietly(word);
		}
	}
}
