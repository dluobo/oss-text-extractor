OSSTextExtractor
================

An RESTFul Web Service for text extraction and analysis.
OSSTextExtractor support various binary formats.

- office documents (doc, docx, xls, xlsx, ppt, pptx, pub, vsd, odf, odt, odp)
- pdf,
- rtf,
- rss,
- html,
- audio files, torrent,
- images.

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

The default TCP port is 9091. To change it use the the -port option.

```shell
java -jar target/oss-text-extractor-1.0-SNAPSHOT.jar -port 9092
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

The parser extracts the text information using the following JSON format:

```json
{
	"time_elapsed":1772,
	"documents":
		[
			{
				"fields":
					{
						"content":
							[
								"Table of contents Requirements Getting Started Deleting Querying Data Sorting Text  Analysis Debugging"
							],
						"character_count":[13634],
						"number_of_pages":[7],
						"producer":["FOP 0.20.5"]
					}
			}
		]
}
```

## Contribute

Writing a parser is easy. Just extends the abstract class com.opensearchserver.textextractor.ParserAbstract and implements the required methods.

```java
protected void parseContent(InputStream inputStream) throws IOException;
```

The parse must build a list of ParserDocument. A parser may return one or more documents (one document per page, one document per RSS item, ...). A Parser Document is a list of name/value pair.

Have a look at the Docx class to see a simple example.

```java
	@Override
	protected void parseContent(InputStream inputStream) throws IOException {
		
		// Obtain a new parser document.
		
		ParserDocument parserDocument = getNewParserDocument();

		// Open the document using the inputStream
		
		XWPFDocument document = new XWPFDocument(inputStream);
		XWPFWordExtractor word = null;
		try {
			word = new XWPFWordExtractor(document);

			// Extract the meta data
			
			CoreProperties info = word.getCoreProperties();
			
			if (info != null) {
			
				// Fill the ParserDocument
				
				parserDocument.add(TITLE, info.getTitle());
				parserDocument.add(CREATOR, info.getCreator());
				parserDocument.add(SUBJECT, info.getSubject());
				parserDocument.add(DESCRIPTION, info.getDescription());
				parserDocument.add(KEYWORDS, info.getKeywords());
			}
			
			parserDocument.add(CONTENT, word.getText());

		} finally {
		
			// Free the resource
			
			IOUtils.closeQuietly(word);
		}
	}
```

## License

Copyright 2014 OpenSearchServer Inc.
http://www.opensearchserver.com

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
