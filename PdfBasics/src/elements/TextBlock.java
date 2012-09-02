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

public class TextBlock {
	private float						c_llx;
	private float						c_lly;
	private float						c_width;
	private float						c_maxWidth;
	private float						c_maxHeight;
	private float						c_height;
	private float						c_borderSize;
	private float						c_borderColor;
	private float						c_padding;
	private Font						c_font;

	private ColumnText			c_columnText;
	private transient int		c_linesWritten;
	private transient float	c_lastHeight;
	private transient float	c_totalHeight;
	private transient float	c_realWidth;

	private transient boolean	c_drawComplete;
	private transient boolean	c_drawing;
	
	
	private List<Element>		c_elems;

	public enum GrowDirection {
		NONE, HORIZ, VERT, BOTH
	}

	private GrowDirection	c_growType;

	public TextBlock(float llx, float lly, float width, float height) {
		c_elems = new ArrayList<Element>();
		c_llx = llx;
		c_lly = lly;
		c_width = width;
		c_height = height;
	}

	public void addElement(Element e) {
		c_elems.add(e);
	}

	/**
	 * Write the buffered text on the pdf document. If the text doesn't fit the current page, or is 
	 * higher than {@link #getMaxHeight() maxHeight} property, it writes as much as lines possible and stops
	 * before the limit is reached.
	 * <p/>
	 * If the text spawns across multiple pages and the current page is not the last one, the method return true
	 * indicating that the text needs subsequent calls to this method to write in its entirety.
	 * 
	 * @param w
	 *          writer to be used to effectively write the text
	 * @param d
	 *          PDF Document on which the text is written
	 * @return true if the drawing has finished, false if some text remains to be
	 *         written on the next page; note that it returns true also if the text
	 *         hasn't completely written but all the available height has been
	 *         used (see {@link #getMaxHeight() maxHeight} property).
	 * @throws DocumentException on low level errors during writing on the pdf document
	 */
	public boolean write(PdfWriter w, Document d) throws DocumentException {
		if (isDrawComplete()) {
			throw new IllegalStateException("No more contents to write");
		}
		if (!isDrawing()) {
			createColumntext(w);
			c_linesWritten = 0;
			c_realWidth = 0;
			c_totalHeight = 0;
			if (isCanGrowX()) {
				// in case of horizontal auto-width, adjust the width to contain the
				// largest text line
				if (getMaxWidth() <= 0) {
					// max width not specified --> max width = document width
					c_maxWidth = d.getPageSize().getWidth() - d.leftMargin()
							- d.rightMargin();
				}
				// float maxWidth = getTextMaxWidth();
				// if (c_width < maxWidth)
				// c_width = maxWidth;
			}
			c_drawing = true;
			c_drawComplete = drawText(c_columnText, d, c_llx, c_lly, c_height);
		}
		else {
			c_drawComplete = redraw(w, d);			
		}
		return c_drawComplete;
	}

	/**
	 * Create the columnText object and initialize its properties
	 * @param w writer to use
	 */
	private void createColumntext(PdfWriter w) {
		PdfContentByte cb = w.getDirectContent();
		c_columnText = new ColumnText(cb);
		for (int i = 0; i < c_elems.size(); i++) {
			Element e = c_elems.get(i);
			if (e instanceof Phrase)
				((Phrase) e).setFont(c_font);
			c_columnText.addElement(e);
		}
		c_columnText.setLeading(c_font.getSize() * 1.5f);		
	}
	
