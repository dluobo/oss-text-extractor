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
import java.io.InputStream;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import com.opensearchserver.textextractor.ParserAbstract;
import com.opensearchserver.textextractor.ParserDocument;
import com.opensearchserver.textextractor.ParserField;
import com.opensearchserver.textextractor.util.ImagePHash;

public class Image extends ParserAbstract {

	final protected static ParserField WIDTH = ParserField.newInteger("width",
			"Width of the image in pixels");

	final protected static ParserField HEIGHT = ParserField.newInteger(
			"height", "Height of the image in pixels");

	final protected static ParserField FORMAT = ParserField.newString("format",
			"The detected format");

	final protected static ParserField PHASH = ParserField.newString("phash",
			"Perceptual Hash");

	final protected static ParserField[] FIELDS = { WIDTH, HEIGHT, FORMAT,
			PHASH };

	public Image() {
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
	protected void parseContent(File file) throws Exception {
		ImagePHash imgPhash = new ImagePHash();
		ImageInputStream in = ImageIO.createImageInputStream(file);
		try {
			final Iterator<ImageReader> readers = ImageIO.getImageReaders(in);
			if (readers.hasNext()) {
				ParserDocument result = getNewParserDocument();
				ImageReader reader = readers.next();
				try {
					reader.setInput(in);
					result.add(WIDTH, reader.getWidth(0));
					result.add(HEIGHT, reader.getHeight(0));
					result.add(FORMAT, reader.getFormatName());
					result.add(PHASH, imgPhash.getHash(reader.read(0)));
				} finally {
					reader.dispose();
				}
			}
		} finally {
			if (in != null)
				in.close();
		}
	}

	@Override
	protected void parseContent(InputStream inputStream) throws Exception {
		File tempFile = ParserAbstract.createTempFile(inputStream, "image");
		try {
			parseContent(tempFile);
		} finally {
			tempFile.delete();
		}
	}
}