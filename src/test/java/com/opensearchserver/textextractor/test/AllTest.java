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
package com.opensearchserver.textextractor.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.ws.rs.core.MultivaluedMap;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import com.opensearchserver.textextractor.ParserAbstract;
import com.opensearchserver.textextractor.ParserList;
import com.opensearchserver.textextractor.ParserResult;
import com.opensearchserver.textextractor.parser.Doc;
import com.opensearchserver.textextractor.parser.Docx;
import com.opensearchserver.textextractor.parser.Odf;
import com.opensearchserver.textextractor.parser.PdfBox;
import com.opensearchserver.textextractor.parser.Ppt;
import com.opensearchserver.textextractor.parser.Pptx;
import com.opensearchserver.textextractor.parser.Rtf;
import com.opensearchserver.textextractor.parser.Text;
import com.opensearchserver.textextractor.parser.Xls;
import com.opensearchserver.textextractor.parser.Xlsx;

public class AllTest {

	static final Logger logger = Logger.getLogger(AllTest.class.getName());

	/**
	 * Check if the parser has been registered, and create the an instance.
	 * 
	 * @param className
	 * @return An instance
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	protected ParserAbstract createRegisterInstance(
			Class<? extends ParserAbstract> className)
			throws InstantiationException, IllegalAccessException {
		Class<? extends ParserAbstract> parserClass = ParserList
				.findParserClass(className.getSimpleName().toLowerCase());
		assert (parserClass != null);
		return parserClass.newInstance();
	}

	protected InputStream getStream(String fileName) {
		InputStream inputStream = getClass().getResourceAsStream(fileName);
		assert (inputStream != null);
		return inputStream;
	}

	protected File getTempFile(String fileName) throws IOException {
		File tempFile = File.createTempFile("oss_text_extractor", "."
				+ FilenameUtils.getExtension(fileName));
		FileOutputStream fos = new FileOutputStream(tempFile);
		InputStream inputStream = getStream(fileName);
		IOUtils.copy(inputStream, fos);
		return tempFile;
	}

	protected void checkText(ParserResult result, String text) {
		for (Map<String, List<Object>> map : result.documents)
			for (Map.Entry<String, List<Object>> entry : map.entrySet())
				for (Object object : entry.getValue())
					if (object.toString().contains(text))
						return;
		logger.severe("Text oss-text-extractor not found");
		assert (false);
	}

	/**
	 * Test inputstream and file parsing
	 * 
	 * @param className
	 * @param fileName
	 * @param parameters
	 * @throws Exception
	 */
	protected void doTest(Class<? extends ParserAbstract> className,
			String fileName, MultivaluedMap<String, String> parameters)
			throws Exception {
		logger.info("Testing " + className);
		ParserAbstract parser = createRegisterInstance(className);
		parser.doParsing(parameters, getStream(fileName));
		parser = createRegisterInstance(className);
		ParserResult parserResult = parser.doParsing(parameters,
				getTempFile(fileName));
		assert (parserResult != null);
		assert (parserResult.documents != null);
		assert (parserResult.documents.size() > 0);
	}

	public void testDoc() throws Exception {
		doTest(Doc.class, "file.doc", null);
	}

	public void testDocx() throws Exception {
		doTest(Docx.class, "file.docx", null);
	}

	public void testPdf() throws Exception {
		doTest(PdfBox.class, "file.pdf", null);
	}

	public void testOdt() throws Exception {
		doTest(Odf.class, "file.odt", null);
	}

	public void testOds() throws Exception {
		doTest(Odf.class, "file.ods", null);
	}

	public void testOdp() throws Exception {
		doTest(Odf.class, "file.odp", null);
	}

	public void testPpt() throws Exception {
		doTest(Ppt.class, "file.ppt", null);
	}

	public void testPptx() throws Exception {
		doTest(Pptx.class, "file.pptx", null);
	}

	public void testRtf() throws Exception {
		doTest(Rtf.class, "file.rtf", null);
	}

	public void testText() throws Exception {
		doTest(Text.class, "file.txt", null);
	}

	public void testXls() throws Exception {
		doTest(Xls.class, "file.xls", null);
	}

	public void testXlsx() throws Exception {
		doTest(Xlsx.class, "file.xlsx", null);
	}

}
