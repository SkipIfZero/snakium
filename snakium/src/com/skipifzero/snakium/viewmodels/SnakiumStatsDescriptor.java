package com.skipifzero.snakium.viewmodels;

import java.text.DecimalFormat;

import com.skipifzero.snakium.Assets;
import com.skipifzero.snakium.BitmapFontRenderer;
import com.skipifzero.snakium.Const;
import com.skipifzero.snakium.SnakiumUtils;
import com.skipifzero.snakium.framework.math.BoundingRectangle;
import com.skipifzero.snakium.framework.opengl.FontRenderer.HorizontalAlignment;
import com.skipifzero.snakium.framework.opengl.FontRenderer.VerticalAlignment;
import com.skipifzero.snakium.model.SnakiumStats;

public class SnakiumStatsDescriptor {

	private static final double LINE_PADDING_FACTOR = 0.02;
	private static final double LINE_OFFSET_FACTOR = 0.1;
	
	private static final int NUMBER_OF_LARGE_ROWS = 1;
	private static final int NUMBER_OF_ROWS = 9;
	private static final double LARGE_ROW_SIZE_FACTOR = 1.8;
	private static final double Y_PADDING_FACTOR = 0.03;
	
	private static final double X_PADDING_FACTOR = 0.02;
	private static final double Y_EDGE_PADDING_FACTOR = 0.03;
	
	private static final DecimalFormat df = new DecimalFormat("#.###");
	
	private final BoundingRectangle bounds;
	private final SnakiumStats stats;
	
	private final double borderWidth, textSize, largeTextSize, padding, edgePadding, linePadding, lineXPos;
	private double yPos;
	
	public SnakiumStatsDescriptor(double x, double y, double size, double borderWidthFactor, SnakiumStats stats) {
		this.bounds = new BoundingRectangle(x, y, size, size);
		this.stats = stats;
		this.linePadding = size*LINE_PADDING_FACTOR;
		this.lineXPos = x + size*LINE_OFFSET_FACTOR;
		
		this.borderWidth = size*borderWidthFactor;
		
		double totalYSize = bounds.getHeight() - 2*borderWidth;
		int paddings = NUMBER_OF_LARGE_ROWS + NUMBER_OF_ROWS - 1;
		int edgePaddings = 2;
		this.padding = Y_PADDING_FACTOR * totalYSize;
		this.edgePadding = Y_EDGE_PADDING_FACTOR * totalYSize;
		
		double totalTextSize = totalYSize - paddings*padding - edgePaddings*edgePadding;
		
		this.textSize = totalTextSize / (NUMBER_OF_ROWS + NUMBER_OF_LARGE_ROWS*LARGE_ROW_SIZE_FACTOR);
		this.largeTextSize = totalTextSize - textSize*NUMBER_OF_ROWS;
	}
	
	public void render(Assets assets) {
		
		//SnakiumUtils.renderBorderComplete(bounds, borderWidth, assets);
		
		BitmapFontRenderer font = assets.font;
		resetYPos();
		font.setVerticalAlignment(VerticalAlignment.TOP);
		font.begin(Const.FONT_COMPLETE_COLOR);
		renderRow(font, "score:", Integer.toString(stats.score()), true);
		renderRow(font, "snake length:", Integer.toString(stats.snakeLength()), false);
		renderRow(font, "objects:", Integer.toString(stats.objectsConsumed()), false);
		renderRow(font, "bonus objects:", Integer.toString(stats.bonusObjectsConsumed()), false);
		renderRow(font, "lost objects:", Integer.toString(stats.missedBonusObjects()), false);
		renderRow(font, "tiles moved:", Integer.toString(stats.amountOfMoves()), false);
		renderRow(font, "left turns:", Integer.toString(stats.leftTurns()), false);
		renderRow(font, "right turns:", Integer.toString(stats.rightTurns()), false);
		renderRow(font, "walls crossed:", Integer.toString(stats.wallsCrossed()), false);
		renderRow(font, "max speed:", df.format(stats.maxSpeedAchieved()), false);
		font.render();
	}
	
	public BoundingRectangle getBounds() {
		return bounds;
	}
	
	private void renderRow(BitmapFontRenderer font, String textLeft, String textRight, boolean large) {
		double rowTextSize;
		if(large) {
			rowTextSize = largeTextSize;
		} else {
			rowTextSize = textSize;
		}
		
		font.setHorizontalAlignment(HorizontalAlignment.RIGHT);
		font.draw(lineXPos - linePadding, yPos, rowTextSize, textLeft);
		
		font.setHorizontalAlignment(HorizontalAlignment.LEFT);
		font.draw(lineXPos + linePadding, yPos, rowTextSize, textRight);
		
		if(large) {
			yPos = yPos - largeTextSize - padding;
		} else {
			yPos = yPos - textSize - padding;
		}
	}
	
	private void resetYPos() {
		this.yPos = bounds.getY() + bounds.getHeight()/2 - borderWidth - edgePadding;
	}
}
