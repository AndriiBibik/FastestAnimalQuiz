package fastest.animal.quiz;

public class Answer {

    // answer id - equals to index in array of answers
    private int id;

    // image id for answer
    private int imageId;

    // answer text
    private String answerText;

    public Answer(int id, int imageId, String answerText) {
        this.id = id;
        this.imageId = imageId;
        this.answerText = answerText;
    }

    // getters
    public int getId() { return id; }
    public int getImageId() {
        return imageId;
    }
    public String getAnswerText() {
        return answerText;
    }
}
