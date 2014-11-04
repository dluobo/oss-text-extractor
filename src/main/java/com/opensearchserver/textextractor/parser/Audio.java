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
		ParserDocument result = getNewParserDocument();
		addFields(result, tag, FieldKey.TITLE, TITLE);
		addFields(result, tag, FieldKey.ARTIST, ARTIST);
		addFields(result, tag, FieldKey.ALBUM, ALBUM);
		addFields(result, tag, FieldKey.YEAR, YEAR);
		addFields(result, tag, FieldKey.TRACK, TRACK);
		addFields(result, tag, FieldKey.ALBUM_ARTIST, ALBUM_ARTIST);
		addFields(result, tag, FieldKey.COMMENT, COMMENT);
		addFields(result, tag, FieldKey.COMPOSER, COMPOSER);
		addFields(result, tag, FieldKey.GROUPING, GROUPING);
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
