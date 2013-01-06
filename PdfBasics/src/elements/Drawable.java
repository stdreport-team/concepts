package elements;

import com.itextpdf.text.DocumentException;

/**
 * Interface that every generic report element must implements.
 */
public interface Drawable {

	/**
	 * Perform actual drawing of this element on the report document, taking into account the properties
	 * regarding size and positioning.
	 * <p>
	 * If the element's content doesn't fit the current page, or is 
	 * higher than {@link #getMaxHeight() maxHeight} property, it writes as much as content is possible and stops
	 * before the limit is reached.
	 * <p>
	 * If the content spawns across multiple pages and the current page is not the last one, the method return true
	 * indicating that the text needs subsequent calls to this method to write in its entirety.
	 *  
	 * @param context current context with the necessary properties to the drawing
	 * 
	 * @return true if the drawing has finished, false if some content remains to be
	 *         written on the next page; note that it returns true also if the text
	 *         hasn't completely written but all the available height has been
	 *         used (see {@link #getMaxHeight() maxHeight} property).
	 * @throws DocumentException on low level errors during writing on the pdf document
	 */
	public boolean draw(DrawContext context) throws DocumentException;
	
	/**
	 * It's the maximum height of this element. If it's 0 or negative,
	 * there isn't a max width, so the limit derive from the parent width.
	 * If the element's content doesn't fit the max height settings,
	 * the content is clipped.
	 * 
	 * @return the maximum height (in points) allowed for this element or 0/negative for 
	 *  no defined maximum
	 *  
	 *  @see #setMaxHeight(float)
	 */
	public float getMaxHeight();

	/**
	 * Set the maximum allowed height for this element
	 * @param h maximum height in points; set to 0 or less for no explicit maximum 
	 * 
	 * @see #getMaxHeight()
	 */
	public void setMaxHeight(float h);
	
	/**
	 * It's the maximum width of this element. If it's 0 or negative,
	 * there isn't a max width, so the limit derive from the parent width.
	 * 
	 * @return the maximum width (in points) allowed for this element or 0/negative for 
	 *  no defined maximum
	 *  
	 *  @see #setMaxWidth(float)
	 */
	public float getMaxWidth();
	
	/**
	 * Set the maximum allowed width for this element
	 * @param h maximum width in points; set to 0 or less for no explicit maximum 
	 *    (will be calculated from the parent's width)
	 * 
	 * @see #getMaxWidth()
	 */
	public void setMaxWidth(float h);

	public float getRealHeight();

	/**
	 * This property indicates if this element can adjust its width to write its actual content
	 * on the document. The amount of horizontal growing space is calculated from the drawing context,
	 * the size properties, the position on the page and the parent element properties.  
	 * 
	 * @return true if this element can extends its width to contain actual content
	 */
	public boolean isCanGrowX();

	/**
	 * This property indicates if this element can adjust its height to write its actual content
	 * on the document. The amount of vertical growing space is calculated from the drawing context,
	 * the size properties, the position on the page and the parent element properties.  
	 * 
	 * @return true if this element can extends its width to contain actual content
	 */
	public boolean isCanGrowY();
	
	/**
	 * 
	 * @return
	 */
	public float getRealWidth();

	public float getPaddingTop();
	public float getPaddingLeft();
	public float getPaddingRight();
	public float getPaddingBottom();
}
