package fastest.animal.quiz;

public class Question {

    // actual question text
    private String questionText;

    // answers
    private Answer[] answers;

    // right answers ids
    private int[] rightAnswersIds;

    public Question(String questionText, Answer[] answers, int[] rightAnswersIds) {
        this.questionText = questionText;
        this.answers = answers;
        this.rightAnswersIds = rightAnswersIds;
    }

    // getters
    public String getQuestionText() {
        return questionText;
    }
    public Answer[] getAnswers() {
        return answers;
    }
    public int[] getRightAnswersIds() { return rightAnswersIds; }
}
