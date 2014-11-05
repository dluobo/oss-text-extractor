/**   
 * License Agreement for OpenSearchServer
 *
 * Copyright (C) 2013 Emmanuel Keller / Jaeksoft
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

import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.poi.hdgf.extractor.VisioTextExtractor;
import org.apache.poi.hpsf.SummaryInformation;

import com.opensearchserver.textextractor.ParserAbstract;
import com.opensearchserver.textextractor.ParserDocument;
import com.opensearchserver.textextractor.ParserField;

public class Visio extends ParserAbstract {

	final protected static ParserField TITLE = ParserField.newString("title",
			"The title of the document");

	final protected static ParserField AUTHOR = ParserField.newString("author",
			"The name of the author");

	final protected static ParserField CREATION_DATE = ParserField.newDate(
			"creation_date", null);

	final protected static ParserField MODIFICATION_DATE = ParserField.newDate(
			"modification_date", null);

	final protected static ParserField KEYWORDS = ParserField.newString(
			"keywords", null);

	final protected static ParserField SUBJECT = ParserField.newString(
			"subject", "The subject of the document");

	final protected static ParserField COMMENTS = ParserField.newString(
			"comments", null);

	final protected static ParserField CONTENT = ParserField.newString(
			"content", "The content of the document");

	final protected static ParserField LANG_DETECTION = ParserField.newString(
			"lang_detection", "Detection of the language");

	final protected static ParserField[] FIELDS = { TITLE, AUTHOR,
			CREATION_DATE, MODIFICATION_DATE, KEYWORDS, SUBJECT, CONTENT,
			LANG_DETECTION };

	public Visio() {
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
	protected void parseContent(InputStream inputStream) throws Exception {
		VisioTextExtractor extractor = null;
		try {
			extractor = new VisioTextExtractor(inputStream);
			SummaryInformation info = extractor.getSummaryInformation();

			if (info != null) {
				metas.add(TITLE, info.getTitle());
				metas.add(AUTHOR, info.getAuthor());
				metas.add(SUBJECT, info.getSubject());
				metas.add(CREATION_DATE, info.getCreateDateTime());
				metas.add(MODIFICATION_DATE, info.getLastSaveDateTime());
				metas.add(CONTENT, info.getKeywords());
				metas.add(COMMENTS, info.getComments());
			}
			String[] texts = extractor.getAllText();
			if (texts == null)
				return;
			ParserDocument result = getNewParserDocument();
			for (String text : texts)
				result.add(CONTENT, text);
			result.add(LANG_DETECTION, languageDetection(CONTENT, 10000));
		} finally {
			if (extractor != null)
				IOUtils.closeQuietly(extractor);
		}
	}
}
