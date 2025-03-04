package Project;

import java.util.*;

public class SurveyBean {
    private int pollId;
    private String question;
    private String startDate;
    private String endDate;
    private String createdAt;
    private List<Item> items;

    public SurveyBean() {
        items = new ArrayList<>();
    }

    // Getters and Setters
    public int getPollId() {
        return pollId;
    }

    public void setPollId(int pollId) {
        this.pollId = pollId;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public List<Item> getItems() {
        return items;
    }

    public void addItem(Item item) {
        items.add(item);
    }

    public static class Item {
        private String content;
        private int voteCount;
        private int itemListNum;

        public Item(String content, int voteCount, int itemListNum) {
            this.content = content;
            this.voteCount = voteCount;
            this.itemListNum = itemListNum;
        }

        public String getContent() {
            return content;
        }

        public int getVoteCount() {
            return voteCount;
        }

        public int getItemListNum() {
            return itemListNum;
        }
    }

    @Override
    public String toString() {
        return "SurveyBean [pollId=" + pollId + ", question=" + question + ", startDate=" + startDate + ", endDate="
                + endDate + ", createdAt=" + createdAt + ", items=" + items + "]";
    }
}
