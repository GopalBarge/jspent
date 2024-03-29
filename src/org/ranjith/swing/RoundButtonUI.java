package org.ranjith.swing;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicButtonUI;

/**
 * A UI delegate for round button/ 
 * @author ranjith
 */
public class RoundButtonUI extends BasicButtonUI{
    private static final RoundButtonUI INSTANCE = new RoundButtonUI();
    public static final int ARC = 20;
    public static ComponentUI createUI(JComponent b) {
        return INSTANCE;
    }  
    protected Color getSelectColor() {
        return new Color(183, 234, 98);
    }
   public void paint(Graphics g, JComponent c) {

		AbstractButton vButton = (AbstractButton) c;
		ButtonModel vButtonModel = vButton.getModel();

		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		int w = c.getWidth();
		int h = c.getHeight();

		int x = 0;
		int y = 0;

		boolean isPressed = vButtonModel.isArmed() && vButtonModel.isPressed();
		boolean isRollover = vButtonModel.isRollover();
		boolean isSelected = vButtonModel.isSelected();

		paintButtonBody(g2d, vButton, x, y, w, h, isPressed, isRollover, isSelected);
		paintButtonBorder(g2d, x, y, w, h, isPressed, isRollover, isSelected);
		paintText(g2d,vButton.getText(),vButton.getForeground(),w,h,isPressed, isRollover, isSelected);
		g2d.dispose();
    }    
    
    protected void paintFocus(Graphics g, AbstractButton b, Rectangle viewRect, Rectangle textRect, Rectangle iconRect) {
        return;
    }    
    
    private void paintButtonBody(Graphics2D g2,AbstractButton button, int x, int y, int width, int height, boolean isPressed, boolean isRollover, boolean isSelected) {
        Paint oldPaint = g2.getPaint();
        //set color
        g2.setColor(new Color(0,0,0,220));
        //set stroke of 3f for border
        g2.setStroke(new BasicStroke(1.3f));
        //set rendering hint for anti alias
        //fill rounded rectangle
        if(!isPressed) {
        	g2.setPaint(new GradientPaint(x,y,Color.LIGHT_GRAY,x,height,Color.BLACK));
        }else {
        	g2.setPaint(new GradientPaint(x,y,Color.BLACK,x,height,Color.LIGHT_GRAY));
        }
        	//g2.fillRect(0, 0, getWidth(), getHeight());
        g2.fillRoundRect(1,1,width-3,height-3,ARC,ARC);
        g2.setPaint(oldPaint);
        paintIcon(g2, button, isRollover,isSelected);
    }
    private void paintIcon(Graphics2D g2, AbstractButton b, boolean isRollover,
            boolean isSelected) {
        int x;
        int y;
        Icon vIcon = null;

        if (isSelected && isRollover)
        {
            vIcon = b.getRolloverSelectedIcon();
        }
        else if (isSelected)
        {
            vIcon = b.getSelectedIcon();
        }
        else if (isRollover)
        {
            vIcon = b.getRolloverIcon();
        }

        if (vIcon == null)
        {
            vIcon = b.getIcon();
        }

        if (vIcon != null)
        {
            x = (b.getWidth() - vIcon.getIconWidth()) / 2 + 1;
            y = (b.getHeight() - vIcon.getIconHeight()) / 2;

            Composite vComposite = g2.getComposite();
            if (!b.isEnabled())
            {
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .5f));
            }

            vIcon.paintIcon(b, g2, x, y);

            if (!b.isEnabled())
            {
                g2.setComposite(vComposite);
            }
        }
    }    
    private void paintButtonBorder(Graphics2D g2,int x, int y, int width, int height, boolean isPressed, boolean isRollover, boolean isSelected) {
    	g2.setColor(Color.WHITE);
    	g2.drawRoundRect(1,1,width-3,height-3,ARC,ARC);
    }
    private void paintText( Graphics g, String text, Color color, int w, int h, boolean isPressed, boolean isRollover, boolean isSelected) {
        // String size
        FontMetrics fm = g.getFontMetrics();
        int swid = fm.stringWidth( text );
        int shgt = fm.getHeight();
        
        int x = (w-swid)/2;
        int y = (h - shgt)/2 + fm.getAscent();
        if(isPressed) {
        	y +=1;
        }
        Graphics2D g2d = (Graphics2D) g;
        g2d.translate( x, y );
        g2d.setColor(color);
	    int offset =  0;
        g2d.drawString( text, offset, offset );
    }

}
