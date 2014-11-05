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
package com.opensearchserver.textextractor;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.opensearchserver.textextractor.parser.Audio;
import com.opensearchserver.textextractor.parser.Doc;
import com.opensearchserver.textextractor.parser.Docx;
import com.opensearchserver.textextractor.parser.Image;
import com.opensearchserver.textextractor.parser.Odf;
import com.opensearchserver.textextractor.parser.PdfBox;
import com.opensearchserver.textextractor.parser.Ppt;
import com.opensearchserver.textextractor.parser.Pptx;
import com.opensearchserver.textextractor.parser.Rtf;
import com.opensearchserver.textextractor.parser.Text;
import com.opensearchserver.textextractor.parser.Xls;
import com.opensearchserver.textextractor.parser.Xlsx;

public class ParserList {

	private final static ReadWriteLock rwl = new ReentrantReadWriteLock();

	private final static Map<String, Class<? extends ParserAbstract>> parsers;

	static {
		parsers = new LinkedHashMap<String, Class<? extends ParserAbstract>>();

		register(Audio.class);
		register(Doc.class);
		register(Docx.class);
		register(Image.class);
		register(Odf.class);
		register(PdfBox.class);
		register(Ppt.class);
		register(Pptx.class);
		register(Rtf.class);
		register(Text.class);
		register(Xls.class);
		register(Xlsx.class);
	}

	public final static void register(
			Class<? extends ParserAbstract> parserClass) {
		Lock l = rwl.writeLock();
		l.lock();
		try {
			parsers.put(parserClass.getSimpleName().toLowerCase(), parserClass);
		} finally {
			l.unlock();
		}
	}

	public final static Class<? extends ParserAbstract> findParserClass(
			String parserName) {
		Lock l = rwl.readLock();
		l.lock();
		try {
			return parsers.get(parserName);
		} finally {
			l.unlock();
		}
	}

	public final static Set<String> getList() {
		Lock l = rwl.readLock();
		l.lock();
		try {
			return parsers.keySet();
		} finally {
			l.unlock();
		}
	}

}
