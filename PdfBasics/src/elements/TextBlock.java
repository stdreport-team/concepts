package elements;

import java.util.ArrayList;
import java.util.List;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;

public class TextBlock extends BlockElement {
	private float							c_startX;
	private float							c_startY;
	private float							c_width;
	private float							c_height;
	private Font							c_font;

	private ColumnText				c_columnText;
	/**
	 * The number of the text lines written so far.
	 */
	private transient int			c_linesWritten;
	/**
	 * the height of the last block written. If the text block spawns across
	 * multiple pages, c_lastHeight is the height of the last piece written.
	 */
	private transient float		c_lastHeight;
	/**
	 * total height (padding and border included) in points of the text written so
	 * far. If the text spawns across multiple pages, this height is the sum of
	 * all pieces of text.
	 */
	private transient float		c_realHeight;

	/**
	 * The real width of the text block, comprising padding and borders. The
	 * property is calculated by the {@link #draw(DrawContext)} method, that takes
	 * into account the {@link #isCanGrowX()} and {@link #getMaxWidth()}
	 * properties and the container's width to calculate the correct total width.
	 */
	private transient float		c_realWidth;

	private transient boolean	c_drawComplete;
	private transient boolean	c_drawing;

	private List<Element>			c_elems;

	/**
	 * Constructs a new text block.
	 * 
	 * @param x
	 *          start x position (upper left point)
	 * @param y
	 *          start y position (upper left point)
	 * @param width
	 *          initial width
	 * @param height
	 *          initial height
	 */
	public TextBlock(float x, float y, float width, float height) {
		this();
		c_startX = x;
		c_startY = y;
		c_width = width;
		c_height = height;
	}

	public TextBlock() {
		c_elems = new ArrayList<Element>();
	}

	public void addElement(Element e) {
		c_elems.add(e);
	}

	public void clearContent() {
		c_elems.clear();
	}
	
	@Override
	public boolean draw(DrawContext context) throws DocumentException {
		if (isDrawComplete()) {
			throw new IllegalStateException("No more contents to write");
		}
		if (!isDrawing()) {
			createColumntext(context.getWriter());
			c_linesWritten = 0;
			c_realWidth = 0;
			c_realHeight = 0;
			if (isCanGrowX()) {
				// in case of horizontal auto-width, adjust the width to contain the
				// largest text line
				if (getMaxWidth() <= 0) {
					// max width not specified --> max width = document width
					setMaxWidth(context.getDocumentAvailWidth());
				}
				float maxTextWidth = calculateTextMaxWidth() + getBorderLeftSize()
						+ getBorderRightSize() + getPaddingLeft() + getPaddingRight();
				// add 1 pt to adjust possible roundings
				maxTextWidth += 1;
				if (maxTextWidth > getMaxWidth())
					c_realWidth = getMaxWidth();
				else
					c_realWidth = maxTextWidth;
			} else {
				c_realWidth = getWidth();
			}
			c_drawing = true;

			float xLeft = c_startX + getBorderLeftSize() + getPaddingLeft();
			float yLower = c_startY - getHeight() + getPaddingBottom()
					+ getBorderBottomSize();
			float height = c_height
					- (getPaddingBottom() + getBorderBottomSize() + getPaddingTop() + getBorderTopSize());
			c_drawComplete = drawText(c_columnText, context.getDocument(), xLeft,
					yLower, height);

			c_realLower -= (getPaddingBottom() + getBorderBottomSize());
			drawBorder(c_columnText, c_startX, c_realLower, c_realUpper);
			c_lastHeight = c_realUpper - c_realLower;
			c_realHeight += c_lastHeight;
		} else {
			c_drawComplete = redraw(context.getWriter(), context.getDocument());
		}
		return c_drawComplete;
	}

	/**
	 * Used internally to write the text on pages after the first.
	 * 
	 * @param w
	 *          writer to use
	 * @param d
	 *          destination document
	 * @return
	 * @throws DocumentException
	 */
	private boolean redraw(PdfWriter w, Document d) throws DocumentException {
		if (!isCanGrowY()) {
			// cannot grow vertically: space for only the remaining height
			float heightLeft = getHeight() - getLastHeight();
			if (heightLeft > 0) {
				drawText(c_columnText, d, c_startX, d.top() - heightLeft, heightLeft);
			}
			return true;
		} else {
			// write a line of text at a time, until all the text has been written
			float yStart = d.top() - getPaddingTop() - getBorderTopSize();
			c_columnText.setUseAscender(true);
			float xLeft = c_startX + getBorderLeftSize() + getPaddingLeft();
			boolean bDrawFinished = false;
			while (!bDrawFinished) {
				if (yStart - c_columnText.getLeading() < d.bottom())
					// no space left even for a single row
					break;
				yStart -= c_columnText.getLeading();
				bDrawFinished = drawText(c_columnText, d, xLeft, yStart,
						c_columnText.getLeading());
			}
			c_realLower -= (getPaddingBottom() + getBorderBottomSize());
			drawBorder(c_columnText, c_startX, c_realLower, c_realUpper);
			c_lastHeight = c_realUpper - c_realLower;
			c_realHeight += c_lastHeight;
			return bDrawFinished;
		}
	}

