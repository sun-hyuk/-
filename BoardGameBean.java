package Project;

public class BoardGameBean {
	private String gameId;
	private String gameTitle;
	private String gameCategory;
	private String gameDescription;
	private int maxMembers;
	private String difficultyLevel;
	private String gameWay;
	private byte[] gameImage;
	private int rankingPoints;

	public String getGameCategory() {
		return gameCategory;
	}

	public void setGameCategory(String gameCategory) {
		this.gameCategory = gameCategory; // setCategory 메서드 구현
	}

	public String getGameId() {
		return gameId;
	}

	public void setGameId(String gameId) {
		this.gameId = gameId;
	}

	public String getGameTitle() {
		return gameTitle;
	}

	public void setGameTitle(String gameTitle) {
		this.gameTitle = gameTitle;
	}

	public String getGameDescription() {
		return gameDescription;
	}

	public void setGameDescription(String gameDescription) {
		this.gameDescription = gameDescription;
	}

	public int getMaxMembers() {
		return maxMembers;
	}

	public void setMaxMembers(int maxMembers) {
		this.maxMembers = maxMembers;
	}

	public String getDifficultyLevel() {
		return difficultyLevel;
	}

	public void setDifficultyLevel(String difficultyLevel) {
		this.difficultyLevel = difficultyLevel;
	}

	public String getGameWay() {
		return gameWay;
	}

	public void setGameWay(String gameWay) {
		this.gameWay = gameWay;
	}

	public byte[] getGameImage() {
		return gameImage;
	}

	public void setGameImage(byte[] gameImage) {
		this.gameImage = gameImage;
	}

	public int getRankingPoints() {
		return rankingPoints;
	}

	public void setRankingPoints(int rankingPoints) {
		this.rankingPoints = rankingPoints;
	}

	// toString() 재정의: JList나 콤보박스에 표시될 때 보드게임 제목으로 보이도록 함
	@Override
	public String toString() {
		return gameId;
	}
}
