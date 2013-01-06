package elements;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfWriter;

public class DrawContext {

	private PdfWriter c_writer;
	private Document c_document;
	
	public DrawContext(PdfWriter writer, Document document) {
		c_writer = writer;
		c_document = document;
	}

	public PdfWriter getWriter() {
		return c_writer;
	}

	public Document getDocument() {
		return c_document;
	}
	
	public float getDocumentAvailWidth() {
		return c_document.getPageSize().getWidth() - c_document.leftMargin()
				- c_document.rightMargin();
	}
}