	/**
	 * Create the columnText object and initialize its properties
	 * 
	 * @param w
	 *          writer to use
	 */
	private void createColumntext(PdfWriter w) {
		PdfContentByte cb = w.getDirectContent();
		c_columnText = new ColumnText(cb);
		for (int i = 0; i < c_elems.size(); i++) {
			Element e = c_elems.get(i);
			if (e instanceof Phrase) {
				Phrase p = (Phrase) e;
				if (p.getFont() == null
						|| p.getFont().getFamilyname().equals(FontFamily.UNDEFINED)
						|| p.getFont().getSize() <= 0) {
					p.setFont(c_font);
				}
			}
			else if (e instanceof Chunk) {
				Chunk c = (Chunk) e;
				if (c.getFont() == null
						|| c.getFont().getFamilyname().equals(FontFamily.UNDEFINED)
						|| c.getFont().getSize() <= 0) {
					c.setFont(c_font);
				}
			}
			c_columnText.addElement(e);
		}
		c_columnText.setLeading(c_font.getSize() * 1.5f);
	}

	private float getPhraseWidth(Phrase p) {
		return getStringWidth(p.getContent(), p.getFont());
	}

	private float getChunkWidth(Chunk p) {
		return getStringWidth(p.getContent(), p.getFont());
	}

	private float getStringWidth(String s, Font f) {
		if (f == null || f.getFamilyname().equals(FontFamily.UNDEFINED)
				|| f.getSize() <= 0)
			f = c_font;

		if (f == null)
			return 0f;

		float fontSize = f.getSize();
		BaseFont bf = f.getCalculatedBaseFont(false);

		String lines[] = s.split("\\n");
		float maxWidth = 0;
		for (String line : lines) {
			float w = bf.getWidthPoint(line, fontSize);
			System.out.println(">>> lunghezza '" + line + "' = " + w + ", fontSize="
					+ fontSize + ", font=" + f.getFamilyname());
			if (w > maxWidth)
				maxWidth = w;
		}
		return maxWidth;
	}

	/**
	 * Calculate the content's max width calculating the text line by line.
	 * 
	 * @return the width in point of the largest line
	 */
	private float calculateTextMaxWidth() {
		float maxWidth = 0;
		for (Element e : c_elems) {
			float w = 0;
			if (e instanceof Phrase)
				w = getPhraseWidth((Phrase) e);
			else if (e instanceof Chunk)
				w = getChunkWidth((Chunk) e);
			if (w > maxWidth)
				maxWidth = w;
		}
		return maxWidth;
	}

	public boolean isCanGrowX() {
		return (getGrowType() == GrowDirection.HORIZ || getGrowType() == GrowDirection.BOTH);
	}

	public boolean isCanGrowY() {
		return (getGrowType() == GrowDirection.VERT || getGrowType() == GrowDirection.BOTH);
	}

	/**
	 * contiene la posizione y del limite inferiore dell'ultimo blocco di testo
	 * scritto. Tale posizione comprende bordi e padding. Se il blocco di testo è
	 * su più pagine, è la coordinata superiore dell'ultimo pezzo scritto.
	 */
	private float	c_realLower;
	/**
	 * contiene la posizione y del limite superiore dell'ultimo blocco di testo
	 * scritto. Tale posizione comprende bordi e padding. Se il blocco di testo è
	 * su più pagine, è la coordinata superiore dell'ultimo pezzo scritto.
	 */
	private float	c_realUpper;

