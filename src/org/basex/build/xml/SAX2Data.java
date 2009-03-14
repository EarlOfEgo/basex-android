package org.basex.build.xml;

import static org.basex.util.Token.*;
import java.io.IOException;
import org.basex.build.Builder;
import org.basex.util.Atts;
import org.basex.util.Token;
import org.basex.util.TokenBuilder;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.DefaultHandler;

/**
 * SAX Parser wrapper.
 * 
 * @author Workgroup DBIS, University of Konstanz 2005-09, ISC License
 * @author Christian Gruen
 */
public final class SAX2Data extends DefaultHandler implements LexicalHandler {
  /** Temporary attribute array. */
  final Atts atts = new Atts();
  /** Builder reference. */
  final Builder builder;
  /** Document name. */
  final String name;
  /** DTD flag. */
  boolean dtd;
  /** Element counter. */
  int nodes;

  // needed for XMLEntityManager: increase entity limit
  static { System.setProperty("entityExpansionLimit", "536870912"); }

  /**
   * Constructor.
   * @param build builder reference
   * @param doc document name
   */
  public SAX2Data(final Builder build, final String doc) {
    builder = build;
    name = doc;
  }
  
  @Override
  public void startElement(final String uri, final String ln, final String qn,
      final Attributes at) throws SAXException {

    try {
      finishText();
      final int as = at.getLength();
      atts.reset();
      for(int a = 0; a < as; a++) {
        atts.add(Token.token(at.getQName(a)), Token.token(at.getValue(a)));
      }
      builder.startElem(Token.token(qn), atts);
      nodes++;
    } catch(final IOException ex) {
      error(ex);
    }
  }

  @Override
  public void endElement(final String uri, final String ln, final String qn)
      throws SAXException {

    try {
      finishText();
      builder.endElem(Token.token(qn));
    } catch(final IOException ex) {
      error(ex);
    }
  }

  @Override
  public void characters(final char[] ch, final int s, final int l) {
    final int e = s + l;
    for(int i = s; i < e; i++) tb.addUTF(ch[i]);
  }

  @Override
  public void processingInstruction(final String nm, final String cont)
      throws SAXException {

    if(dtd) return;
    try {
      finishText();
      builder.pi(new TokenBuilder(nm + ' ' + cont));
    } catch(final IOException ex) {
      error(ex);
    }
  }

  public void comment(final char[] ch, final int s, final int l)
      throws SAXException {

    if(dtd) return;
    try {
      finishText();
      builder.comment(new TokenBuilder(new String(ch, s, l)));
    } catch(final IOException ex) {
      error(ex);
    }
  }

  /** Temporary string builder. */
  private final TokenBuilder tb = new TokenBuilder();
  /** Temporary namespaces. */
  private final Atts ns = new Atts();

  /**
   * Checks if a text node has to be written.
   * @throws IOException I/O exception
   */
  private void finishText() throws IOException {
    if(tb.size != 0) {
      builder.text(tb, false);
      tb.reset();
    }
    for(int i = 0; i < ns.size; i++) builder.startNS(ns.key[i], ns.val[i]);
    ns.reset();
  }

  /**
   * Creates and throws a SAX exception for the specified exception.
   * @param ex exception
   * @throws SAXException SAX exception
   */
  private void error(final IOException ex) throws SAXException {
    final SAXException ioe = new SAXException(ex.getMessage());
    ioe.setStackTrace(ex.getStackTrace());
    throw ioe;
  }

  // Entity Resolver
  /* public InputSource resolveEntity(String pub, String sys) { } */

  // DTDHandler
  /* public void notationDecl(String name, String pub, String sys) { } */
  /* public void unparsedEntityDecl(final String name, final String pub,
      final String sys, final String not) { } */

  // ContentHandler
  /*public void setDocumentLocator(final Locator locator) { } */
  
  @Override
  public void startDocument() throws SAXException {
    try {
      builder.startDoc(token(name));
    } catch(final IOException ex) {
      error(ex);
    }
  }
  
  @Override
  public void endDocument() throws SAXException {
    try {
      builder.endDoc();
    } catch(final IOException ex) {
      error(ex);
    }
  }

  @Override
  public void startPrefixMapping(final String prefix, final String uri) {
    ns.add(Token.token(prefix), Token.token(uri));
  }

  /*public void endPrefixMapping(final String prefix) { } */
  /*public void ignorableWhitespace(char[] ch, int s, int l) { } */
  /*public void skippedEntity(final String name) { } */

  // ErrorHandler
  /* public void warning(final SAXParseException e) { } */
  /* public void fatalError(final SAXParseException e) { } */

  // LexicalHandler
  public void startDTD(final String n, final String pid, final String sid) {
    dtd = true;
  }

  public void endDTD() {
    dtd = false;
  }

  public void endCDATA() { }
  public void endEntity(final String n) { }
  public void startCDATA() { }
  public void startEntity(final String n) { }
}
