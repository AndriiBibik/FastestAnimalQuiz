package fastest.animal.quiz;

public class Question {

    // actual question text
    private String questionText;

    // answers
    private Answer[] answers;

    // right answers ids
    private int[] rightAnswersIds;

    // explanations
    private String explanationText;

    public Question(String questionText, String explanationText, Answer[] answers, int[] rightAnswersIds) {
        this.questionText = questionText;
        this.explanationText = explanationText;
        this.answers = answers;
        this.rightAnswersIds = rightAnswersIds;
    }

    // getters
    public String getQuestionText() {
        return questionText;
    }
    public String getExplanationText() { return explanationText; }
    public Answer[] getAnswers() {
        return answers;
    }
    public int[] getRightAnswersIds() { return rightAnswersIds; }
}
