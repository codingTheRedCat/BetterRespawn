
package me.theredcat.betterespawn;

public final class XP {

	public static int levelsToPoints(double levels) {
		
		if(levels <= 16)
			return (int) Math.round(Math.pow(levels, 2) + 6 * levels);
		
		if(levels <= 31) {
			return (int) Math.round(2.5*Math.pow(levels, 2)-40.5 * levels + 360);
		}
		
		return (int) Math.round(4.5 * levels - 162.5 * levels + 2220);
		
	}
	
	
}
