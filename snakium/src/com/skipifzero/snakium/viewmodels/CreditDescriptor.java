package com.skipifzero.snakium.viewmodels;

import com.skipifzero.snakium.Assets;
import com.skipifzero.snakium.Const;
import com.skipifzero.snakium.framework.math.BoundingRectangle;
import com.skipifzero.snakium.framework.opengl.FontRenderer.HorizontalAlignment;
import com.skipifzero.snakium.framework.opengl.FontRenderer.VerticalAlignment;

public class CreditDescriptor {
	
	private static final double JOB_SIZE_FACTOR = 0.28;
	private static final double NAME_SIZE_FACTOR = 0.42;
	
	private final BoundingRectangle bounds;
	private final double borderWidth, height;
	private final String job, name;
	
	public CreditDescriptor(double x, double y, double height, double borderWidthFactor, String job, String name) {
		this.bounds = new BoundingRectangle(x, y, height*4, height);
		this.borderWidth = bounds.getWidth()*borderWidthFactor;
		this.height = bounds.getHeight() - 2*borderWidth;
		this.job = job;
		this.name = name;
	}
	
	public void render(Assets assets) {
		//SnakiumUtils.renderBorderComplete(bounds, borderWidth, assets);
		
		assets.font.setHorizontalAlignment(HorizontalAlignment.CENTER);
		assets.font.begin(Const.FONT_COMPLETE_COLOR);
		
		assets.font.setVerticalAlignment(VerticalAlignment.TOP);
		assets.font.draw(bounds.getX(), bounds.getY() + height*JOB_SIZE_FACTOR, height*JOB_SIZE_FACTOR, job);
		assets.font.setVerticalAlignment(VerticalAlignment.BOTTOM);
		assets.font.draw(bounds.getX(), bounds.getY() - height*NAME_SIZE_FACTOR, height*NAME_SIZE_FACTOR, name);
		
		assets.font.render();
	}
}
