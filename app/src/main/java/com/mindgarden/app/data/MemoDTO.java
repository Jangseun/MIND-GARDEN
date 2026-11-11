package com.mindgarden.app.data;

public class MemoDTO {
    private long id;
    private String source;
    private String keyword;
    private String content;
    private long createdAt;
    private long updatedAt;
    private String reflectionStatus;
    private String reflectionNote;
    private Long reflectedAt;

    public MemoDTO(long id, String source, String keyword, String content,
                   long createdAt, long updatedAt, String reflectionStatus,
                   String reflectionNote, Long reflectedAt) {
        this.id = id;
        this.source = source;
        this.keyword = keyword;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.reflectionStatus = reflectionStatus;
        this.reflectionNote = reflectionNote;
        this.reflectedAt = reflectedAt;
    }

    public MemoDTO(String source, String keyword, String content, long now) {
        this(0, source, keyword, content, now, now, null, null, null);
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    public long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }
    public String getReflectionStatus() { return reflectionStatus; }
    public void setReflectionStatus(String reflectionStatus) { this.reflectionStatus = reflectionStatus; }
    public String getReflectionNote() { return reflectionNote; }
    public void setReflectionNote(String reflectionNote) { this.reflectionNote = reflectionNote; }
    public Long getReflectedAt() { return reflectedAt; }
    public void setReflectedAt(Long reflectedAt) { this.reflectedAt = reflectedAt; }

    @Override
    public String toString() {
        return "[" + keyword + "] " + content;
    }
}
