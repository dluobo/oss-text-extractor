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

import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.pegdown.PegDownProcessor;
import org.pegdown.ast.AbbreviationNode;
import org.pegdown.ast.AutoLinkNode;
import org.pegdown.ast.BlockQuoteNode;
import org.pegdown.ast.BulletListNode;
import org.pegdown.ast.CodeNode;
import org.pegdown.ast.DefinitionListNode;
import org.pegdown.ast.DefinitionNode;
import org.pegdown.ast.DefinitionTermNode;
import org.pegdown.ast.ExpImageNode;
import org.pegdown.ast.ExpLinkNode;
import org.pegdown.ast.HeaderNode;
import org.pegdown.ast.HtmlBlockNode;
import org.pegdown.ast.InlineHtmlNode;
import org.pegdown.ast.ListItemNode;
import org.pegdown.ast.MailLinkNode;
import org.pegdown.ast.Node;
import org.pegdown.ast.OrderedListNode;
import org.pegdown.ast.ParaNode;
import org.pegdown.ast.QuotedNode;
import org.pegdown.ast.RefImageNode;
import org.pegdown.ast.RefLinkNode;
import org.pegdown.ast.ReferenceNode;
import org.pegdown.ast.RootNode;
import org.pegdown.ast.SimpleNode;
import org.pegdown.ast.SpecialTextNode;
import org.pegdown.ast.StrikeNode;
import org.pegdown.ast.StrongEmphSuperNode;
import org.pegdown.ast.SuperNode;
import org.pegdown.ast.TableBodyNode;
import org.pegdown.ast.TableCaptionNode;
import org.pegdown.ast.TableCellNode;
import org.pegdown.ast.TableColumnNode;
import org.pegdown.ast.TableHeaderNode;
import org.pegdown.ast.TableNode;
import org.pegdown.ast.TableRowNode;
import org.pegdown.ast.TextNode;
import org.pegdown.ast.VerbatimNode;
import org.pegdown.ast.Visitor;
import org.pegdown.ast.WikiLinkNode;

import com.opensearchserver.textextractor.ParserAbstract;
import com.opensearchserver.textextractor.ParserDocument;
import com.opensearchserver.textextractor.ParserField;

public class Markdown extends ParserAbstract {

	final protected static ParserField CONTENT = ParserField.newString(
			"content", "The content of the document");

	final protected static ParserField URL = ParserField.newString("url",
			"Detected URLs");

	final protected static ParserField LANG_DETECTION = ParserField.newString(
			"lang_detection", "Detection of the language");

	final protected static ParserField[] FIELDS = { CONTENT, URL,
			LANG_DETECTION };

	private ParserDocument result;

	public Markdown() {
	}

	@Override
	protected ParserField[] getParameters() {
		return null;
	}

	@Override
	protected ParserField[] getFields() {
		return FIELDS;
	}

	private void parseContent(char[] source) throws Exception {
		// PegDownProcessor is not thread safe One processor per thread
		PegDownProcessor pdp = new PegDownProcessor();
		pdp.parseMarkdown(source).accept(new ExtractVisitor());
		result = getNewParserDocument();
		result.add(LANG_DETECTION, languageDetection(CONTENT, 10000));
	}

	@Override
	protected void parseContent(InputStream inputStream) throws Exception {
		parseContent(IOUtils.toCharArray(inputStream));
	}

	public class ExtractVisitor implements Visitor {

		@Override
		public void visit(AbbreviationNode node) {
		}

		@Override
		public void visit(AutoLinkNode node) {
			result.add(CONTENT, node.getText());
		}

		@Override
		public void visit(BlockQuoteNode node) {
		}

		@Override
		public void visit(BulletListNode node) {
		}

		@Override
		public void visit(CodeNode node) {
			result.add(CONTENT, node.getText());
		}

		@Override
		public void visit(DefinitionListNode node) {
		}

		@Override
		public void visit(DefinitionNode node) {
		}

		@Override
		public void visit(DefinitionTermNode node) {
		}

		@Override
		public void visit(ExpImageNode node) {
			result.add(CONTENT, node.title);
			result.add(URL, node.url);
		}

		@Override
		public void visit(ExpLinkNode node) {
			result.add(CONTENT, node.title);
			result.add(URL, node.url);
		}

		@Override
		public void visit(HeaderNode node) {
		}

		@Override
		public void visit(HtmlBlockNode node) {
			result.add(CONTENT, node.getText());
		}

		@Override
		public void visit(InlineHtmlNode node) {
			result.add(CONTENT, node.getText());
		}

		@Override
		public void visit(ListItemNode node) {
		}

		@Override
		public void visit(MailLinkNode node) {
			result.add(CONTENT, node.getText());
		}

		@Override
		public void visit(OrderedListNode node) {
		}

		@Override
		public void visit(ParaNode node) {
		}

		@Override
		public void visit(QuotedNode node) {
		}

		@Override
		public void visit(ReferenceNode node) {
			result.add(CONTENT, node.getTitle());
			result.add(URL, node.getUrl());
		}

		@Override
		public void visit(RefImageNode node) {
		}

		@Override
		public void visit(RefLinkNode node) {
		}

		@Override
		public void visit(RootNode node) {
		}

		@Override
		public void visit(SimpleNode node) {
		}

		@Override
		public void visit(SpecialTextNode node) {
			result.add(CONTENT, node.getText());
		}

		@Override
		public void visit(StrikeNode node) {
		}

		@Override
		public void visit(StrongEmphSuperNode node) {
		}

		@Override
		public void visit(TableBodyNode node) {
		}

		@Override
		public void visit(TableCaptionNode node) {
		}

		@Override
		public void visit(TableCellNode node) {
		}

		@Override
		public void visit(TableColumnNode node) {
		}

		@Override
		public void visit(TableHeaderNode node) {
		}

		@Override
		public void visit(TableNode node) {
		}

		@Override
		public void visit(TableRowNode node) {
		}

		@Override
		public void visit(VerbatimNode node) {
			result.add(CONTENT, node.getText());
		}

		@Override
		public void visit(WikiLinkNode node) {
			result.add(CONTENT, node.getText());
		}

		@Override
		public void visit(TextNode node) {
			System.out.println("TEXT: node.getText()");
			result.add(CONTENT, node.getText());

		}

		@Override
		public void visit(SuperNode node) {
		}

		@Override
		public void visit(Node node) {
		}
	}

}
