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
import org.apache.poi.hpsf.SummaryInformation;
import org.apache.poi.hwpf.OldWordFileFormatException;
import org.apache.poi.hwpf.extractor.Word6Extractor;
import org.apache.poi.hwpf.extractor.WordExtractor;

import com.opensearchserver.textextractor.ParserAbstract;
import com.opensearchserver.textextractor.ParserDocument;
import com.opensearchserver.textextractor.ParserField;

public class Doc extends ParserAbstract {

	final protected static ParserField TITLE = ParserField.newString("title",
			null);

	final protected static ParserField AUTHOR = ParserField.newString("author",
			"The name of the author");

	final protected static ParserField SUBJECT = ParserField.newString(
			"subject", "The subject of the document");

	final protected static ParserField CONTENT = ParserField.newString(
			"content", "The content of the document");

	final protected static ParserField LANG_DETECTION = ParserField.newString(
			"lang_detection", "Detection of the language");

	final protected static ParserField[] FIELDS = { TITLE, AUTHOR, SUBJECT,
			CONTENT, LANG_DETECTION };

	public Doc() {
	}

	@Override
	protected ParserField[] getParameters() {
		return null;
	}

	@Override
	protected ParserField[] getFields() {
		return FIELDS;
	}

	private void currentWordExtraction(ParserDocument document,
			InputStream inputStream) throws IOException {
		WordExtractor word = null;

		try {
			word = new WordExtractor(inputStream);

			SummaryInformation info = word.getSummaryInformation();
			if (info != null) {
				document.add(TITLE, info.getTitle());
				document.add(AUTHOR, info.getAuthor());
				document.add(SUBJECT, info.getSubject());
			}

			String[] paragraphes = word.getParagraphText();
			for (String paragraph : paragraphes)
				document.add(CONTENT, paragraph);
		} finally {
			IOUtils.closeQuietly(word);
		}
	}

	private void oldWordExtraction(ParserDocument document,
			InputStream inputStream) throws IOException {
		Word6Extractor word6 = null;
		try {
			word6 = new Word6Extractor(inputStream);
			SummaryInformation si = word6.getSummaryInformation();
			if (si != null) {
				document.add(TITLE, si.getTitle());
				document.add(AUTHOR, si.getAuthor());
				document.add(SUBJECT, si.getSubject());
			}

			@SuppressWarnings("deprecation")
			String[] paragraphes = word6.getParagraphText();
			for (String paragraph : paragraphes)
				document.add(CONTENT, paragraph);
		} finally {
			IOUtils.closeQuietly(word6);
		}
	}

	@Override
	public void parseContent(InputStream inputStream) throws IOException {
		ParserDocument document = getNewParserDocument();
		try {
			currentWordExtraction(document, inputStream);
			document.add(LANG_DETECTION, languageDetection(CONTENT, 10000));
		} catch (OldWordFileFormatException e) {
			oldWordExtraction(document, inputStream);
		}
	}

}
