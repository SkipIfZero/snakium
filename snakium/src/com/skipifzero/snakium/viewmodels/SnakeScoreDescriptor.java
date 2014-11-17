package com.skipifzero.snakium.viewmodels;

import com.skipifzero.snakium.Assets;
import com.skipifzero.snakium.BitmapFontRenderer;
import com.skipifzero.snakium.Const;
import com.skipifzero.snakium.SnakeScore;
import com.skipifzero.snakium.framework.math.BoundingRectangle;
import com.skipifzero.snakium.framework.opengl.FontRenderer.HorizontalAlignment;
import com.skipifzero.snakium.framework.opengl.FontRenderer.VerticalAlignment;

public class SnakeScoreDescriptor {
	
	private static final double SCORE_SIZE_FACTOR = 0.6;
	private static final double DATE_SIZE_FACTOR = 0.9 - SCORE_SIZE_FACTOR;
	
	private final BoundingRectangle bounds;
	private SnakeScore score;
	
	private final double borderWidth, scoreTextSize, dateTextSize;
	
	public SnakeScoreDescriptor(double x, double y, double height, double borderWidthFactor, SnakeScore score) {
		this.bounds = new BoundingRectangle(x, y, height*3, height);
		this.score = score;
		
		this.borderWidth = borderWidthFactor*bounds.getWidth();
		this.scoreTextSize = (bounds.getHeight()-2*borderWidth)*SCORE_SIZE_FACTOR;
		this.dateTextSize = (bounds.getHeight()-2*borderWidth)*DATE_SIZE_FACTOR;
	}
	
	public void render(Assets assets) {
		
		//SnakiumUtils.renderBorderComplete(bounds, borderWidth, assets);
		
		if(score != null) {
			BitmapFontRenderer font = assets.font;

			font.setHorizontalAlignment(HorizontalAlignment.LEFT);
			font.begin(Const.FONT_COMPLETE_COLOR);
			
			font.setVerticalAlignment(VerticalAlignment.TOP);
			font.draw(bounds.getX() - bounds.getWidth()/2 + borderWidth, bounds.getY() + bounds.getHeight()/2 - borderWidth, scoreTextSize, score.getScore() + " pts");
			
			font.setVerticalAlignment(VerticalAlignment.BOTTOM);
			font.draw(bounds.getX() - bounds.getWidth()/2 + borderWidth, bounds.getY() - bounds.getHeight()/2 + borderWidth, dateTextSize, score.getDateTimeString());
			
			font.render();
		}
	}
	
	public void setScore(SnakeScore score) {
		this.score = score;
	}
	
	public SnakeScore getScore() {
		return score;
	}
	
	public boolean isVisible() {
		return score != null;
	}
	
	public BoundingRectangle getBounds() {
		return bounds;
	}
}
