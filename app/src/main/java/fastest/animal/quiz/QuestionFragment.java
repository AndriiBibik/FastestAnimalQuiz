package fastest.animal.quiz;

import android.content.res.Configuration;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.flexbox.JustifyContent;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

public class QuestionFragment extends Fragment {

    public static final String KEY_CURRENT_QUESTION = "current";

    public static final String KEY_SET_MULTI_ANSWER = "multi";

    public static final String KEY_SHUFFLED = "shuffled";

    private int questionIndex;

    private Question question;

    private int[] shuffled;

    private TreeSet<Integer> setForMultiAnswer = new TreeSet<>();

    public static final String LOG_TAG = QuestionFragment.class.getSimpleName();

    public QuestionFragment() {
        // Required empty public constructor
    }

    public QuestionFragment(int questionIndex, int[] shuffled) {
        this.questionIndex = questionIndex;
        this.shuffled = shuffled;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_question, container, false);

        if (savedInstanceState != null) {
            questionIndex = savedInstanceState.getInt(KEY_CURRENT_QUESTION);
            shuffled = ((QuizActivity)getActivity()).getArrayOfIndexesFromString(savedInstanceState.getString(KEY_SHUFFLED));
            if (savedInstanceState.containsKey(KEY_SET_MULTI_ANSWER)) {
                String multiAnswersString = savedInstanceState.getString(KEY_SET_MULTI_ANSWER);
                for (int idx: ((QuizActivity)getActivity()).getArrayOfIndexesFromString(multiAnswersString)) {
                    setForMultiAnswer.add(idx);
                }
            }
        }

        Question[] questions = ((QuizActivity)getActivity()).getQuestions();

        question = questions[shuffled[questionIndex]];

        Log.v(LOG_TAG, "" + question);
        // is question multianswered
        boolean isQuestionMultiAnswered = question.getRightAnswersIds().length > 1;

        // if question have multi answers
        if (isQuestionMultiAnswered) {
            layout.findViewById(R.id.next_question_button).setVisibility(View.VISIBLE);
            layout.findViewById(R.id.next_question_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int rightWrong = areSetContainsSameValuesAsArray(setForMultiAnswer, question.getRightAnswersIds())
                            ? QuizActivity.RIGHT
                            : QuizActivity.WRONG;
                    QuizActivity quizActivity = (QuizActivity) getActivity();
                    quizActivity.updateQuizWithNext(rightWrong);
                }
            });
        }

        ((TextView) layout.findViewById(R.id.question_text)).setText(question.getQuestionText());
        FlexboxLayout answersContainer = layout.findViewById(R.id.answers_container);
        int orientation = this.getResources().getConfiguration().orientation;
        // changes in flexbox layout for landscape mode
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            answersContainer.setFlexDirection(FlexDirection.ROW);
            answersContainer.setFlexWrap(FlexWrap.WRAP);
            answersContainer.setJustifyContent(JustifyContent.CENTER);
        }
        for (Answer a: question.getAnswers()) {
            CardView answerCard = (CardView) inflater.inflate(R.layout.answer_layout, answersContainer, false);
            // setting cards horizontal margins in landscape mode
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                int cardMargin = (int) getContext().getResources()
                        .getDimension(R.dimen.cards_horizontal_margin_landscape);
                // layout params
                ViewGroup.MarginLayoutParams layoutParams =
                        (ViewGroup.MarginLayoutParams) answerCard.getLayoutParams();
                layoutParams.setMargins(cardMargin, cardMargin, cardMargin, cardMargin);
                answerCard.setLayoutParams(layoutParams);
            }
            ((ImageView) answerCard.findViewById(R.id.answer_image)).setImageResource(a.getImageId());
            ((TextView) answerCard.findViewById(R.id.answer_text)).setText(a.getAnswerText());
            if (isQuestionMultiAnswered) {
                answerCard.findViewById(R.id.answer_checkbox).setVisibility(View.VISIBLE);
                if (setForMultiAnswer.contains(new Integer(a.getId()))) {
                    ((ImageView)answerCard.findViewById(R.id.answer_checkbox)).setImageResource(R.drawable.ic_checkbox_checked);
                }
            }
            answerCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!isQuestionMultiAnswered) {
                        int rightWrong = a.getId() == question.getRightAnswersIds()[0]
                                ? QuizActivity.RIGHT
                                : QuizActivity.WRONG;
                        // accessing activity method to put there rightWrong for this answer
                        QuizActivity quizActivity = (QuizActivity)getActivity();
                        quizActivity.updateQuizWithNext(rightWrong);
                    } else {
                        if (!setForMultiAnswer.contains(new Integer(a.getId()))) {
                            setForMultiAnswer.add(new Integer(a.getId()));
                            ((ImageView)answerCard.findViewById(R.id.answer_checkbox))
                                    .setImageResource(R.drawable.ic_checkbox_checked);

                        } else {
                            setForMultiAnswer.remove(new Integer(a.getId()));
                            ((ImageView)answerCard.findViewById(R.id.answer_checkbox))
                                    .setImageResource(R.drawable.ic_checkbox_unchecked);
                        }
                    }
                }
            });
            answersContainer.addView(answerCard);
        }
        return layout;
    }

    // are set equals to int[]
    public boolean areSetContainsSameValuesAsArray(Set<Integer> set, int[] array) {
        if (set.size() != array.length)
            return false;
        for (int i = 0; i < array.length; i++) {
            if (!set.contains(new Integer(array[i])))
                return false;
        }
        return true;
    }

    public int[] setIntoArray(Set<Integer> set) {
        int[] array = new int[set.size()];
        int idx = 0;
        for (int value: set) {
            array[idx] = value;
            idx++;
        }
        return array;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt(KEY_CURRENT_QUESTION, questionIndex);
        savedInstanceState.putString(KEY_SHUFFLED, Arrays.toString(shuffled));
        if (setForMultiAnswer.size() > 0) {
            savedInstanceState.putString(KEY_SET_MULTI_ANSWER, Arrays.toString(setIntoArray(setForMultiAnswer)));
        }
    }

}
