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
import java.util.List;
import java.util.logging.Level;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagField;

import com.opensearchserver.textextractor.ParserAbstract;
import com.opensearchserver.textextractor.ParserDocument;
import com.opensearchserver.textextractor.ParserField;

public class Audio extends ParserAbstract {

	final protected static ParserField NAME = ParserField.newString("name",
			null);

	final protected static ParserField ARTIST = ParserField.newString("artist",
			null);

	final protected static ParserField ALBUM = ParserField.newString("album",
			null);

	final protected static ParserField ALBUM_ARTIST = ParserField.newString(
			"album_artist", null);

	final protected static ParserField TITLE = ParserField.newString("title",
			null);

	final protected static ParserField TRACK = ParserField.newString("track",
			null);

	final protected static ParserField YEAR = ParserField.newInteger("year",
			null);

	final protected static ParserField GENRE = ParserField.newString("genre",
			null);

	final protected static ParserField COMMENT = ParserField.newString(
			"comment", null);

	final protected static ParserField COMPOSER = ParserField.newString(
			"composer", null);

	final protected static ParserField GROUPING = ParserField.newString(
			"grouping", null);

	final protected static ParserField[] FIELDS = { NAME, ARTIST, ALBUM,
			ALBUM_ARTIST, TITLE, TRACK, YEAR, GENRE, COMMENT, COMPOSER,
			GROUPING };

	static {
		AudioFileIO.logger.setLevel(Level.OFF);
	}

	public Audio() {
	}

	@Override
	protected ParserField[] getParameters() {
		return null;
	}

	@Override
	protected ParserField[] getFields() {
		return FIELDS;
	}

	private void addFields(ParserDocument result, Tag tag, FieldKey fieldKey,
			ParserField parserField) {
		List<TagField> list = tag.getFields(fieldKey);
		if (list != null && list.size() > 0) {
			for (TagField field : list)
				result.add(parserField, field.toString());
			return;
		}
		String f = tag.getFirst(fieldKey);
		if (f == null)
			return;
		f = f.trim();
		if (f.length() == 0)
			return;
		result.add(parserField, f);
	}

	@Override
	protected void parseContent(File file) throws Exception {
		AudioFile f = AudioFileIO.read(file);
		Tag tag = f.getTag();
		if (tag == null)
			return;
		addFields(metas, tag, FieldKey.TITLE, TITLE);
		addFields(metas, tag, FieldKey.ARTIST, ARTIST);
		addFields(metas, tag, FieldKey.ALBUM, ALBUM);
		addFields(metas, tag, FieldKey.YEAR, YEAR);
		addFields(metas, tag, FieldKey.TRACK, TRACK);
		addFields(metas, tag, FieldKey.ALBUM_ARTIST, ALBUM_ARTIST);
		addFields(metas, tag, FieldKey.COMMENT, COMMENT);
		addFields(metas, tag, FieldKey.COMPOSER, COMPOSER);
		addFields(metas, tag, FieldKey.GROUPING, GROUPING);
	}

	@Override
	protected void parseContent(InputStream inputStream) throws Exception {
		File tempFile = ParserAbstract.createTempFile(inputStream, "audio");
		try {
			parseContent(tempFile);
		} finally {
			tempFile.delete();
		}
	}

}
