package elements;

public interface Drawable {

	/**
	 * 
	 * @param container elemento contenitore
	 * @param distX distanza a sinistra da cui iniziare a disegnare
	 * @param distY distanza sopra da cui iniziare a disegnare
	 */
	public void draw(Drawable container, float distX, float distY);
	
	public float getMaxHeight();
	public void setMaxHeight(float h);
	public float getMaxWidth();
	public void setMaxWidth(float h);

	public float getRealHeight();
	public float getRealWidth();

	public float getPaddingTop();
	public float getPaddingLeft();
	public float getPaddingRight();
	public float getPaddingBottom();
}
