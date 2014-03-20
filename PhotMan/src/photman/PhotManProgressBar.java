package photman;

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.BoundedRangeModel;
import javax.swing.JProgressBar;

/**
 * <p>
 * This class defines a new progress bar.
 * </p>
 * <p>
 * Copyright: Copyright (c) 2014
 * </p>
 * <pre>
 * Change history:
 *   2014-03-01 GEB  Initial coding.
 * </pre>
 * @author Gérald Eberle (GEB)
 */
public class PhotManProgressBar extends JProgressBar {
	private static final long serialVersionUID = -7984348277214193578L;

	private Color m_beginColor = new Color(0,200,128);
	private Color m_endColor = new Color(200,250,160);
	private GradientPaint m_gradient;
	private Font m_boldFont = new Font("Verdana",Font.BOLD,10);

	/**
	 * Constructor of the class.
	 */
	public PhotManProgressBar() {
		super();
	}

	/**
	 * Constructor of the class.
	 * @param orient the bar orientation
	 */
	public PhotManProgressBar(int orient) {
		super(orient);
	}

	/**
	 * Constructor of the class.
	 * @param min the bar minimal value
	 * @param max the bar maximal value
	 */
	public PhotManProgressBar(int min, int max) {
		super(min,max);
	}

	/**
	 * Constructor of the class.
	 * @param orient the bar orientation
	 * @param min the bar minimal value
	 * @param max the bar maximal value
	 */
	public PhotManProgressBar(int orient, int min, int max) {
		super(orient,min,max);
	}

	/**
	 * Constructor of the class.
	 * @param newModel the new model for the bar
	 */
	public PhotManProgressBar(BoundedRangeModel newModel) {
		super(newModel);
	}

	/**
	 * Overridden painting method.
	 */
	@Override
	public void paint(Graphics g) {
		m_gradient = new GradientPaint(0,0,m_beginColor,0,getHeight(),m_endColor,true);
		Graphics2D g2d = (Graphics2D) g;

		g2d.setColor(Color.LIGHT_GRAY);
		g2d.fillRect(0,0,getWidth()-1,getHeight()-2);

		g2d.setPaint(m_gradient);
		g2d.fillRect(1,1,getWidth()*getValue()/getMaximum()-2,getHeight()-3);

		g2d.setColor(Color.GRAY);
		g2d.drawRoundRect(0,0,getWidth()*getValue()/getMaximum()-1,getHeight()-2, 4, 4);

		g2d.setColor(Color.DARK_GRAY);
		g2d.draw3DRect(0,0,getWidth()-1,getHeight()-2,false);    

		g2d.setColor(Color.DARK_GRAY);
		g2d.setFont(m_boldFont);
		g2d.drawString(getString(),(getWidth()-(g2d.getFontMetrics().stringWidth(getString())))/2,12);
	}
}
