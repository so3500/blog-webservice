package hackday.blogengine.evernote.dto;

public class Evernote {

    private String title;
    private String content;

    public Evernote(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public String getTitle() {
        return this.title;
    }

    public String getContent() {
        return this.content;
    }
}
