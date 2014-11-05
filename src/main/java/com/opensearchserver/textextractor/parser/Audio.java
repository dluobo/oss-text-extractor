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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.SupportedFileFormat;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagField;
import org.jaudiotagger.tag.TagTextField;

import com.opensearchserver.textextractor.ParserAbstract;
import com.opensearchserver.textextractor.ParserField;

public class Audio extends ParserAbstract {

	final protected static Map<FieldKey, ParserField> FIELDMAP;
	final protected static ParserField[] FIELDS;

	final protected static ParserField FORMAT;

	static {
		// Build the list of extension for the FORMAT parameter
		StringBuilder sb = new StringBuilder("Supported format: ");
		boolean first = true;
		for (SupportedFileFormat sff : SupportedFileFormat.values()) {
			if (!first)
				sb.append(", ");
			else
				first = false;
			sb.append(sff.getFilesuffix());
		}
		FORMAT = ParserField.newString("format", sb.toString());

		// Build the list of fields returned by the library
		FIELDMAP = new HashMap<FieldKey, ParserField>();
		for (FieldKey fieldKey : FieldKey.values())
			FIELDMAP.put(fieldKey,
					ParserField.newString(fieldKey.name().toLowerCase(), null));
		FIELDS = FIELDMAP.values().toArray(new ParserField[FIELDMAP.size()]);
		Arrays.sort(FIELDS, ParserField.COMPARATOR);
	}

	final protected static ParserField[] PARAMETERS = { FORMAT };

	public Audio() {
	}

	@Override
	protected ParserField[] getParameters() {
		return PARAMETERS;
	}

	@Override
	protected ParserField[] getFields() {
		return FIELDS;
	}

	@Override
	protected void parseContent(File file) throws Exception {
		AudioFile f = AudioFileIO.read(file);
		Tag tag = f.getTag();
		if (tag == null)
			return;
		if (tag.getFieldCount() == 0)
			return;
		for (Map.Entry<FieldKey, ParserField> entry : FIELDMAP.entrySet()) {
			List<TagField> tagFields = tag.getFields(entry.getKey());
			if (tagFields == null)
				continue;
			for (TagField tagField : tagFields) {
				if (!(tagField instanceof TagTextField))
					continue;
				metas.add(entry.getValue(),
						((TagTextField) tagField).getContent());
			}
		}
	}

	@Override
	protected void parseContent(InputStream inputStream) throws Exception {
		String format = getParameterValue(FORMAT, 0);
		if (StringUtils.isEmpty(format))
			throw new Exception("The format parameter is missing");
		File tempFile = ParserAbstract
				.createTempFile(inputStream, '.' + format);
		try {
			parseContent(tempFile);
		} finally {
			tempFile.delete();
		}
	}

}
