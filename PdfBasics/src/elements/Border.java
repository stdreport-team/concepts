package elements;

import com.itextpdf.text.BaseColor;

public class Border {

	private float c_size;
	private BaseColor c_color;
	private Style c_style = Style.solid;
	
	public enum Style {
		solid, dotted, dashed
	}
	
	public Border(float size, BaseColor color) {
		c_size = size;
		c_color = color;
	}
	
	public float getSize() {
		return c_size;
	}
	public void setSize(float size) {
		c_size = size;
	}
	public BaseColor getColor() {
		return c_color;
	}
	public void setColor(BaseColor color) {
		c_color = color;
	}

	public Style getStyle() {
		return c_style;
	}

	public void setStyle(Style style) {
		c_style = style;
	}
}
