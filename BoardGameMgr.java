package Project;

import java.sql.*;
import java.util.Vector;

public class BoardGameMgr {
	private DBConnectionMgr pool;

	public BoardGameMgr() {
		try {
			pool = DBConnectionMgr.getInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// DB에서 게임 목록을 불러옴. 여기서는 game_id를 보드게임 제목으로 사용합니다.
	public Vector<BoardGameBean> listGame() {
		Vector<BoardGameBean> gameList = new Vector<>();
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		// game 테이블에서 game_id를 가져옵니다.
		String sql = "SELECT game_id FROM game ORDER BY game_id";
		try {
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				BoardGameBean bean = new BoardGameBean();
				bean.setGameId(rs.getString("game_id"));
				gameList.add(bean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return gameList;
	}

	// 게임 목록을 가져오는 메서드 (BoardGamePageAWT에서 사용)
	public Vector<BoardGameBean> listBoardGames(int currentPage, int gamesPerPage, String categoryFilter,
			String keywordFilter) {
		Vector<BoardGameBean> gameList = new Vector<>();
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		// 게임 목록을 가져오는 SQL (필터와 페이징 고려)
		String sql = "SELECT * FROM game WHERE game_id LIKE ? AND category LIKE ? ORDER BY ranking_points DESC LIMIT ?, ?";

		try {
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, "%" + keywordFilter + "%"); // 게임 ID 검색 (keywordFilter)
			pstmt.setString(2, "%" + categoryFilter + "%"); // 카테고리 검색 (categoryFilter)
			pstmt.setInt(3, (currentPage - 1) * gamesPerPage); // OFFSET (페이징)
			pstmt.setInt(4, gamesPerPage); // LIMIT (페이징)

			rs = pstmt.executeQuery();

			while (rs.next()) {
				BoardGameBean bean = new BoardGameBean();
				bean.setGameId(rs.getString("game_id"));
				bean.setGameTitle(rs.getString("game_id")); // game_id를 게임 제목으로 사용
				bean.setGameDescription(rs.getString("game_description"));
				bean.setMaxMembers(rs.getInt("max_member"));
				bean.setDifficultyLevel(rs.getString("difficulty_level"));
				bean.setGameCategory(rs.getString("category"));
				bean.setGameWay(rs.getString("game_way"));
				bean.setRankingPoints(rs.getInt("ranking_points"));
				bean.setGameImage(rs.getBytes("game_image"));
				gameList.add(bean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return gameList;
	}

	// 게임 상세 정보 조회 메서드 (BoardGameDetailPageAWT에서 사용)
	public BoardGameBean getGameDetails(String gameId) {
		BoardGameBean game = null;
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		// 게임 ID로 상세 정보를 가져오는 SQL
		String sql = "SELECT * FROM game WHERE game_id = ?";

		try {
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, gameId);
			rs = pstmt.executeQuery();

			if (rs.next()) {
				game = new BoardGameBean();
				game.setGameId(rs.getString("game_id"));
				game.setGameTitle(rs.getString("game_id")); // game_id를 게임 제목으로 사용
				game.setGameDescription(rs.getString("game_description"));
				game.setMaxMembers(rs.getInt("max_member"));
				game.setDifficultyLevel(rs.getString("difficulty_level"));
				game.setGameCategory(rs.getString("category"));
				game.setGameWay(rs.getString("game_way"));
				game.setRankingPoints(rs.getInt("ranking_points"));
				game.setGameImage(rs.getBytes("game_image"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return game;
	}

	// 인기순위 페이지에서 인기 게임 목록을 가져오는 메서드 (BoardGameRankingPageAWT에서 사용)
	public Vector<BoardGameBean> getTopRankedGames(int topLimit) {
		Vector<BoardGameBean> topRankedGames = new Vector<>();
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		// 랭킹 순으로 게임 목록을 가져오는 SQL
		String sql = "SELECT * FROM game ORDER BY ranking_points DESC LIMIT ?";

		try {
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, topLimit); // Top N 게임 가져오기
			rs = pstmt.executeQuery();

			while (rs.next()) {
				BoardGameBean bean = new BoardGameBean();
				bean.setGameId(rs.getString("game_id"));
				bean.setGameTitle(rs.getString("game_id")); // game_id를 게임 제목으로 사용
				bean.setGameDescription(rs.getString("game_description"));
				bean.setMaxMembers(rs.getInt("max_member"));
				bean.setDifficultyLevel(rs.getString("difficulty_level"));
				bean.setGameCategory(rs.getString("category"));
				bean.setGameWay(rs.getString("game_way"));
				bean.setRankingPoints(rs.getInt("ranking_points"));
				bean.setGameImage(rs.getBytes("game_image"));
				topRankedGames.add(bean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return topRankedGames;
	}

	// 게임 랭킹 점수 업데이트 메서드 (게임 클릭 시 랭킹 점수 증가)
	public boolean updateGameRankingPoints(String gameId, int pointsToAdd) {
		Connection con = null;
		PreparedStatement pstmt = null;
		String sql = "UPDATE game SET ranking_points = ranking_points + ? WHERE game_id = ?";
		boolean flag = false;

		try {
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, pointsToAdd); // 랭킹 점수 증가
			pstmt.setString(2, gameId); // 게임 ID

			if (pstmt.executeUpdate() == 1) {
				flag = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt);
		}
		return flag;
	}
}
