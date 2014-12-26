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

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import com.opensearchserver.textextractor.ParserAbstract;
import com.opensearchserver.textextractor.ParserList;
import com.opensearchserver.textextractor.ParserResult;
import com.opensearchserver.textextractor.parser.Audio;
import com.opensearchserver.textextractor.parser.Doc;
import com.opensearchserver.textextractor.parser.Docx;
import com.opensearchserver.textextractor.parser.Image;
import com.opensearchserver.textextractor.parser.Markdown;
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

	static final String DEFAULT_TEST_STRING = "osstextextractor";

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

	/**
	 * Check if the given string is present in a multivalued map
	 * 
	 * @param map
	 * @param text
	 * @return
	 */
	protected boolean checkText(Map<String, List<Object>> map, String text) {
		if (map == null)
			return false;
		for (Map.Entry<String, List<Object>> entry : map.entrySet())
			for (Object object : entry.getValue())
				if (object.toString().contains(text))
					return true;
		return false;
	}

	/**
	 * Check if the given string is present in the result
	 * 
	 * @param result
	 * @param text
	 */
	protected void checkText(ParserResult result, String text) {
		if (text == null)
			return;
		for (Map<String, List<Object>> map : result.documents)
			if (checkText(map, text))
				return;
		if (checkText(result.metas, text))
			return;
		logger.severe("Text " + text + " not found");
		assert (false);
	}

	/**
	 * Build a map of parameters using key/value pairs
	 * 
	 * @param keyValueParams
	 * @return
	 */
	protected MultivaluedMap<String, String> getParameters(
			String... keyValueParams) {
		if (keyValueParams == null)
			return null;
		MultivaluedHashMap<String, String> parameters = null;
		parameters = new MultivaluedHashMap<>();
		for (int i = 0; i < keyValueParams.length; i += 2)
			parameters.add(keyValueParams[i], keyValueParams[i + 1]);
		return parameters;
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
			String fileName, String testString, String... keyValueParams)
			throws Exception {
		logger.info("Testing " + className);
		MultivaluedMap<String, String> parameters = getParameters(keyValueParams);
		ParserAbstract parser = createRegisterInstance(className);
		parser.doParsing(parameters, getStream(fileName));
		parser = createRegisterInstance(className);
		ParserResult parserResult = parser.doParsing(parameters,
				getTempFile(fileName));
		assert (parserResult != null);
		checkText(parserResult, testString);
	}

	final String AUDIO_TEST_STRING = "opensearchserver";

	public void testAudioFlag() throws Exception {
		doTest(Audio.class, "file.flac", AUDIO_TEST_STRING, "format", "flac");
	}

	public void testAudioM4a() throws Exception {
		doTest(Audio.class, "file.m4a", DEFAULT_TEST_STRING, "format", "m4a");
	}

	public void testAudioMp3() throws Exception {
		doTest(Audio.class, "file.mp3", DEFAULT_TEST_STRING, "format", "mp3");
	}

	public void testAudioOgg() throws Exception {
		doTest(Audio.class, "file.ogg", AUDIO_TEST_STRING, "format", "ogg");
	}

	public void testAudioWav() throws Exception {
		doTest(Audio.class, "file.wav", null, "format", "wav");
	}

	public void testAudioWma() throws Exception {
		doTest(Audio.class, "file.wma", AUDIO_TEST_STRING, "format", "wma");
	}

	public void testDoc() throws Exception {
		doTest(Doc.class, "file.doc", DEFAULT_TEST_STRING);
	}

	public void testDocx() throws Exception {
		doTest(Docx.class, "file.docx", DEFAULT_TEST_STRING);
	}

	public void testImageGif() throws Exception {
		doTest(Image.class, "file.gif", DEFAULT_TEST_STRING);
	}

	public void testImageJpg() throws Exception {
		doTest(Image.class, "file.jpg", DEFAULT_TEST_STRING);
	}

	public void testImagePng() throws Exception {
		doTest(Image.class, "file.png", DEFAULT_TEST_STRING);
	}

	public void testImageTiff() throws Exception {
		doTest(Image.class, "file.tiff", null);
	}

	public void testMarkdown() throws Exception {
		doTest(Markdown.class, "file.md", "extract data to be indexed");
	}

	public void testPdf() throws Exception {
		doTest(PdfBox.class, "file.pdf", DEFAULT_TEST_STRING);
	}

	public void testOdt() throws Exception {
		doTest(Odf.class, "file.odt", DEFAULT_TEST_STRING);
	}

	public void testOds() throws Exception {
		doTest(Odf.class, "file.ods", DEFAULT_TEST_STRING);
	}

	public void testOdp() throws Exception {
		doTest(Odf.class, "file.odp", DEFAULT_TEST_STRING);
	}

	public void testPpt() throws Exception {
		doTest(Ppt.class, "file.ppt", DEFAULT_TEST_STRING);
	}

	public void testPptx() throws Exception {
		doTest(Pptx.class, "file.pptx", DEFAULT_TEST_STRING);
	}

	public void testRtf() throws Exception {
		doTest(Rtf.class, "file.rtf", DEFAULT_TEST_STRING);
	}

	public void testText() throws Exception {
		doTest(Text.class, "file.txt", DEFAULT_TEST_STRING);
	}

	public void testXls() throws Exception {
		doTest(Xls.class, "file.xls", DEFAULT_TEST_STRING);
	}

	public void testXlsx() throws Exception {
		doTest(Xlsx.class, "file.xlsx", DEFAULT_TEST_STRING);
	}

}
