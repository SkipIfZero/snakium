package com.skipifzero.snakium;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public final class HighScores implements Serializable {
	private static final long serialVersionUID = -3843783376663081673L;
	
	/*
	 * Inner classes
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 */
	
	public static enum ScoreTableType {
		CLASSIC, SNAKE2, SNAKIUM;
	}
	
	private final class ScoreTable implements Serializable {
		private static final long serialVersionUID = -8235137937954954647L;
		private static final int TABLE_SIZE = 5;
		private final List<SnakeScore> table = new ArrayList<SnakeScore>(TABLE_SIZE+1); //+1 to avoid creating larger table when adding new score.
		
		private ScoreTable() {
		}
		
		private void addSnakeScore(SnakeScore score) {
			table.add(score);
			Collections.sort(table); //Sorts from lowest to highest
			Collections.reverse(table); //Changes from highest to lowest
			if(table.size() > TABLE_SIZE) {
				table.remove(table.size()-1);
			}
		}
		
		private void clearTable() {
			table.clear();
		}
		
		private boolean isHighestScore(SnakeScore score) {
			if(table.size() < 1) {
				return false;
			}
			return !(score.compareTo(table.get(0)) < 0); //Hack. No idea why this is necessary or why it works now.
		}
		
		private boolean containsScore(SnakeScore score) {
			return table.contains(score);
		}
	}

	/*
	 * This class
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 */
	
	private final Map<ScoreTableType, ScoreTable> typeToTable = new EnumMap<ScoreTableType, ScoreTable>(ScoreTableType.class);
	
	public HighScores() {
		for(ScoreTableType type : ScoreTableType.values()) {
			typeToTable.put(type, new ScoreTable());
		}
	}
	
	public List<SnakeScore> getSnakeScoreTable(ScoreTableType type) {
		return typeToTable.get(type).table; //Dangerous. Direct access to internal score list, but I trust myself to not do anything stupid with it.
	}
	
	public void addScoreMaybe(ScoreTableType type, SnakeScore score) {
		typeToTable.get(type).addSnakeScore(score);
	}
	
	public boolean isHighestScore(ScoreTableType type, SnakeScore score) {
		return typeToTable.get(type).isHighestScore(score);
	}
	
	public boolean tableContainsScore(ScoreTableType type, SnakeScore score) {
		return typeToTable.get(type).containsScore(score);
	}
	
	public void clearTable(ScoreTableType type) {
		typeToTable.get(type).clearTable();
	}
}
