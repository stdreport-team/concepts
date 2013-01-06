package elements;

public abstract class BlockElement implements Drawable {
	private float					c_maxWidth;
	private float					c_maxHeight;
	private Border				c_border;
	private Border				c_borderLeft;
	private Border				c_borderRight;
	private Border				c_borderBottom;
	private Border				c_borderTop;
	private float					c_paddingLeft;
	private float					c_paddingRight;
	private float					c_paddingTop;
	private float					c_paddingBottom;
	private BlockElement	c_parent;
	private GrowDirection	c_growType;

	public enum GrowDirection {
		NONE, HORIZ, VERT, BOTH
	}

	/**
	 * The maximum height this block of text. If the text doesn't fit the
	 * width/height settings, the text is clipped.
	 * 
	 * @return the maximum total height (in points) that this text block can
	 *         reach.
	 */
	@Override
	public float getMaxHeight() {
		return c_maxHeight;
	}

	@Override
	public void setMaxHeight(float maxHeight) {
		c_maxHeight = maxHeight;
	}

	@Override
	public float getMaxWidth() {
		return c_maxWidth;
	}

	@Override
	public void setMaxWidth(float maxWidth) {
		c_maxWidth = maxWidth;
	}

	public GrowDirection getGrowType() {
		return c_growType;
	}

	public void setGrowType(GrowDirection growType) {
		c_growType = growType;
	}

	public Border getBorder() {
		return c_border;
	}

	public void setBorder(Border border) {
		c_border = border;
	}

	public BlockElement getParent() {
		return c_parent;
	}

	public void setParent(BlockElement parent) {
		c_parent = parent;
	}

	public float getBorderSize() {
		return c_border == null ? 0 : c_border.getSize();
	}

	public Border getBorderLeft() {
		return c_borderLeft;
	}

	public float getBorderLeftSize() {
		return c_borderLeft == null ? getBorderSize() : c_borderLeft.getSize();
	}

	public float getBorderRightSize() {
		return c_borderRight == null ? getBorderSize() : c_borderRight.getSize();
	}

	public float getBorderTopSize() {
		return c_borderTop == null ? getBorderSize() : c_borderTop.getSize();
	}

	public float getBorderBottomSize() {
		return c_borderBottom == null ? getBorderSize() : c_borderBottom.getSize();
	}

	public void setBorderLeft(Border borderLeft) {
		c_borderLeft = borderLeft;
	}

	public Border getBorderRight() {
		return c_borderRight;
	}

	public void setBorderRight(Border borderRight) {
		c_borderRight = borderRight;
	}

	public Border getBorderBottom() {
		return c_borderBottom;
	}

	public void setBorderBottom(Border borderBottom) {
		c_borderBottom = borderBottom;
	}

	public Border getBorderTop() {
		return c_borderTop;
	}

	public void setBorderTop(Border borderTop) {
		c_borderTop = borderTop;
	}

	public void setPadding(float padding) {
		c_paddingLeft = padding;
		c_paddingRight = padding;
		c_paddingTop = padding;
		c_paddingBottom = padding;
	}
	
	
	public float getPaddingLeft() {
		return c_paddingLeft;
	}

	public void setPaddingLeft(float padding) {
		c_paddingLeft = padding;
	}

	public float getPaddingRight() {
		return c_paddingRight;
	}

	public void setPaddingRight(float paddingRight) {
		c_paddingRight = paddingRight;
	}

	public float getPaddingTop() {
		return c_paddingTop;
	}

	public void setPaddingTop(float paddingTop) {
		c_paddingTop = paddingTop;
	}

	public float getPaddingBottom() {
		return c_paddingBottom;
	}

	public void setPaddingBottom(float paddingBottom) {
		c_paddingBottom = paddingBottom;
	}
	
}
