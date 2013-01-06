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

import elements.BlockElement.GrowDirection;
import elements.Border;
import elements.DrawContext;
import elements.TextBlock;

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
			
			PdfData pdf = createPdfDocument("provaColumntext");
			Document doc = pdf.document;
			
			TextBlock tb = new TextBlock(doc.left(10), doc.top(10), 200, 150);
			tb.setBorder(new Border(0.8f, new BaseColor(120, 89, 78)));
			tb.setFont(helv18);
			tb.setPadding(2);
			tb.setGrowType(GrowDirection.BOTH);
			setText(tb);			
			draw(tb, pdf.writer, doc);
			

			tb.setStartY(tb.getStartY() - tb.getLastHeight() - 30);		
			tb.setStartX(tb.getStartX() + 10);
			tb.setFont(times20);
			tb.setPadding(10);
			draw(tb, pdf.writer, doc);

			tb.setStartY(tb.getStartY() - tb.getLastHeight() - 30);		
			tb.setStartX(tb.getStartX() + 10);
			tb.setFont(new Font(FontFamily.TIMES_ROMAN, 14));
			tb.setPaddingRight(2);
			tb.setPaddingTop(1);
			setText2(tb);
			draw(tb, pdf.writer, doc);
			
			doc.newPage();
			
			tb.setWidth(100);
			tb.setGrowType(GrowDirection.NONE);
			tb.setStartY(doc.top(20));
			draw(tb, pdf.writer, doc);
			
			
			drawGridX(doc, pdf.writer, tb.getStartY() + tb.getHeight(), 20);			
			doc.close();
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.toString());
		}
	}

	private void setText(TextBlock block) {
		block.clearContent();
		Font helv11 = new Font(FontFamily.HELVETICA, 11);
		helv11.setColor(BaseColor.BLUE);

		Font helv12 = new Font(FontFamily.HELVETICA, 12);
		helv12.setColor(BaseColor.CYAN);
		
		block.addElement(new Phrase("Tanto va la gatta al lardo\n che ci lascia lo zampino."));
		block.addElement(new Phrase("L'ultimo che esce chiude la porta"));
		block.addElement(new Phrase("Meglio un uovo oggi che una gallina domani."));
		block.addElement(new Phrase("Chi trova un amico trova un tesoro."));
		Phrase p = new Phrase("Chi va con lo zoppo");
		Chunk c = new Chunk(" impara a zoppicare.", helv11);
		c.setHorizontalScaling(1.5f);
		p.add(c);
		block.addElement(p);
		block.addElement(new Phrase("A caval donato non si guarda in bocca.", helv12));
		block.addElement(new Phrase("Chi è causa del suo mal, pianga se stesso."));

		Font cou12 = new Font(FontFamily.COURIER, 10, Font.BOLD);
		cou12.setColor(BaseColor.MAGENTA);
		block.addElement(new Phrase("Il mondo è fatto a scale, c'è chi scende e c'è chi sale.", cou12));		
	}
	
	private void setText2(TextBlock block) {
		block.clearContent();
		block.addElement(new Phrase("Tanto va la gatta al lardo\n che ci lascia lo zampino."));
		block.addElement(new Phrase("L'ultimo che esce chiude la porta"));
		block.addElement(new Phrase("Meglio un uovo oggi che una gallina domani."));
	}
	
	@Test
	public void testColumnTextOnTwoPages() {
		try {
			Font helv18 = new Font(FontFamily.HELVETICA, 18);
			
			PdfData pdf = createPdfDocument("provaColumntextPages");
			Document doc = pdf.document;
			
			TextBlock block = new TextBlock(doc.left(30), doc.bottomMargin() + 50, 200, 100);
			block.setBorder(new Border(0.8f, new BaseColor(120, 89, 78)));
			block.setFont(helv18);
			block.setPadding(2);
			block.setGrowType(GrowDirection.BOTH);
			setText(block);
			draw(block, pdf.writer, doc);
			
			//drawGridX(doc, pdf.writer, block.getLly() + block.getHeight(), 20);
			doc.close();
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.toString());
		}
	}
	
	private void draw(TextBlock block, PdfWriter w, Document d) throws DocumentException, IOException {
		DrawContext dc = new DrawContext(w, d);
		block.resetDrawStatus();
		while (!block.draw(dc)) {
			drawPageMargins(d, w);
			drawVertSize(w, block);
			drawHorzSize(w, block);
			drawPaddings(w, block);
			d.newPage();
		}
		drawPageMargins(d, w);
		drawVertSize(w, block);
		drawHorzSize(w, block);
		drawPaddings(w, block);
		System.out.println("#lines: " + block.getLinesWritten());
		System.out.println("#real height: " + block.getLastHeight());
		System.out.println("#real width: " + block.getRealWidth());		
	}

}
