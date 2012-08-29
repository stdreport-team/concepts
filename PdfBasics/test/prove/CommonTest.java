package prove;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;



import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;

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

}
