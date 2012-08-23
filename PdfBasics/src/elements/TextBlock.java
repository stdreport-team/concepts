package elements;

import java.util.ArrayList;
import java.util.List;

import com.itextpdf.text.Chunk;
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
	private float c_llx;
	private float c_lly;
	private float c_width;
	private float c_height;
	private float c_borderSize;
	private float c_borderColor;
	private float c_padding;
	private Font c_font;

	private transient int c_linesWritten;
	private transient float c_realHeight;
	private transient float c_realWidth;

	private List<Element> c_elems;

	public enum GrowDirection {
		NONE, HORIZ, VERT, BOTH
	}

	private GrowDirection c_growType;

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

	public void draw(PdfWriter w) throws DocumentException {
		PdfContentByte cb = w.getDirectContent();
		ColumnText column = new ColumnText(cb);
		for (int i = 0; i < c_elems.size(); i++) {
			Element e = c_elems.get(i);
			if (e instanceof Phrase)
				((Phrase)e).setFont(c_font);
			column.addElement(e);
		}
		column.setLeading(c_font.getSize() * 1.5f);
		drawText(column);
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

	private float getMaxWidth() {
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

	private void drawText(ColumnText c) throws DocumentException {
		c_linesWritten = 0;
		c_realWidth = 0;
		c_realHeight = 0;
		if (getGrowType() == GrowDirection.HORIZ
				|| getGrowType() == GrowDirection.BOTH) {
			// in case of horizontal auto-width, adjust the width to contain the
			// largest text line
			float maxWidth = getMaxWidth();
			if (c_width < maxWidth)
				c_width = maxWidth;
		}
		float x1 = c_llx;
		float y1 = c_lly;
		float x2 = c_llx + c_width;
		float y2 = c_lly + c_height;
		int status = ColumnText.START_COLUMN;
		while (ColumnText.hasMoreText(status)) {
			c.setSimpleColumn(x1, y1, x2, y2);
			c.setUseAscender(true);
			status = c.go();
			c_linesWritten += c.getLinesWritten();
			c_realWidth = Math.max(c_realWidth, c.getFilledWidth());
			c_realHeight += c.getLinesWritten() * c.getLeading();   // c_lly + c_height  - c.getYLine();				
			float fw = c.getFilledWidth();
			if (fw > c_width)
				System.out
						.println("WARNING: some text can be cutted out the text block ("
								+ (fw - c_width) + "pt)");

			if (getGrowType() == GrowDirection.NONE) {
					break; 
			} else if ((getGrowType() == GrowDirection.VERT
					|| getGrowType() == GrowDirection.BOTH)
					&& ColumnText.hasMoreText(status)) {
				// need to extend vertically: adjust "y" of lower left and upper
				// right points
				float dist = c.getLinesWritten() * c.getLeading() - c_height;
				y1 -= c_height + dist;
				if (y1 < 0)
					y1 = 0;
				y2 -= c_height + dist;
				if (y2 < 0)
					y2 = 0;
			}
			
		}
		y1 = c.getYLine() - c_padding + c.getDescender();
		y2 = c_lly + c_height + c_padding;
		c_realHeight = y2 - y1;

		PdfContentByte cb = c.getCanvas();
		if (c_borderSize > 0) {
			x1 -= c_padding;
			x2 = x1 + c_width + c_padding * 2;
			cb.setLineWidth(c_borderSize);
			cb.setRGBColorStrokeF(0.3f, 0.17f, 0.5f);
			c.getCanvas().moveTo(x1, y1); // ll
			c.getCanvas().lineTo(x1, y2); // ul
			c.getCanvas().lineTo(x2, y2); // ur
			c.getCanvas().lineTo(x2, y1); // lr
			c.getCanvas().closePath();
			c.getCanvas().stroke();
			c_realHeight += c_borderSize*2;
		}
	}

	private float getLastlineExtraspace(ColumnText c) {
		if (c.getCompositeElements().size() > 0) {
			Element e = c.getCompositeElements().get(c.getCompositeElements().size() - 1);
			if (e instanceof Phrase) {
				return ((Phrase) e).getLeading() - ((Phrase) e).getFont().getSize();
			}
		}
		return c.getLeading() - c_font.getSize();
	}
	
	public int getLinesWritten() {
		return c_linesWritten;
	}

	public float getRealWidth() {
		return c_realWidth;
	}

	public float getRealHeight() {
		return c_realHeight;
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

}
