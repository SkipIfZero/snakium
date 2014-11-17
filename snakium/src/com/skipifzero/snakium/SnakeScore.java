package com.skipifzero.snakium;

import java.io.Serializable;

import android.text.format.Time;

import com.skipifzero.snakium.model.SnakiumStats;

public final class SnakeScore implements Serializable, Comparable<SnakeScore> {
	private static final long serialVersionUID = 7397429599987218113L;
	
	transient private StringBuilder strBuilder = null;
	transient private Time time = null;
	transient private String dateString = null;
	transient private String timeString = null;
	transient private String dateTimeString = null;
	
	private final String playerName;
	private final SnakiumStats finalStats;
	private final long timeMillis;
	private final String timeTimezone;
	
	public SnakeScore(SnakiumStats finalStats) {
		this("noname", finalStats, getCurrentTime());
	}
	
	public SnakeScore(String playerName, SnakiumStats finalStats) {
		this(playerName, finalStats, getCurrentTime());
	}
	
	public SnakeScore(String playerName, SnakiumStats finalStats, Time time) {
		this.playerName = playerName;
		this.finalStats = finalStats;
		this.timeMillis = time.toMillis(false);
		this.timeTimezone = time.timezone;
	}
	
	/*
	 * Public methods
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 */
	
	public String getPlayerName() {
		return playerName;
	}
	
	public SnakiumStats getStats() {
		return finalStats;
	}
	
	public int getScore() {
		return this.finalStats.score();
	}
	
	public String getDateString() {
		ensureHasDateString();
		return dateString;
	}
	
	public String getTimeString() {
		ensureHasTimeString();
		return timeString;
	}
	
	public String getDateTimeString() {
		ensureHasDateTimeString();
		return dateTimeString;
	}

	@Override
	public int compareTo(SnakeScore other) {
		int diff = this.getScore() - other.getScore();
		if(diff == 0) {
			this.ensureHasTime();
			other.ensureHasTime();
			diff = Time.compare(other.time, this.time); //Inverted, since a score that was made earlier is more worth than a more recent one.
		}
		return diff;
	}
	
	/*
	 * Private methods
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 */
	
	private String generateDateString() {
		ensureHasStringBuilder();
		ensureHasTime();
		strBuilder.delete(0, strBuilder.length());
		strBuilder.append(time.year).append('-').append(time.month+1).append('-').append(time.monthDay);
		return strBuilder.toString();
	}
	
	private String generateTimeString() {
		ensureHasStringBuilder();
		ensureHasTime();
		strBuilder.delete(0, strBuilder.length());
		strBuilder.append(time.hour).append(':').append(time.minute+1);
		return strBuilder.toString();
	}
	
	private String generateDateTimeString() {
		ensureHasDateString();
		ensureHasTimeString();
		strBuilder.delete(0, strBuilder.length());
		strBuilder.append(dateString).append(' ').append(timeString);
		return strBuilder.toString();
	}
	
	private void ensureHasDateString() {
		if(this.dateString == null) {
			this.dateString = generateDateString();
		}
	}
	
	private void ensureHasTimeString() {
		if(this.timeString == null) {
			this.timeString = generateTimeString();
		}
	}
	
	private void ensureHasDateTimeString() {
		if(this.dateTimeString == null) {
			this.dateTimeString = generateDateTimeString();
		}
	}
	
	private void ensureHasTime() {
		if(this.time == null) {
			this.time = new Time();
			this.time.set(timeMillis);
			//this.time.switchTimezone(timeTimezone);
		}
	}
	
	private void ensureHasStringBuilder() {
		if(this.strBuilder == null) {
			this.strBuilder = new StringBuilder(16);
		}
	}
	
	private static Time getCurrentTime() {
		Time t = new Time();
		t.setToNow();
		t.normalize(false);
		return t;
	}
}
