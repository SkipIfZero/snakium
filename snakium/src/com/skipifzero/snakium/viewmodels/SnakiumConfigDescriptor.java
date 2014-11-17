package com.skipifzero.snakium.viewmodels;

import com.skipifzero.snakium.Assets;
import com.skipifzero.snakium.BitmapFontRenderer;
import com.skipifzero.snakium.Const;
import com.skipifzero.snakium.SnakiumUtils;
import com.skipifzero.snakium.framework.math.BoundingRectangle;
import com.skipifzero.snakium.framework.opengl.FontRenderer.HorizontalAlignment;
import com.skipifzero.snakium.framework.opengl.FontRenderer.VerticalAlignment;
import com.skipifzero.snakium.model.SnakiumModel.SnakiumConfig;

public class SnakiumConfigDescriptor {

	//Constants
	private static final int NUMBER_OF_MAIN_ROWS = 4;
	private static final int NUMBER_OF_INDENTED_ROWS = 10;
	private static final double INDENT_SIZE_FACTOR = 0.8;
	private static final double INDENT_FACTOR = 0.025;
	private static final double SPACING_FACTOR = 0.012;
	private static final double INNER_X_MARGIN_FACTOR = 0.035;
	private static final double INNER_Y_MARGIN_FACTOR = 0.02;
	
	private final BoundingRectangle bounds;
	private SnakiumConfig config;
	
	private double xPos, yPos, mainSize, indentedSize, borderWidth;
	
	public SnakiumConfigDescriptor(double x, double y, double size, double borderWidthFactor, SnakiumConfig config) {
		this.bounds = new BoundingRectangle(x, y, size, size);
		this.config = config;
		this.borderWidth = size*borderWidthFactor;
	}
	
	public void render(Assets assets) {
		
		SnakiumUtils.renderBorderComplete(bounds, borderWidth, assets);
		
		BitmapFontRenderer font = assets.font;
		
		font.setHorizontalAlignment(HorizontalAlignment.LEFT);
		font.setVerticalAlignment(VerticalAlignment.TOP);
		
		this.xPos = bounds.getPosition().getX() - bounds.getWidth()/2 + INNER_X_MARGIN_FACTOR*bounds.getHeight();
		this.yPos = bounds.getPosition().getY() + bounds.getHeight()/2 - INNER_Y_MARGIN_FACTOR*bounds.getHeight();
		double heightMinusSpacing = bounds.getHeight() - (bounds.getHeight()*SPACING_FACTOR)*(NUMBER_OF_MAIN_ROWS + NUMBER_OF_INDENTED_ROWS - 1) - 2*INNER_Y_MARGIN_FACTOR*bounds.getHeight();
		this.mainSize = heightMinusSpacing / (NUMBER_OF_MAIN_ROWS + NUMBER_OF_INDENTED_ROWS*INDENT_SIZE_FACTOR);
		this.indentedSize = mainSize*INDENT_SIZE_FACTOR;
		
		font.begin(Const.FONT_COMPLETE_COLOR);
		
		renderRow(font, false, "board" );
		renderRow(font, true, "- size: " + config.boardWidth + " x " + config.boardHeight);
		renderRow(font, true, config.hasWrapping ? "- pass through walls: ON" : "- pass through walls: OFF");
		renderRow(font, false, "speed");
		renderRow(font, true, "- tiles per second: " + config.tilesPerSecond);
		renderRow(font, true, config.hasSpeedIncrease ? "- increasing speed: ON" : "- increasing speed: OFF");
		renderRow(font, true, "- increase per object: " + config.speedIncreasePerObject + " t/s");
		renderRow(font, false, "bonus");
		renderRow(font, true, config.hasBonus ? "- bonus: ON" : "- bonus: OFF");
		renderRow(font, true, "- activates after: " + config.bonusFrequency + " objects");
		renderRow(font, true, "- duration: " + config.bonusDuration + " tiles");
		renderRow(font, false, "score");
		renderRow(font, true, "- normal object: " + config.pointsPerObject + " pts");
		renderRow(font, true, "- bonus object: " + config.pointsPerBonusObject + " pts");
		
		font.render();
	}
	
	public BoundingRectangle getBounds() {
		return bounds;
	}
	
	public void setConfig(SnakiumConfig config) {
		this.config = config;
	}
	
	public SnakiumConfig getConfig() {
		return config;
	}
	
	private void renderRow(BitmapFontRenderer font, boolean indent, String text) {
		if(indent) {
			font.draw(xPos + INDENT_FACTOR*bounds.getWidth(), yPos, indentedSize, text);
			yPos = yPos - indentedSize - SPACING_FACTOR*bounds.getHeight();
		} else {
			font.draw(xPos, yPos, mainSize, text);
			yPos = yPos - mainSize - SPACING_FACTOR*bounds.getHeight();
		}
		
	}
}
