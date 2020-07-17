package fastest.animal.quiz;

import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class QuestionFragment extends Fragment {

    private Question question;

    public QuestionFragment() {
        // Required empty public constructor
    }

    public QuestionFragment(Question question) {
        this.question = question;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_question, container, false);

        ((TextView) layout.findViewById(R.id.question_text)).setText(question.getQuestionText());
        LinearLayout answersContainer = layout.findViewById(R.id.answers_container);
        for (Answer a: question.getAnswers()) {
            CardView answerCard = (CardView) inflater.inflate(R.layout.answer_layout, answersContainer, false);
            ((ImageView) answerCard.findViewById(R.id.answer_image)).setImageResource(a.getImageId());
            ((TextView) answerCard.findViewById(R.id.answer_text)).setText(a.getAnswerText());
            answersContainer.addView(answerCard);
        }
        return layout;
    }
}
