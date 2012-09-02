package prove;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;

import elements.TextBlock;
import elements.TextBlock.GrowDirection;

public class Tests extends CommonTest {
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
			draw(col2, pdf.writer, doc);
			
			drawGridX(doc, pdf.writer, col2.getLly() + col2.getHeight(), 20);

			col2.setLly(doc.top(30) - 480);			
			col2.setFont(times20);
			draw(col2, pdf.writer, doc);
			
			doc.newPage();
			
			col2.setGrowType(GrowDirection.NONE);
			col2.setLly(doc.top(30) - 400);

			draw(col2, pdf.writer, doc);
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

	@Test
	public void testColumnTextOnTwoPages() {
		try {
			Font times20 = new Font(FontFamily.TIMES_ROMAN, 20);
			Font helv18 = new Font(FontFamily.HELVETICA, 18);
			Font helv11 = new Font(FontFamily.HELVETICA, 11);
			helv11.setColor(BaseColor.BLUE);
			
			PdfData pdf = createPdfDocument("provaColumntextPages");
			Document doc = pdf.document;
			
			TextBlock block = new TextBlock(doc.left(30), doc.bottomMargin() + 50, 200, 100);
			block.setBorderSize(0.8f);
			block.setFont(helv18);
			block.setPadding(2);
			block.setGrowType(GrowDirection.BOTH);
			block.addElement(new Phrase(
					"Tanto va la gatta al lardo\n che ci lascia lo zampino."));
			block.addElement(new Phrase("L'ultimo che esce chiude la porta"));
			block.addElement(new Phrase(
					"Meglio un uovo oggi che una gallina domani."));
			block.addElement(new Phrase("Chi trova un amico trova un tesoro."));
			Phrase p = new Phrase("Chi va con lo zoppo");
			Chunk c = new Chunk(" impara a zoppicare.", helv11);
			c.setHorizontalScaling(3.5f);
			p.add(c);
			block.addElement(p);
			block.addElement(new Phrase("A caval donato non si guarda in bocca."));
			block.addElement(new Phrase(
					"Chi è causa del suo mal, pianga se stesso."));
			block.addElement(new Phrase(
					"Il mondo è fatto a scale, c'è chi scende e c'è chi sale."));
			draw(block, pdf.writer, doc);
			
			//drawGridX(doc, pdf.writer, block.getLly() + block.getHeight(), 20);
			doc.close();
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.toString());
		}
	}
	
	private void draw(TextBlock block, PdfWriter w, Document d) throws DocumentException, IOException {
		if (!block.write(w, d)) {
			drawMargins(d, w);
			drawVertSize(w, block);
			drawHorzSize(w, block);
			drawPaddings(w, block);
			d.newPage();
			block.write(w, d);
		}
		drawMargins(d, w);
		drawVertSize(w, block);
		drawHorzSize(w, block);
		System.out.println("#lines: " + block.getLinesWritten());
		System.out.println("#real height: " + block.getLastHeight());
		System.out.println("#real width: " + block.getRealWidth());		
	}

}
