package Project;

public class CommentBean {
	private int commentId;
	private String commentContent;
	private String createdDate;

	public CommentBean(int commentId, String commentContent, String createdDate) {
		this.commentId = commentId;
		this.commentContent = commentContent;
		this.createdDate = createdDate;
	}

	public int getCommentId() {
		return commentId;
	}

	public void setCommentId(int commentId) {
		this.commentId = commentId;
	}

	public String getCommentContent() {
		return commentContent;
	}

	public void setCommentContent(String commentContent) {
		this.commentContent = commentContent;
	}

	public String getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}
}
