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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.exceptions.CryptographyException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.encryption.BadSecurityHandlerException;
import org.apache.pdfbox.pdmodel.encryption.StandardDecryptionMaterial;
import org.apache.pdfbox.util.PDFTextStripper;

import com.opensearchserver.textextractor.ParserAbstract;
import com.opensearchserver.textextractor.ParserDocument;
import com.opensearchserver.textextractor.ParserField;
import com.opensearchserver.textextractor.ParserList;

public class PdfBox extends ParserAbstract {

	final protected static ParserField TITLE = ParserField.newString("title",
			"The title of the Word document");

	final protected static ParserField AUTHOR = ParserField.newString("author",
			"The name of the author");

	final protected static ParserField SUBJECT = ParserField.newString(
			"subject", "The subject of the document");

	final protected static ParserField CONTENT = ParserField.newString(
			"content", "The content of the document");

	final protected static ParserField PRODUCER = ParserField.newString(
			"producer", "The producer of the document");

	final protected static ParserField KEYWORDS = ParserField.newString(
			"keywords", "The keywords of the document");

	final protected static ParserField CREATION_DATE = ParserField.newDate(
			"creation_date", null);

	final protected static ParserField MODIFICATION_DATE = ParserField.newDate(
			"modification_date", null);

	final protected static ParserField LANGUAGE = ParserField.newString(
			"language", null);

	final protected static ParserField NUMBER_OF_PAGES = ParserField
			.newInteger("number_of_pages", null);

	final protected static ParserField CHARACTER_COUNT = ParserField
			.newInteger("character_count", null);

	final protected static ParserField[] FIELDS = { TITLE, AUTHOR, SUBJECT,
			CONTENT, PRODUCER, KEYWORDS, CREATION_DATE, MODIFICATION_DATE,
			LANGUAGE, NUMBER_OF_PAGES };

	static {
		ParserList.register(PdfBox.class);
	}

	public PdfBox() {
	}

	private Calendar getCreationDate(PDDocumentInformation pdfInfo) {
		try {
			return pdfInfo.getCreationDate();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	private Calendar getModificationDate(PDDocumentInformation pdfInfo) {
		try {
			return pdfInfo.getCreationDate();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	private Date getDate(Calendar cal) {
		if (cal == null)
			return null;
		return cal.getTime();
	}

	private void extractMetaData(ParserDocument document, PDDocument pdf)
			throws IOException {
		PDDocumentInformation info = pdf.getDocumentInformation();
		if (info != null) {
			document.add(TITLE, info.getTitle());
			document.add(SUBJECT, info.getSubject());
			document.add(AUTHOR, info.getAuthor());
			document.add(PRODUCER, info.getProducer());
			document.add(KEYWORDS, info.getKeywords());
			document.add(CREATION_DATE, getDate(getCreationDate(info)));
			document.add(MODIFICATION_DATE, getModificationDate(info));
		}
		int pages = pdf.getNumberOfPages();
		document.add(NUMBER_OF_PAGES, pages);
		PDDocumentCatalog catalog = pdf.getDocumentCatalog();
		if (catalog != null)
			document.add(LANGUAGE, catalog.getLanguage());
	}

	/**
	 * Extract text content using PDFBox
	 * 
	 * @param result
	 * @param pdf
	 * @throws IOException
	 */
	private int extractTextContent(ParserDocument document, PDDocument pdf)
			throws IOException {
		PDFTextStripper stripper = new PDFTextStripper();
		String text = stripper.getText(pdf);
		if (StringUtils.isEmpty(text))
			return 0;
		document.add(CONTENT, text);
		return text.length();
	}

	private void parseContent(PDDocument pdf) throws IOException {
		try {
			if (pdf.isEncrypted())
				pdf.openProtection(new StandardDecryptionMaterial(""));
			ParserDocument document = getNewParserDocument();
			extractMetaData(document, pdf);
			document.add(CHARACTER_COUNT, extractTextContent(document, pdf));
		} catch (BadSecurityHandlerException e) {
			throw new IOException(e);
		} catch (CryptographyException e) {
			throw new IOException(e);
		} finally {
			if (pdf != null)
				pdf.close();
		}
	}

	@Override
	public void parseContent(InputStream inputStream) throws IOException {
		parseContent(PDDocument.loadNonSeq(inputStream, null));
	}

	@Override
	public void parseContent(File file) throws IOException {
		parseContent(PDDocument.loadNonSeq(file, null));
	}

	@Override
	protected ParserField[] getParameters() {
		return null;
	}

	@Override
	protected ParserField[] getFields() {
		return FIELDS;
	}

}
