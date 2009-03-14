package org.basex.api.jaxp;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFunctionResolver;
import javax.xml.xpath.XPathVariableResolver;
import org.basex.BaseX;
import org.xml.sax.InputSource;

/**
 * This class provides an API for standalone XPath processing.
 *
 * @author Workgroup DBIS, University of Konstanz 2005-09, ISC License
 * @author Christian Gruen
 */
public final class BXPath implements XPath {
  /** Variables. */
  private XPathVariableResolver variables;
  /** Functions. */
  private XPathFunctionResolver functions;

  public void reset() { }

  public void setXPathVariableResolver(final XPathVariableResolver var) {
    variables = var;
    BaseX.notimplemented();
  }

  public XPathVariableResolver getXPathVariableResolver() {
    return variables;
  }

  public void setXPathFunctionResolver(final XPathFunctionResolver fun) {
    functions = fun;
    BaseX.notimplemented();
  }

  public XPathFunctionResolver getXPathFunctionResolver() {
    return functions;
  }

  public void setNamespaceContext(final NamespaceContext ns) {
    BaseX.notimplemented();
  }

  public NamespaceContext getNamespaceContext() {
    return null;
  }

  public XPathExpression compile(final String expr) {
    return new XPathExprImpl(expr);
  }

  public Object evaluate(final String expr, final Object item,
      final QName res) throws XPathExpressionException {

    return new XPathExprImpl(expr).evaluate(item, res);
  }

  public String evaluate(final String expr, final Object item)
      throws XPathExpressionException {

    return new XPathExprImpl(expr).evaluate(item);
  }

  public Object evaluate(final String expr, final InputSource source,
      final QName res) throws XPathExpressionException {

    return new XPathExprImpl(expr).evaluate(source, res);
  }

  public String evaluate(final String expr, final InputSource source)
      throws XPathExpressionException {

    return new XPathExprImpl(expr).evaluate(source);
  }
}