	/**
	 * Draw the buffered text on the pdf writer, using the columnText object
	 * passed as the first argument. NOTE: the arguments regarding position and
	 * size refers exactly to the text rectangle, that is <b>doesn't include</b>
	 * padding and borders.
	 * 
	 * @param c
	 *          ColumnText object used to write the text
	 * @param d
	 *          PDF Document on which the text is written
	 * @param xStart
	 *          left position of the ColumnText rectangle; it takes into account
	 *          left border and left padding
	 * @param yStart
	 *          lower position of the ColumnText rectangle
	 * @param height
	 *          initial height of the ColumnText rectangle; if the text doesn't
	 *          fit the ColumnText rectangle, depending on the
	 *          {@link #getGrowType() growType} property, the rectangle size can
	 *          be vertically increased to enclose all the text.
	 * @return true if the drawing has finished, false if some text remains to be
	 *         drawn on the next page; note that it returns true also if the text
	 *         hasn't completely written but all the available height has been
	 *         used.
	 * @throws DocumentException
	 */
	private boolean drawText(ColumnText c, Document d, float xStart,
			float yStart, float height) throws DocumentException {

		// FIXME controllare che esista l'altezza minima disponibile per scrivere
		// almeno una riga

		boolean endText = true;
		c_lastHeight = 0;

		// prepare the right X point: it limit the text on the right side
		// hence doesn't include border/padding
		float xRight = xStart + c_realWidth - getPaddingRight()
				- getBorderRightSize() - getPaddingLeft() - getBorderLeftSize();
		// float mw = getMaxWidth() > 0 ? getMaxWidth() :
		// d.getPageSize().getWidth();
		// xRight += (isCanGrowX() ? mw : getWidth());
		// if (xRight > d.right())
		// xRight = d.right();
		// xRight -= getPaddingLeft() + getPaddingRight() + getBorderLeftSize() +
		// getBorderRightSize();

		float yLower = yStart;

		float remainingHeight = 0;
		if (getMaxHeight() > 0)
			// FIXME qui forse bisogna sottrarre padding e border
			remainingHeight = getMaxHeight() - c_realHeight;
		else
			remainingHeight = height;

		float yUpper = yStart + remainingHeight;
		// c_realUpper include border and padding
		c_realUpper = yUpper + getPaddingTop() + getBorderTopSize();

		// FIXME need to save columntext status across drawText calls to safely stop
		// writing text
		int status = ColumnText.START_COLUMN;
		while (ColumnText.hasMoreText(status)) {
			c_realLower = yLower - getPaddingBottom() - getBorderBottomSize();
			c.setSimpleColumn(xStart, yLower, xRight, yUpper);
			c.setUseAscender(true);
			status = c.go();
			c_linesWritten += c.getLinesWritten();
			// c_realWidth = Math.max(c_realWidth, c.getFilledWidth() + getPadding() *
			// 2
			// + getBorder().getSize() * 2);
			c_lastHeight += c.getLinesWritten() * c.getLeading();
			// float fw = c.getFilledWidth();
			// if (fw > c_width)
			// System.out
			// .println("WARNING: some text can be cutted out from the text block ("
			// + (fw - c_width) + "pt cutted)");

			if (getGrowType() == GrowDirection.NONE) {
				break;
			} else if ((getGrowType() == GrowDirection.VERT || getGrowType() == GrowDirection.BOTH)
					&& ColumnText.hasMoreText(status)) {
				// need to extend vertically: adjust "y" of lower/upper points
				// dist=space to skip after last written line: is the (negative)
				// vertical space
				// remaining between end of columntext rectangle and the last line
				// written
				float dist = c.getLinesWritten() * c.getLeading() - height;
				yLower -= height + dist;
				if (yLower < 0)
					yLower = 0;
				yUpper -= height + dist;
				if (yUpper < 0)
					yUpper = 0;
			}
			if (yUpper < d.bottomMargin()) {
				endText = false;
				break;
			}
			if (yLower < d.bottomMargin()) {
				yLower = d.bottomMargin();
			}
			if (yUpper - yLower < c.getLeading()) {
				// non ci sta nemmeno una riga
				endText = false;
				break;
			}
		}

		// c.getDescender is negative: add the correct distance from the baseline of
		// the last written row
		yLower = c.getYLine() + c.getDescender();
		c_realLower = yLower;
		// yUpper = yLower + height + c_padding;
		// c_realUpper = yLower;

		// drawBorder(c, xStart, c_realLower, c_realUpper);
		// c_lastHeight = c_realUpper - c_realLower;
		// c_totalHeight += c_lastHeight;
		return endText;
	}

