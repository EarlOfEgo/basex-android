package org.basex.gui;

import static org.basex.Text.*;
import static org.basex.gui.GUIConstants.*;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import javax.swing.JOptionPane;
import org.basex.core.Prop;
import org.basex.gui.layout.BaseXLayout;
import org.basex.gui.layout.BaseXPanel;
import org.basex.util.Performance;
import org.basex.util.Token;

/**
 * This is the status bar of the main window. It displays progress information
 * and includes a memory status.
 * @author Workgroup DBIS, University of Konstanz 2005-09, ISC License
 * @author Christian Gruen
 */
public final class GUIStatus extends BaseXPanel implements Runnable {
  /** Width of the memory status box. */
  private static final int MEMW = 70;
  /** Current status text. */
  private String status = STATUSOK;
  /** Current path. */
  private String oldStatus = STATUSOK;
  /** Current focus on memory. */
  boolean memfocus;
  /** Error flag. */
  boolean error;
  /** Maximum memory. */
  long max = 1;
  /** Used memory. */
  long used;

  /**
   * Constructor.
   * @param main reference to the main window
   */
  public GUIStatus(final GUI main) {
    super(main, null);
    BaseXLayout.setHeight(this, getFont().getSize() + 6);
    addKeyListener(this);
    addMouseListener(this);
    addMouseMotionListener(this);
  }

  /**
   * Sets the status text.
   * @param stat the text to be set
   */
  public void setText(final String stat) {
    error = false;
    refresh(stat);
  }

  /**
   * Sets the error status text.
   * @param stat the text to be set
   */
  public void setError(final String stat) {
    error = true;
    refresh(stat);
  }

  /**
   * Refreshes the status text.
   * @param txt status text
   */
  private void refresh(final String txt) {
    status = txt;
    oldStatus = status;

    final Runtime rt = Runtime.getRuntime();
    max = rt.maxMemory();
    used = rt.totalMemory() - rt.freeMemory();
    if(txt.equals(STATUSWAIT)) new Thread(this).start();
    repaint();
  }

  /**
   * Sets the current path.
   * @param path the path to be set
   */
  public void setPath(final byte[] path) {
    status = path.length == 0 ? oldStatus : Token.string(path);
    error = false;
    repaint();
  }

  /**
   * Thread.
   */
  public void run() {
    Performance.sleep(500);
    repaint();
  }

  @Override
  public void paintComponent(final Graphics g) {
    super.paintComponent(g);
    BaseXLayout.antiAlias(g);

    final int ww = getWidth() - MEMW;
    final int hh = getHeight();

    // draw memory box
    int xe = ww + MEMW - 3;
    int ye = hh - 2;
    g.setColor(Color.white);
    g.fillRect(ww, 0, MEMW - 1, hh - 2);
    g.setColor(COLORBUTTON);
    g.drawLine(ww, 0, xe, 0);
    g.drawLine(ww, 0, ww, ye);

    // show current memory usage
    final boolean full = used * 6 / 5 > max;
    g.setColor(full ? colormark4 : color4);
    g.fillRect(ww + 2, 2, (int) (used * (MEMW - 7) / max), hh - 5);

    // print current memory usage
    g.setFont(getFont());
    FontMetrics fm = g.getFontMetrics();
    final String mem = Performance.format(used, true);
    int fw = ww + (MEMW - 4 - fm.stringWidth(mem)) / 2;
    final int h = fm.getHeight() - 2;
    g.setColor(full ? colormark3 : color6);
    g.drawString(mem, fw, h);

    /* print performance string
    final int w = ww - fm.stringWidth(perf) - 10;
    g.setColor(Color.black);
    g.drawString(perf, w, h);
    */

    // print status text
    g.setColor(Color.black);
    BaseXLayout.chopString(g, Token.token(status), 4, 0, fw - 24);
  }

  @Override
  public void mousePressed(final MouseEvent e) {
    if(memfocus) {
      Performance.gc(4);
      repaint();

      final Runtime rt = Runtime.getRuntime();
      final long occ = rt.totalMemory();
      max = rt.maxMemory();
      used = occ - rt.freeMemory();

      final String inf = MEMTOTAL + Performance.format(max, true) + Prop.NL
          + MEMRESERVED + Performance.format(occ, true) + Prop.NL + MEMUSED
          + Performance.format(used, true) + Prop.NL + Prop.NL + MEMHELP;

      JOptionPane.showMessageDialog(gui, inf, MEMTITLE,
          JOptionPane.INFORMATION_MESSAGE);
    }
  }

  @Override
  public void mouseMoved(final MouseEvent e) {
    memfocus = e.getX() > getWidth() - MEMW;
    if(memfocus) BaseXLayout.help(this, HELPMEM);
    gui.cursor(memfocus ? CURSORHAND : CURSORARROW);
  }

  @Override
  public void mouseExited(final MouseEvent e) {
    memfocus = false;
    gui.cursor(CURSORARROW);
  }
}
