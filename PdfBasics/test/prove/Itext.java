package prove;

import org.junit.Assert;
import org.junit.Test;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfContentByte;

public class Itext extends CommonTest {

	
	@Test
	public void testLines() {
		try {
			PdfData pdf = createPdfDocument("linesTest");
			Document doc = pdf.document;
			
			PdfContentByte cb = pdf.writer.getDirectContentUnder();
			float y = 500;
			drawLineButt(cb, doc.leftMargin(), y, 16f);
			drawLineButt(cb, doc.leftMargin() + 40, y, 8f);
			drawLineButt(cb, doc.leftMargin() + 80, y, 4f);
			drawLineButt(cb, doc.leftMargin() + 120, y, 2f);
			drawLineButt(cb, doc.leftMargin() + 160, y, 1f);
			drawLineButt(cb, doc.leftMargin() + 200, y, 0.5f);
			drawLineButt(cb, doc.leftMargin() + 240, y, 0.1f);
			
			y -= 100;			
			drawLineSQ(cb, doc.leftMargin(), y, 16f);
			drawLineSQ(cb, doc.leftMargin() + 40, y, 8f);
			drawLineSQ(cb, doc.leftMargin() + 80, y, 4f);
			drawLineSQ(cb, doc.leftMargin() + 120, y, 2f);
			drawLineSQ(cb, doc.leftMargin() + 160, y, 1f);
			drawLineSQ(cb, doc.leftMargin() + 200, y, 0.5f);
			drawLineSQ(cb, doc.leftMargin() + 240, y, 0.1f);
			
			y -= 100;			
			drawLineRound(cb, doc.leftMargin(), y, 16f);
			drawLineRound(cb, doc.leftMargin() + 40, y, 8f);
			drawLineRound(cb, doc.leftMargin() + 80, y, 4f);
			drawLineRound(cb, doc.leftMargin() + 120, y, 2f);
			drawLineRound(cb, doc.leftMargin() + 160, y, 1f);
			drawLineRound(cb, doc.leftMargin() + 200, y, 0.5f);
			drawLineRound(cb, doc.leftMargin() + 240, y, 0.1f);
			
			doc.close();
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.toString());
		}		
	}
	
	private void drawLineButt(PdfContentByte cb, float startX, float startY, float thick) {
		cb.setLineWidth(thick);
		cb.setLineCap(PdfContentByte.LINE_CAP_BUTT);
		cb.moveTo(startX, startY); 
		cb.lineTo(startX + 35, startY); 		
		cb.stroke();
	}

	private void drawLineSQ(PdfContentByte cb, float startX, float startY, float thick) {
		cb.setLineWidth(thick);
		cb.setLineCap(PdfContentByte.LINE_CAP_PROJECTING_SQUARE);
		cb.moveTo(startX, startY); 
		cb.lineTo(startX + 35, startY); 		
		cb.stroke();
	}

	private void drawLineRound(PdfContentByte cb, float startX, float startY, float thick) {
		cb.setLineWidth(thick);
		cb.setLineCap(PdfContentByte.LINE_CAP_ROUND);
		cb.moveTo(startX, startY); 
		cb.lineTo(startX + 35, startY); 		
		cb.stroke();
	}
}
