package prove;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import org.junit.Assert;
import org.junit.Test;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import elements.TextBlock;
import elements.TextBlock.GrowDirection;

public class Tests {
	private PdfData createPdfDocument(String baseName)
			throws DocumentException, FileNotFoundException {
		Document doc = new Document(PageSize.A4, 30, 30, 30, 30);
		FileOutputStream fos = new FileOutputStream("c:/temp/pdf/" + baseName
				+ ".pdf");
		PdfWriter w = PdfWriter.getInstance(doc, fos);
		doc.open();

		return new PdfData(doc, w);
	}

	private class PdfData {
		Document document;
		PdfWriter writer;

		PdfData(Document d, PdfWriter w) {
			document = d;
			writer = w;
		}
	}

	@Test
	public void testPhrases() {
		try {
			PdfData pdf = createPdfDocument("provaPhrases");
			Document doc = pdf.document;
			Font helv32 = new Font(FontFamily.HELVETICA, 20, Font.BOLD);
			
			Phrase c = new Phrase("Frase 1", helv32);			
			doc.add(c);

			c = new Phrase("Frase 2", helv32);			
			doc.add(c);
			
			doc.close();
		} catch (Exception e) {
			Assert.fail(e.toString());
		}
	}
	
	@Test
	public void testChunk() {
		try {
			PdfData pdf = createPdfDocument("provaChunks");
			Document doc = pdf.document;
			Font helv32 = new Font(FontFamily.HELVETICA, 40, Font.BOLD);
			
			Chunk c = new Chunk("Test chunk STROKE", helv32);			
			c.setTextRenderMode( PdfContentByte.TEXT_RENDER_MODE_STROKE, 0.5f, BaseColor.MAGENTA);			
			doc.add(new Paragraph(c));

			c = new Chunk("Test chunk CLIP", helv32);			
			c.setTextRenderMode( PdfContentByte.TEXT_RENDER_MODE_CLIP, 0.5f, BaseColor.MAGENTA);
			doc.add(new Paragraph(c));
			
			c = new Chunk("Test chunk FILL", helv32);			
			c.setTextRenderMode( PdfContentByte.TEXT_RENDER_MODE_FILL, 0.5f, BaseColor.MAGENTA);
			doc.add(new Paragraph(c));

			c = new Chunk("Test chunk FILL", helv32);			
			c.setTextRenderMode( PdfContentByte.TEXT_RENDER_MODE_FILL, 0.5f, BaseColor.MAGENTA);
			c.setCharacterSpacing(2f);
			doc.add(new Paragraph(c));
			
			c = new Chunk("Test chunk FILL-STROKE", helv32);			
			c.setTextRenderMode( PdfContentByte.TEXT_RENDER_MODE_FILL_STROKE, 1.5f, BaseColor.ORANGE);
			doc.add(new Paragraph(c));

			doc.close();
		} catch (Exception e) {
			Assert.fail(e.toString());
		}
	}
	
	
	@Test
	public void testColumnText() {
		try {
			Font times20 = new Font(FontFamily.TIMES_ROMAN, 20);
			Font helv18 = new Font(FontFamily.HELVETICA, 18);
			Font helv11 = new Font(FontFamily.HELVETICA, 11);
			helv11.setColor(BaseColor.BLUE);
			
			PdfData pdf = createPdfDocument("provaColumntext");
			Document doc = pdf.document;
			
			TextBlock col2 = new TextBlock(doc.left(30), doc.top(30) - 200, 200, 150);
			col2.setBorderSize(0.8f);
			col2.setFont(helv18);
			col2.setPadding(2);
			col2.setGrowType(GrowDirection.BOTH);
			col2.addElement(new Phrase(
					"Tanto va la gatta al lardo\n che ci lascia lo zampino."));
			col2.addElement(new Phrase("L'ultimo che esce chiude la porta"));
			col2.addElement(new Phrase(
					"Meglio un uovo oggi che una gallina domani."));
			col2.addElement(new Phrase("Chi trova un amico trova un tesoro."));
			Phrase p = new Phrase("Chi va con lo zoppo");
			Chunk c = new Chunk(" impara a zoppicare.", helv11);
			c.setHorizontalScaling(3.5f);
			p.add(c);
			col2.addElement(p);
			col2.addElement(new Phrase("A caval donato non si guarda in bocca."));
			col2.addElement(new Phrase(
					"Chi è causa del suo mal, pianga se stesso."));
			col2.addElement(new Phrase(
					"Il mondo è fatto a scale, c'è chi scende e c'è chi sale."));
			draw(col2, pdf.writer);
			
			drawGridX(doc, pdf.writer, col2.getLly() + col2.getHeight(), 20);

			col2.setLly(doc.top(30) - 480);			
			col2.setFont(times20);
			draw(col2, pdf.writer);
			
			doc.newPage();
			
			col2.setGrowType(GrowDirection.NONE);
			col2.setLly(doc.top(30) - 400);

			draw(col2, pdf.writer);
//			
//			
//			TextBlock tb = new TextBlock(doc.left(30), doc.top(30) - 200, 200, 150);
//			tb.setBorderSize(0.8f);
//			tb.setFont(helv18);
//			tb.setPadding(2);
//			tb.setGrowType(GrowDirection.BOTH);
//			tb.addElement(new Phrase(
//					"Tanto va la gatta al lardo\n che ci lascia lo zampino."));
//			tb.addElement(new Chunk("Chunk+Chunk.", helv11));
//			tb.addElement(new Phrase("L'ultimo che esce chiude la porta"));
//			tb.addElement(new Phrase(
//					"Meglio un uovo oggi che una gallina domani."));
//			tb.addElement(new Phrase("Chi trova un amico trova un tesoro."));
//			tb.addElement(new Phrase(
//					"Chi va con lo zoppo impara a zoppicare."));
//			tb.addElement(new Phrase("A caval donato non si guarda in bocca."));
//			tb.addElement(new Chunk("Chunk+Chunk.", helv11));
//			tb.addElement(new Phrase(
//					"Chi è causa del suo mal, pianga se stesso."));
//			tb.addElement(new Phrase(
//					"Il mondo è fatto a scale, c'è chi scende e c'è chi sale."));
//			draw(tb, pdf.writer);
						
			doc.close();
		} catch (Exception e) {
			Assert.fail(e.toString());
		}
	}
	
	private void drawGridX(Document doc, PdfWriter w, float startTop, float step) {
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
	
	private void draw(TextBlock block, PdfWriter w) throws DocumentException {
		block.draw(w);
		System.out.println("#lines: " + block.getLinesWritten());
		System.out.println("#real height: " + block.getRealHeight());
		System.out.println("#real width: " + block.getRealWidth());		
	}

}
