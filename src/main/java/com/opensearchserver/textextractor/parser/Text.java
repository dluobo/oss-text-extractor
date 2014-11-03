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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

import com.ibm.icu.text.CharsetDetector;
import com.ibm.icu.text.CharsetMatch;
import com.opensearchserver.textextractor.ParserAbstract;
import com.opensearchserver.textextractor.ParserDocument;
import com.opensearchserver.textextractor.ParserField;

public class Text extends ParserAbstract {

	final protected static ParserField CONTENT = ParserField.newString(
			"content", "The content of the document");

	final protected static ParserField LANG_DETECTION = ParserField.newString(
			"lang_detection", "Detection of the language");

	final protected static ParserField CHARSET_DETECTION = ParserField
			.newString("charset_detection", "Detection of the charset");

	final protected static ParserField[] FIELDS = { CONTENT, LANG_DETECTION,
			CHARSET_DETECTION };

	public Text() {
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
		// Trying to detect the CHARSET of the stream
		CharsetDetector detector = new CharsetDetector();
		BufferedInputStream bis = null;
		try {
			bis = new BufferedInputStream(inputStream);
			detector.setText(bis);
			CharsetMatch match = detector.detect();
			ParserDocument result = getNewParserDocument();
			String content = null;
			if (match != null) {
				content = match.getString();
				result.add(CHARSET_DETECTION, match.getName());
			} else {
				bis.reset();
				content = IOUtils.toString(bis);
			}
			result.add(CONTENT, content);
			result.add(LANG_DETECTION, languageDetection(CONTENT, 10000));
		} finally {
			IOUtils.closeQuietly(bis);
		}
	}

}
