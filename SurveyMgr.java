package Project;

import java.sql.*;
import java.util.Vector;

public class SurveyMgr {
    private DBConnectionMgr pool;

    public SurveyMgr() {
        try {
            pool = DBConnectionMgr.getInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Vector<SurveyBean> listSurveys() {
        Vector<SurveyBean> surveyList = new Vector<>();
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        // created_at 기준으로 내림차순으로 정렬
        String sql = "SELECT s.poll_id, s.question, s.sdate, s.edate, s.created_at, " +
                     " i.itemlist_num, i.item, i.vote_count " +
                     "FROM tblpolllist s " +
                     "LEFT JOIN tblpollitem i ON s.poll_id = i.poll_id " +
                     "ORDER BY s.created_at DESC, i.itemlist_num ASC"; // created_at 기준 내림차순 정렬

        try {
            con = pool.getConnection();
            pstmt = con.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                SurveyBean currentSurvey = new SurveyBean();
                currentSurvey.setPollId(rs.getInt("poll_id"));
                currentSurvey.setQuestion(rs.getString("question"));
                currentSurvey.setStartDate(rs.getString("sdate"));
                currentSurvey.setEndDate(rs.getString("edate"));
                currentSurvey.setCreatedAt(rs.getString("created_at")); // created_at 값을 저장

                // 설문 항목 추가
                SurveyBean.Item newItem = new SurveyBean.Item(
                    rs.getString("item"), // item_content 제거
                    rs.getInt("vote_count"), 
                    rs.getInt("itemlist_num")
                );
                currentSurvey.addItem(newItem);

                surveyList.add(currentSurvey);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.freeConnection(con, pstmt, rs);
        }
        return surveyList;
    }

    // 설문 생성 메소드
    public boolean createSurvey(String question, Date endDate, Vector<String> items) {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        // 현재 날짜를 sdate로 설정
        Date startDate = new Date(System.currentTimeMillis()); // 현재 날짜로 설정
        String sql = "INSERT INTO tblpolllist (question, sdate, edate) VALUES (?, ?, ?)";
        boolean flag = false;

        try {
            con = pool.getConnection();
            pstmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, question); // 설문 제목 추가
            pstmt.setDate(2, startDate); // 현재 날짜를 sdate에 설정
            pstmt.setDate(3, new java.sql.Date(endDate.getTime())); // 종료일 설정

            if (pstmt.executeUpdate() == 1) {
                rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    int pollId = rs.getInt(1); // 새로 생성된 설문 ID를 가져옵니다.
                    // 설문 항목 저장
                    saveSurveyItems(pollId, items, con);
                    flag = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.freeConnection(con, pstmt, rs);
        }
        return flag;
    }

    // 투표 수 업데이트 메서드
    public boolean updateVoteCount(int itemListNum, int newVoteCount) {
        Connection con = null;
        PreparedStatement pstmt = null;
        String sql = "UPDATE tblpollitem SET vote_count = ? WHERE itemlist_num = ?";
        boolean isUpdated = false;

        try {
            con = pool.getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, newVoteCount);
            pstmt.setInt(2, itemListNum);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 1) {
                isUpdated = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.freeConnection(con, pstmt);
        }
        return isUpdated;
    }

    // 설문 항목 저장
    private void saveSurveyItems(int pollId, Vector<String> items, Connection con) throws SQLException {
        String query = "INSERT INTO tblpollitem (item, poll_id) VALUES (?, ?)";
        try (PreparedStatement pstmt = con.prepareStatement(query)) {
            for (String item : items) {
                pstmt.setString(1, item);
                pstmt.setInt(2, pollId);
                pstmt.executeUpdate();
            }
        }
    }

    public boolean deleteSurvey(int pollId) {
        Connection con = null;
        PreparedStatement pstmt = null;
        String sql = "DELETE FROM tblpollitem WHERE poll_id = ?"; // 항목 삭제

        try {
            con = pool.getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, pollId);
            pstmt.executeUpdate(); // 설문 항목 삭제

            // 설문 본체 삭제
            sql = "DELETE FROM tblpolllist WHERE poll_id = ?";
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, pollId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows == 1; // 본체가 삭제되었는지 확인
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            pool.freeConnection(con, pstmt);
        }
    }

    // 1. 사용자가 이미 해당 설문에 대해 투표했는지 확인 (member_id 사용)
    public boolean hasUserVoted(String member_id, int pollId) {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String sql = "SELECT COUNT(*) FROM tblpolluser WHERE member_id = ? AND poll_id = ?";
        try {
            con = pool.getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, member_id);
            pstmt.setInt(2, pollId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0; // 투표 기록이 있으면 이미 투표한 것임
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.freeConnection(con, pstmt, rs);
        }
        return false;
    }

    // 2. 사용자가 해당 설문에 투표했음을 기록 (member_id 사용)
    public void recordVote(String member_id, int pollId, int itemlist_num) {
        Connection con = null;
        PreparedStatement pstmt = null;
        String sql = "INSERT INTO tblpolluser (member_id, poll_id, itemlist_num) VALUES (?, ?, ?)";
        try {
            con = pool.getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, member_id);
            pstmt.setInt(2, pollId);
            pstmt.setInt(3, itemlist_num);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.freeConnection(con, pstmt);
        }
    }

    // 설문 상세 정보 조회 (설문 항목 및 투표 수)
    public SurveyBean getSurveyDetail(int pollId) {
        SurveyBean survey = null;
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        String sql = "SELECT s.poll_id, s.question, s.sdate, s.edate, " +
                     "i.itemlist_num, i.item, i.vote_count " +
                     "FROM tblpolllist s " +
                     "LEFT JOIN tblpollitem i ON s.poll_id = i.poll_id " +
                     "WHERE s.poll_id = ?";

        try {
            con = pool.getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, pollId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                if (survey == null) {
                    survey = new SurveyBean();
                    // 설문 정보 설정
                    survey.setPollId(rs.getInt("poll_id"));
                    survey.setQuestion(rs.getString("question"));
                    survey.setStartDate(rs.getString("sdate"));
                    survey.setEndDate(rs.getString("edate"));
                }

                // 항목 정보 설정 및 Item 객체 생성
                int itemListNum = rs.getInt("itemlist_num");
                String item = rs.getString("item");  // item_content 제거
                int voteCount = rs.getInt("vote_count");

                // Item 객체 생성 및 추가
                SurveyBean.Item newItem = new SurveyBean.Item(item, voteCount, itemListNum);
                survey.addItem(newItem); // 항목 리스트에 추가
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.freeConnection(con, pstmt, rs);
        }
        return survey;
    }

}
