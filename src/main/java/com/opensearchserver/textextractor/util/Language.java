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
package com.opensearchserver.textextractor.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.cybozu.labs.langdetect.Detector;
import com.cybozu.labs.langdetect.DetectorFactory;
import com.cybozu.labs.langdetect.LangDetectException;

public class Language {

	private static final String[] LANG_LIST = { "af", "ar", "bg", "bn", "cs",
			"da", "de", "el", "en", "es", "et", "fa", "fi", "fr", "gu", "he",
			"hi", "hr", "hu", "id", "it", "ja", "kn", "ko", "lt", "lv", "mk",
			"ml", "mr", "ne", "nl", "no", "pa", "pl", "pt", "ro", "ru", "sk",
			"sl", "so", "sq", "sv", "sw", "ta", "te", "th", "tl", "tr", "uk",
			"ur", "vi", "zh-cn", "zh-tw" };

	static {
		try {
			List<String> langList = DetectorFactory.getLangList();
			List<String> profiles = new ArrayList<String>(langList.size());
			for (String lang : LANG_LIST) {
				InputStream is = com.cybozu.labs.langdetect.Detector.class
						.getResourceAsStream("/profiles/" + lang);
				profiles.add(IOUtils.toString(is));
				is.close();
			}
			DetectorFactory.loadProfile(profiles);
		} catch (LangDetectException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static final String detect(String text, int length)
			throws LangDetectException {
		if (StringUtils.isEmpty(text))
			return null;
		Detector detector = DetectorFactory.create();
		detector.setMaxTextLength(length);
		detector.append(text);
		return detector.detect();
	}

	public static final String quietDetect(String text, int length) {
		try {
			return detect(text, length);
		} catch (LangDetectException e) {
			return null;
		}
	}
}