	/**
	 * Used internally to write the text on pages after the first.
	 * @param w writer to use
	 * @param d destination document 
	 * @return
	 * @throws DocumentException
	 */
	private boolean redraw(PdfWriter w, Document d) throws DocumentException {
		if (!isCanGrowY()) {
			// cannot grow vertically: space for only the remaining height
			float heightLeft = getHeight() - getLastHeight();
			if (heightLeft > 0) {
				drawText(c_columnText, d, c_llx, d.top() - heightLeft, heightLeft);
			}
			return true;
		} else {
			//write a line of text at a time, until all the text has been written
			float yStart = d.top() - c_columnText.getLeading();
			boolean bDrawFinished = false;
			while (yStart > d.bottomMargin() && !bDrawFinished) {
				bDrawFinished = drawText(c_columnText, d, c_llx,	yStart, c_columnText.getLeading());
				yStart -= c_columnText.getLeading();
			}
			return bDrawFinished;
		}
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
			if (w > maxWidth)
				maxWidth = w;
		}
		return maxWidth;
	}

	private float getTextMaxWidth() {
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

	private float	c_realLower;
	private float	c_realUpper;

	/**
	 * Draw the buffered text on the pdf writer, using the columnText object
	 * passed as the first argument.
	 * 
	 * @param c
	 *          ColumnText object used to write the text
	 * @param d
	 *          PDF Document on which the text is written
	 * @param xStart
	 *          left position of the ColumnText rectangle
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
		boolean endText = true;
		c_lastHeight = 0;
		float xLeft = xStart + getBorderSize() + getPadding();
		float xRight = xStart - getPadding() - getBorderSize(); // right x
		xRight += (isCanGrowX() ? getMaxWidth() : getWidth());
		float yLower = yStart + getPadding() + getBorderSize(); // lower y
		float yUpper = yStart + height - getPadding() - getBorderSize(); // upper y
		c_realUpper = yStart + height;
		
		//FIXME need to save columntext status across drawText calls to safely stop writing text
		int status = ColumnText.START_COLUMN;
		while (ColumnText.hasMoreText(status)) {
			c_realLower = yLower;
			c.setSimpleColumn(xLeft, yLower, xRight, yUpper);
			c.setUseAscender(true);
			status = c.go();
			c_linesWritten += c.getLinesWritten();
			c_realWidth = Math.max(c_realWidth, c.getFilledWidth() + getPadding() * 2
					+ getBorderSize() * 2);
			c_lastHeight += c.getLinesWritten() * c.getLeading();
			float fw = c.getFilledWidth();
			if (fw > c_width)
				System.out
						.println("WARNING: some text can be cutted out from the text block ("
								+ (fw - c_width) + "pt cutted)");

			if (getGrowType() == GrowDirection.NONE) {
				break;
			} else if ((getGrowType() == GrowDirection.VERT || getGrowType() == GrowDirection.BOTH)
					&& ColumnText.hasMoreText(status)) {
				// need to extend vertically: adjust "y" of lower/upper points
				// dist=space to skip after last written line
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
		yLower = c.getYLine() - c_padding + c.getDescender();
		c_realLower = yLower;
		// yUpper = yLower + height + c_padding;
		// c_realUpper = yLower;

		drawBorder(c, xStart, c_realLower, c_realUpper);
		c_lastHeight = c_realUpper - c_realLower;
		c_totalHeight += c_lastHeight;
		return endText;
	}

	private void drawBorder(ColumnText c, float x1, float y1, float y2) {
		PdfContentByte cb = c.getCanvas();
		if (c_borderSize > 0) {
			float halfBorder = c_borderSize / 2;
			// x1 -= c_padding;
			float x2 = x1 + c_realWidth;
			cb.setLineWidth(c_borderSize);
			cb.setRGBColorStrokeF(0.3f, 0.17f, 0.5f);
			c.getCanvas().moveTo(x1 + halfBorder, y1);
			c.getCanvas().lineTo(x1 + halfBorder, y2); // vertical left side
			c.getCanvas().moveTo(x1, y2 - halfBorder);
			c.getCanvas().lineTo(x2, y2 - halfBorder); // horizontal top side
			c.getCanvas().moveTo(x2 - halfBorder, y2);
			c.getCanvas().lineTo(x2 - halfBorder, y1); // vertical right side
			c.getCanvas().moveTo(x2, y1 + halfBorder);
			c.getCanvas().lineTo(x1, y1 + halfBorder); // horizontal bottom side
			c.getCanvas().stroke();
			c_lastHeight += c_borderSize * 2;
		}
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

	public float getRealWidth() {
		return c_realWidth;
	}

	/**
	 * Returns the height of the block rendered on the last written page. If the
	 * text spawns across multiple pages, this property returns only the height of the
	 * text written in the last page. If the text fit entirely in one page,
	 * this property is equal to {@link #getTotalHeight() totalHeight}.
	 * <p/>
	 * Note that this property is meaningful only after the
	 * {@link #draw(PdfWriter, Document) draw} method has been called.
	 * 
	 * @return the height (in points) of the actual block rendered on the last
	 *         page
	 * 
	 * @see #getTotalHeight()
	 * @see #getHeight()
	 */
	public float getLastHeight() {
		return c_lastHeight;
	}

	public float getLlx() {
		return c_llx;
	}

	public void setLlx(float llx) {
		c_llx = llx;
	}

	public float getLly() {
		return c_lly;
	}

	public void setLly(float lly) {
		c_lly = lly;
	}

	public float getWidth() {
		return c_width;
	}

	public void setWidth(float width) {
		c_width = width;
	}

	public float getHeight() {
		return c_height;
	}

	public void setHeight(float height) {
		c_height = height;
	}

	public GrowDirection getGrowType() {
		return c_growType;
	}

	public void setGrowType(GrowDirection growType) {
		c_growType = growType;
	}

	public float getBorderSize() {
		return c_borderSize;
	}

	public void setBorderSize(float borderSize) {
		c_borderSize = borderSize;
	}

	public float getBorderColor() {
		return c_borderColor;
	}

	public void setBorderColor(float borderColor) {
		c_borderColor = borderColor;
	}

	public float getPadding() {
		return c_padding;
	}

	public void setPadding(float padding) {
		c_padding = padding;
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

	public float getMaxWidth() {
		return c_maxWidth;
	}

	public void setMaxWidth(float maxWidth) {
		c_maxWidth = maxWidth;
	}

	/**
	 * Returns the height of the whole text block rendered so far. If the text
	 * spawns across multiple pages, this property return the sum of every parts
	 * height.
	 * <p/>
	 * Note that this property is meaningful only after the
	 * {@link #draw(PdfWriter, Document) draw} method has been called.
	 * 
	 * @return the height (in points) of the actual block rendered on the last
	 *         page
	 * 
	 * @see #getTotalHeight()
	 * @see #getHeight()
	 */
	public float getTotalHeight() {
		return c_totalHeight;
	}

	public boolean isDrawComplete() {
		return c_drawComplete;
	}

	public boolean isDrawing() {
		return c_drawing;
	}

	/**
	 * The maximum height this block of text. If the text doesn't fit the width/height settings,
	 * the text is clipped.
	 * @return the maximum total height (in points) that this text block can reach.
	 */
	public float getMaxHeight() {
		return c_maxHeight;
	}

	public void setMaxHeight(float maxHeight) {
		c_maxHeight = maxHeight;
	}

}
