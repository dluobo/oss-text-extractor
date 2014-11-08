OpenSearchServer Text Extractor
===============================

A RESTFul Web Service for text extraction and analysis.
Oss-text-extractor supports various binary formats.

- Word processor (doc, docx, odt, rtf)
- Spreadsheet (xls, xlsx, ods)
- Presentation (ppt, pptx, odp)
- Publishing (pdf, pub)
- Web (rss, html/xhtml)
- Medias (audio, images)
- Others (vsd, text)

## How to build

Compiling the service requires Maven 2.2.1 and Java 7.
 
Get the source code:

```shell
git clone https://github.com/opensearchserver/oss_text_extractor.git
```
    
Compile:

```shell
mvn clean package
```

## Starting the server

TextExtractor works on Linux, Windows, Mac OS with a JAVA 7.
To run the server, open a shell and start the daemon:

```shell
java -jar target/oss-text-extractor-1.0-SNAPSHOT.jar
```

The default TCP port is 9091. To change it use the the -p option.

```shell
java -jar target/oss-text-extractor-1.0-SNAPSHOT.jar
```

## APIs

### Obtain the parser list

* Method: GET
* URL: http://{hostname}:{port}/

```shell
curl -XGET http://localhost:9091
```

The function return the list of available parsers.

```json
["doc","docx","pdfbox"]
```

### Get information about a parser

* Method: GET
* URL: http://{hostname}:{port}/{parser_name}

```shell
curl -XGET http://localhost:9091/pdfbox
```

The function displays which fields are returned by the parser.

```json
{
  "fields":
  	[
		{"name":"title", "type":"STRING", "description":"The title of the Word document"},
		{"name":"author","type":"STRING","description":"The name of the author"},
		{"name":"subject","type":"STRING","description":"The subject of the document"},
		{"name":"content","type":"STRING","description":"The content of the document"},
		{"name":"producer","type":"STRING","description":"The producer of the document"},
		{"name":"keywords","type":"STRING","description":"The keywords of the document"},
		{"name":"creation_date","type":"DATE"},{"name":"modification_date","type":"DATE"},
		{"name":"language","type":"STRING"},{"name":"number_of_pages","type":"INTEGER"}
	]
}
```
    
### Submit a document to a parser

* Method: PUT
* URL: http://{hostname}:{port}/{parser_name}
* Payload: The document

```shell
curl -XPUT --data-binary @tutorial.pdf http://localhost:9091/pdfbox
```
    
If the file is already available in the server, the follow API is available:

* Method: GET
* URL: http://{hostname}:{port}/{parser_name}?path=file_path

```shell
curl -XGET http://localhost:9091/pdfbox?path=/home/manu/tutorial.pdf
```

The parser extracts the metas and text information using the following JSON format:

```json
{
	"time_elapsed": 2735,
	"metas": {
		"number_of_pages": [7],
		"producer": ["FOP 0.20.5"]
	},
	"documents": [ {
		"content": ["Table of contents Requirements Getting Started Deleting Querying Data Sorting Text  Analysis Debugging"],
		"character_count":[13634]
	} ]
}
```

## Contribute

Writing a parser is easy. Just extends the abstract class [ParserAbstract](https://github.com/opensearchserver/oss_text_extractor/blob/master/src/main/java/com/opensearchserver/textextractor/ParserAbstract.java) and implements the required methods.

```java
protected void parseContent(InputStream inputStream) throws Exception;
```

The parse must build a list of ParserDocument. A parser may return one or more documents (one document per page, one document per RSS item, ...). A Parser Document is a list of name/value pair.

Have a look at the [Rtf](https://github.com/opensearchserver/oss_text_extractor/blob/master/src/main/java/com/opensearchserver/textextractor/parser/rtf.java) class to see a simple example.

```java
	@Override
	protected void parseContent(InputStream inputStream) throws Exception {

		// Extract the text data
		RTFEditorKit rtf = new RTFEditorKit();
		Document doc = rtf.createDefaultDocument();
		rtf.read(inputStream, doc, 0);

		// Fill the metas
		metas.add(TITLE, "title of the document");
		
		// Obtain a new parser document.
		ParserDocument result = getNewParserDocument();

		// Fill the field of the ParserDocument
		result.add(CONTENT, doc.getText(0, doc.getLength()));

		// Apply the language detection
		result.add(LANG_DETECTION, languageDetection(CONTENT, 10000));

	}
```

## License

Copyright 2014 [OpenSearchServer Inc.](http://www.opensearchserver.com)


Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
