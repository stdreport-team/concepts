package prove;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.NumberFormat;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;

import elements.TextBlock;

public class CommonTest {
	protected PdfData createPdfDocument(String baseName)
			throws DocumentException, FileNotFoundException {
		Document doc = new Document(PageSize.A4, 30, 30, 30, 30);
		FileOutputStream fos = new FileOutputStream("c:/temp/pdf/" + baseName
				+ ".pdf");
		PdfWriter w = PdfWriter.getInstance(doc, fos);
		doc.open();

		return new PdfData(doc, w);
	}

	public class PdfData {
		Document document;
		PdfWriter writer;

		PdfData(Document d, PdfWriter w) {
			document = d;
			writer = w;
		}
	}

	protected void drawVertSize(PdfWriter w, TextBlock block)
			throws DocumentException, IOException {
		// posX,posY=bottom position
		float posX = block.getStartX() + block.getRealWidth() + 4;
		float posY = block.getRealLower();
		float height = block.getLastHeight();

		PdfContentByte cb = w.getDirectContentUnder();
		try {
			cb.saveState();
			cb.setLineWidth(0.5f);
			// float[] dash1 = { 1 };
			// cb.setLineDash(dash1, 1);
			cb.setRGBColorStrokeF(0.9f, 0.8f, 0.8f);
			// cb.setLineDash(0.2f);
			cb.moveTo(posX, posY); // bottom
			cb.lineTo(posX, posY + height); // top
			cb.stroke();
			BaseFont f = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252,
					true);
			NumberFormat nf = NumberFormat.getNumberInstance();
			nf.setMaximumFractionDigits(1);
			cb.beginText();
			cb.setFontAndSize(f, 6);
			String sh = String.format("height: real %s / decl %s", nf.format(height), nf.format(block.getHeight()));
			cb.showTextAligned(Element.ALIGN_LEFT, sh, posX + 4, posY + height / 2, 0);
			cb.showTextAligned(Element.ALIGN_LEFT, nf.format(posY), posX + 2, posY, 0);
			cb.showTextAligned(Element.ALIGN_LEFT, nf.format(posY + height),
					posX + 2, posY + height, 0);
			cb.endText();
		} finally {
			cb.restoreState();
		}
	}

	protected void drawHorzSize(PdfWriter w, TextBlock block)
			throws DocumentException, IOException {
		// posX,posY=left position
		float posX = block.getStartX();
		float posY = block.getRealLower() - 4;
		float width = block.getRealWidth();

		PdfContentByte cb = w.getDirectContentUnder();
		try {
			cb.saveState();
			cb.setLineWidth(0.5f);
			// float[] dash1 = { 1 };
			// cb.setLineDash(dash1, 1);
			cb.setRGBColorStrokeF(0.8f, 0.8f, 0.8f);
			// cb.setLineDash(0.2f);
			cb.moveTo(posX, posY); // left
			cb.lineTo(posX + width, posY); // right
			cb.stroke();
			BaseFont f = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252,
					true);
			NumberFormat nf = NumberFormat.getNumberInstance();
			nf.setMaximumFractionDigits(1);
			cb.beginText();
			cb.setFontAndSize(f, 6);
			String sw = String.format("width: real %s / decl %s", nf.format(width), nf.format(block.getWidth()));
			cb.showTextAligned(Element.ALIGN_CENTER, sw, posX + width / 2, posY - 6, 0);
			cb.showTextAligned(Element.ALIGN_LEFT, nf.format(posX), posX, posY - 6, 0);
			cb.showTextAligned(Element.ALIGN_RIGHT, nf.format(posX + width), posX
					+ width, posY - 6, 0);
			cb.endText();
		} finally {
			cb.restoreState();
		}
	}

	protected void drawPaddings(PdfWriter w, TextBlock block)
			throws DocumentException, IOException {
		drawPaddingLeft(w, block);
		drawPaddingRight(w, block);		
	}
	
	protected void drawPaddingLeft(PdfWriter w, TextBlock block)
			throws DocumentException, IOException {
		
		if (block.getPaddingLeft() == 0f)
			return;		

		PdfContentByte cb = w.getDirectContent();
		try {
			// posX,posY=left position
			float posX = block.getStartX();
			float posY = block.getRealLower() - 6;
			float paddingHeight = 36f;
			cb.saveState();
			float tl = 0.08f;
			cb.setLineWidth(tl);
			float[] dash1 = { 1 };
			cb.setLineDash(dash1, 1);
			cb.setRGBColorStrokeF(0f, 0.5f, 0f);
			//linea sinistra
			float x1 = posX + block.getBorderLeftSize();  // + tl;
			cb.moveTo(x1, posY); 
			cb.lineTo(x1, posY + paddingHeight);  
			//linea destra
			float x2 = x1 + block.getPaddingLeft();
			cb.moveTo(x2, posY); 
			cb.lineTo(x2, posY + paddingHeight);  //left padding

			cb.stroke();
		} finally {
			cb.restoreState();
		}
	}

	protected void drawPaddingRight(PdfWriter w, TextBlock block)
			throws DocumentException, IOException {
		
		if (block.getPaddingLeft() == 0f)
			return;		

		PdfContentByte cb = w.getDirectContentUnder();
		try {
			// posX,posY=left position
			float posX = block.getStartX();
			float posY = block.getRealLower() - 6;
			float paddingHeight = 36f;
			cb.saveState();
			float tl = 0.08f;
			cb.setLineWidth(tl);
			float[] dash1 = { 1 };
			cb.setLineDash(dash1, 1);
			cb.setRGBColorStrokeF(0f, 0.5f, 0f);
			//linea sinistra
			float x1 = posX + block.getRealWidth() - block.getBorderLeftSize() - block.getPaddingRight();
			cb.moveTo(x1, posY); 
			cb.lineTo(x1, posY + paddingHeight);  
			//linea destra
			float x2 = x1 + block.getPaddingRight();
			cb.moveTo(x2, posY); 
			cb.lineTo(x2, posY + paddingHeight);  //left padding

			cb.stroke();
		} finally {
			cb.restoreState();
		}
	}
	
	protected void drawGridX(Document doc, PdfWriter w, float startTop, float step) {
		PdfContentByte cb = w.getDirectContentUnder();
		cb.setLineWidth(0.1f);
		cb.setRGBColorStrokeF(0.7f, 0.7f, 0.7f);
		float y = startTop;
		while (y > 0) {
			cb.moveTo(doc.leftMargin(), y); // ll
			cb.lineTo(doc.right() - doc.rightMargin(), y); // ul
			cb.stroke();
			y -= step;
		}
	}

	protected void drawPageMargins(Document doc, PdfWriter w) {
		PdfContentByte cb = w.getDirectContentUnder();
		try {
			cb.saveState();
			cb.setLineWidth(0.1f);
			float[] dash1 = { 1 };
			cb.setLineDash(dash1, 1);
			cb.setRGBColorStrokeF(0.7f, 0.7f, 1f);
			cb.moveTo(doc.leftMargin(), doc.bottom()); // ll
			cb.lineTo(doc.leftMargin(), doc.top()); // ul
			cb.lineTo(doc.right(), doc.top()); // ur
			cb.lineTo(doc.right(), doc.bottom()); // lr
			cb.closePath();
			cb.stroke();
		} finally {
			cb.restoreState();
		}
	}

}
