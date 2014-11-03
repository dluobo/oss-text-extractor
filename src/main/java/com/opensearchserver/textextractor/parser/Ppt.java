/**   
 * License Agreement for OpenSearchServer
 *
 * Copyright (C) 2008-2013 Emmanuel Keller / Jaeksoft
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

import org.apache.poi.hslf.model.Slide;
import org.apache.poi.hslf.model.TextRun;
import org.apache.poi.hslf.record.TextHeaderAtom;
import org.apache.poi.hslf.usermodel.SlideShow;

import com.opensearchserver.textextractor.ParserAbstract;
import com.opensearchserver.textextractor.ParserDocument;
import com.opensearchserver.textextractor.ParserField;

public class Ppt extends ParserAbstract {

	final protected static ParserField TITLE = ParserField.newString("title",
			"The title of the document");

	final protected static ParserField BODY = ParserField.newString("body",
			"The body of the document");

	final protected static ParserField NOTES = ParserField.newString("notes",
			null);

	final protected static ParserField OTHER = ParserField.newString("other",
			null);

	final protected static ParserField LANG_DETECTION = ParserField.newString(
			"lang_detection", "Detection of the language");

	final protected static ParserField[] FIELDS = { TITLE, BODY, NOTES, OTHER,
			LANG_DETECTION };

	public Ppt() {
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

		SlideShow ppt = new SlideShow(inputStream);

		Slide[] slides = ppt.getSlides();
		for (Slide slide : slides) {
			ParserDocument document = getNewParserDocument();
			TextRun[] textRuns = slide.getTextRuns();
			for (TextRun textRun : textRuns) {
				ParserField parserField;
				switch (textRun.getRunType()) {
				case TextHeaderAtom.TITLE_TYPE:
				case TextHeaderAtom.CENTER_TITLE_TYPE:
					parserField = TITLE;
					break;
				case TextHeaderAtom.NOTES_TYPE:
					parserField = NOTES;
					break;
				case TextHeaderAtom.BODY_TYPE:
				case TextHeaderAtom.CENTRE_BODY_TYPE:
				case TextHeaderAtom.HALF_BODY_TYPE:
				case TextHeaderAtom.QUARTER_BODY_TYPE:
					parserField = BODY;
					break;
				case TextHeaderAtom.OTHER_TYPE:
				default:
					parserField = OTHER;
					break;
				}
				document.add(parserField, textRun.getText());
			}
			document.add(LANG_DETECTION,
					languageDetection(document, BODY, 10000));
		}

	}
}