	private void drawBorder(ColumnText c, float x1, float y1, float y2) {
		PdfContentByte cb = c.getCanvas();
		float x2 = x1 + c_realWidth;
		cb.setRGBColorStrokeF(0.3f, 0.17f, 0.5f);
		if (getBorderLeftSize() > 0) {
			float halfBorder = getBorderLeftSize() / 2;
			// x1 -= c_padding;

			cb.setLineWidth(getBorderLeftSize());
			c.getCanvas().moveTo(x1 + halfBorder, y1);
			c.getCanvas().lineTo(x1 + halfBorder, y2); // vertical left side
		}
		if (getBorderTopSize() > 0) {
			float halfBorder = getBorderTopSize() / 2;
			cb.setLineWidth(getBorderTopSize());
			c.getCanvas().moveTo(x1, y2 - halfBorder);
			c.getCanvas().lineTo(x2, y2 - halfBorder); // horizontal top side
		}
		if (getBorderRightSize() > 0) {
			float halfBorder = getBorderRightSize() / 2;
			cb.setLineWidth(getBorderRightSize());
			c.getCanvas().moveTo(x2 - halfBorder, y2);
			c.getCanvas().lineTo(x2 - halfBorder, y1); // vertical right side
		}
		if (getBorderBottomSize() > 0) {
			float halfBorder = getBorderBottomSize() / 2;
			cb.setLineWidth(getBorderBottomSize());
			c.getCanvas().moveTo(x2, y1 + halfBorder);
			c.getCanvas().lineTo(x1, y1 + halfBorder); // horizontal bottom side
		}

		c.getCanvas().stroke();
		c_lastHeight += getBorderTopSize() + getBorderBottomSize();
	}

	// private float getLastlineExtraspace(ColumnText c) {
	// if (c.getCompositeElements().size() > 0) {
	// Element e = c.getCompositeElements().get(
	// c.getCompositeElements().size() - 1);
	// if (e instanceof Phrase) {
	// return ((Phrase) e).getLeading()
	// - ((Phrase) e).getFont().getSize();
	// }
	// }
	// return c.getLeading() - c_font.getSize();
	// }

	/**
	 * Return the lines written in the box so far.
	 * 
	 * @return number of the lines written so far
	 */
	public int getLinesWritten() {
		return c_linesWritten;
	}

	/**
	 * This method returns a meaningful value only after the
	 * {@link #draw(DrawContext)} method has been called.
	 * <p>
	 * If {@link #isCanGrowX()} is false, this value is always equals to
	 * {@link #getWidth()}, otherwise it's calculated as the minimum value between
	 * {@link #getMaxWidth()} and the width of the longest line.
	 * 
	 * @return the real width of this text block once written
	 */
	public float getRealWidth() {
		return c_realWidth;
	}

	/**
	 * Returns the height of the block rendered on the last written page. If the
	 * text spawns across multiple pages, this property returns only the height of
	 * the text written in the last page. If the text fit entirely in one page,
	 * this property is equal to {@link #getRealHeight() totalHeight}.
	 * <p/>
	 * Note that this property is meaningful only after the
	 * {@link #draw(PdfWriter, Document) draw} method has been called.
	 * 
	 * @return the height (in points) of the actual block rendered on the last
	 *         page
	 * 
	 * @see #getRealHeight()
	 * @see #getHeight()
	 */
	public float getLastHeight() {
		return c_lastHeight;
	}

	public float getStartX() {
		return c_startX;
	}

	public void setStartX(float x) {
		c_startX = x;
	}

	public float getStartY() {
		return c_startY;
	}

	public void setStartY(float y) {
		c_startY = y;
	}

	public float getWidth() {
		return c_width;
	}

	public void setWidth(float width) {
		c_width = width;
	}

	/**
	 * This is the initial height of the text block. If the block has fixed
	 * height, this is also the height of the text block once written on the
	 * document. Conversely, if {@link #isCanGrowY()} is true, the height of the
	 * block written ({@link #getRealHeight()}) is likely different from the
	 * initial height.
	 * 
	 * @return the initial height of the text block
	 */
	public float getHeight() {
		return c_height;
	}

	/**
	 * Assign the initial height of the text block
	 * 
	 * @param height
	 *          initial height in points
	 */
	public void setHeight(float height) {
		c_height = height;
	}

	public Font getFont() {
		return c_font;
	}

	public void setFont(Font font) {
		c_font = font;
	}

	public float getRealLower() {
		return c_realLower;
	}

	public float getRealUpper() {
		return c_realUpper;
	}

	/**
	 * Returns the height of the whole text block rendered so far. If the text
	 * spawns across multiple pages, this property return the sum of all text
	 * parts.
	 * <p/>
	 * Note that this property is meaningful only after the
	 * {@link #draw(PdfWriter, Document) draw} method has been called.
	 * 
	 * @return the height (in points) of the actual block rendered on the last
	 *         page
	 * 
	 * @see #getRealHeight()
	 * @see #getHeight()
	 */
	public float getRealHeight() {
		return c_realHeight;
	}

	public boolean isDrawComplete() {
		return c_drawComplete;
	}

	public void resetDrawStatus() {
		c_drawComplete = false;
		c_drawing = false;
	}

	/**
	 * 
	 * @return true if this element has started the writing on the report document
	 *         and has not yet finished.
	 */
	public boolean isDrawing() {
		return c_drawing;
	}
}
