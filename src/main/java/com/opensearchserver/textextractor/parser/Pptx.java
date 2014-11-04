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

import java.io.File;
import java.io.InputStream;

import org.apache.poi.POIXMLProperties.CoreProperties;
import org.apache.poi.xslf.XSLFSlideShow;
import org.apache.poi.xslf.extractor.XSLFPowerPointExtractor;
import org.apache.poi.xslf.usermodel.DrawingParagraph;
import org.apache.poi.xslf.usermodel.DrawingTextBody;
import org.apache.poi.xslf.usermodel.DrawingTextPlaceholder;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFCommentAuthors;
import org.apache.poi.xslf.usermodel.XSLFComments;
import org.apache.poi.xslf.usermodel.XSLFCommonSlideData;
import org.apache.poi.xslf.usermodel.XSLFNotes;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFSlideLayout;
import org.apache.poi.xslf.usermodel.XSLFSlideMaster;
import org.openxmlformats.schemas.presentationml.x2006.main.CTComment;
import org.openxmlformats.schemas.presentationml.x2006.main.CTCommentAuthor;

import com.opensearchserver.textextractor.ParserAbstract;
import com.opensearchserver.textextractor.ParserDocument;
import com.opensearchserver.textextractor.ParserField;

public class Pptx extends ParserAbstract {

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

	final protected static ParserField CREATION_DATE = ParserField.newDate(
			"creation_date", null);

	final protected static ParserField MODIFICATION_DATE = ParserField.newDate(
			"modification_date", null);

	final protected static ParserField SLIDES = ParserField.newString("slides",
			null);

	final protected static ParserField MASTER = ParserField.newString("master",
			null);

	final protected static ParserField NOTES = ParserField.newString("notes",
			null);

	final protected static ParserField COMMENTS = ParserField.newString(
			"comments", null);

	final protected static ParserField LANG_DETECTION = ParserField.newString(
			"lang_detection", "Detection of the language");

	final protected static ParserField[] FIELDS = { TITLE, CREATOR,
			DESCRIPTION, KEYWORDS, SUBJECT, CREATION_DATE, MODIFICATION_DATE,
			SLIDES, MASTER, NOTES, COMMENTS, LANG_DETECTION };

	public Pptx() {
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
		File tempFile = ParserAbstract.createTempFile(inputStream, "pptx");
		try {
			parseContent(tempFile);
		} finally {
			tempFile.delete();
		}
	}

	@Override
	protected void parseContent(File file) throws Exception {

		XSLFSlideShow pptSlideShow = new XSLFSlideShow(file.getAbsolutePath());
		XMLSlideShow slideshow = new XMLSlideShow(pptSlideShow.getPackage());

		// Extract metadata
		XSLFPowerPointExtractor poiExtractor = null;
		try {
			poiExtractor = new XSLFPowerPointExtractor(slideshow);
			CoreProperties info = poiExtractor.getCoreProperties();
			if (info != null) {
				metas.add(TITLE, info.getTitle());
				metas.add(CREATOR, info.getCreator());
				metas.add(SUBJECT, info.getSubject());
				metas.add(DESCRIPTION, info.getDescription());
				metas.add(KEYWORDS, info.getKeywords());
				metas.add(CREATION_DATE, info.getCreated());
				metas.add(MODIFICATION_DATE, info.getModified());
			}
		} finally {
			poiExtractor.close();
		}
		extractSides(slideshow);
	}

	/**
	 * Declined from XSLFPowerPointExtractor.java
	 */
	private String extractText(XSLFCommonSlideData data,
			boolean skipPlaceholders) {
		StringBuilder sb = new StringBuilder();
		for (DrawingTextBody textBody : data.getDrawingText()) {
			if (skipPlaceholders && textBody instanceof DrawingTextPlaceholder) {
				DrawingTextPlaceholder ph = (DrawingTextPlaceholder) textBody;
				if (!ph.isPlaceholderCustom()) {
					// Skip non-customised placeholder text
					continue;
				}
			}

			for (DrawingParagraph p : textBody.getParagraphs()) {
				sb.append(p.getText());
				sb.append("\n");
			}
		}
		return sb.toString();
	}

	/**
	 * Declined from XSLFPowerPointExtractor.java
	 * 
	 * @param pptSlideShow
	 */
	private void extractSides(XMLSlideShow slideshow) {

		XSLFSlide[] slides = (XSLFSlide[]) slideshow.getSlides();
		XSLFCommentAuthors commentAuthors = slideshow.getCommentAuthors();

		for (XSLFSlide slide : slides) {

			// One document per slide
			ParserDocument result = getNewParserDocument();

			XSLFNotes notes = slide.getNotes();
			XSLFComments comments = slide.getComments();
			XSLFSlideLayout layout = slide.getSlideLayout();
			XSLFSlideMaster master = layout.getSlideMaster();

			// TODO Do the slide's name
			// (Stored in docProps/app.xml)

			// Do the slide's text
			result.add(SLIDES, extractText(slide.getCommonSlideData(), false));
			result.add(LANG_DETECTION, languageDetection(SLIDES, 10000));

			// If requested, get text from the master and it's layout
			if (layout != null) {
				result.add(MASTER,
						extractText(layout.getCommonSlideData(), true));
			}
			if (master != null) {
				result.add(MASTER,
						extractText(master.getCommonSlideData(), true));
			}

			// If the slide has comments, do those too
			if (comments != null) {
				for (CTComment comment : comments.getCTCommentsList()
						.getCmList()) {
					StringBuilder sbComment = new StringBuilder();
					// Do the author if we can
					if (commentAuthors != null) {
						CTCommentAuthor author = commentAuthors
								.getAuthorById(comment.getAuthorId());
						if (author != null) {
							sbComment.append(author.getName());
							sbComment.append(": ");
						}
					}

					// Then the comment text, with a new line afterwards
					sbComment.append(comment.getText());
					sbComment.append("\n");
					if (sbComment.length() > 0)
						result.add(COMMENTS, sbComment.toString());
				}
			}

			// Do the notes if requested
			if (notes != null) {
				result.add(NOTES,
						extractText(notes.getCommonSlideData(), false));
			}
		}
	}
}
