package me.hardcoded.algorithm.midi;

public record MelodyScore(double score, boolean valid) {
	public static final MelodyScore None = new MelodyScore(0, false);
	
	/**
	 * Returns the score with the lesser error
	 */
	public MelodyScore min(MelodyScore other) {
		if (!valid) {
			return other.valid ? other : this;
		}
		
		return other.valid && other.score < this.score ? other : this;
	}
	
	public double getCalculatedScore() {
		return valid ? score : Double.NaN;
	}
}
