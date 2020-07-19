package fastest.animal.quiz;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.flexbox.FlexboxLayout;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QuizActivity extends AppCompatActivity {

    private static final String LOG_TAG = QuizActivity.class.getSimpleName();

    // right wrong answers tracking
    private int[] rightWrongAnswers;
    public static final int RIGHT = 2;
    public static final int WRONG = 1;

    private int currentQuestion = -1;

    //questions
    private Question[] questions;

    // shuffled indexes
    private int[] shuffled;

    // for saved instance state
    public static final String KEY_BUNDLE_RESULTS = "results";
    public static final String KEY_BUNDLE_SHUFFLED = "shuffled";
    public static final String KEY_BUNDLE_CURRENT_QUESTION = "question";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        // get questions with answers
        questions = getQuestions();

        // if screen rotated and state was saved
        if (savedInstanceState != null) {
            rightWrongAnswers = getArrayOfIndexesFromString(savedInstanceState.getString(KEY_BUNDLE_RESULTS));
            shuffled = getArrayOfIndexesFromString(savedInstanceState.getString(KEY_BUNDLE_SHUFFLED));
            currentQuestion = savedInstanceState.getInt(KEY_BUNDLE_CURRENT_QUESTION);
        } else {
            rightWrongAnswers = new int[questions.length];
            // get shuffled array
            // shuffle questions and save info into shared prefs
            int[] idxs = new int[questions.length];
            for (int i = 0; i < questions.length; i++) idxs[i] = i;
            shuffled = shuffleIntArray(idxs);

            replaceFragmentWithNewOne();

        }

        inputIndicatorsIntoFlexbox();
    }

    // refreshing flexbox with indicators for each question
    public void inputIndicatorsIntoFlexbox() {
        FlexboxLayout flexboxLayout = findViewById(R.id.indicative_flexbox);

        for (int i = 0; i < rightWrongAnswers.length; i++) {

            ImageView imageView = new ImageView(this);
            FlexboxLayout.LayoutParams params =
                    new FlexboxLayout.LayoutParams(
                            (int) getResources().getDimension(R.dimen.indicator_size),
                            (int) getResources().getDimension(R.dimen.indicator_size));
            int imageViewMargin = (int) getResources().getDimension(R.dimen.indicator_margin);
            params.setMargins(imageViewMargin, imageViewMargin, imageViewMargin, imageViewMargin);
            imageView.setLayoutParams(params);

            if (i < currentQuestion) {
                imageView.setImageResource(R.drawable.ic_full_circle);
            } else if (i == currentQuestion) {
                imageView.setImageResource(R.drawable.ic_circle_with_dot);
            } else {
                imageView.setImageResource(R.drawable.ic_empty_circle);
            }

            flexboxLayout.addView(imageView);
        }
    }

    // refresh flexbox
    public void refreshFlexBox() {
        FlexboxLayout flexboxLayout = findViewById(R.id.indicative_flexbox);
        if (currentQuestion < questions.length) {
            ((ImageView) flexboxLayout.getChildAt(currentQuestion)).setImageResource(R.drawable.ic_full_circle);
        }

        if (currentQuestion < (questions.length - 1)) {
            ((ImageView) flexboxLayout.getChildAt(currentQuestion + 1)).setImageResource(R.drawable.ic_circle_with_dot);
        }
    }

    // update quiz with next question
    public void updateQuizWithNext(int rightWrong) {

        if (currentQuestion < questions.length) {

            putRightWrongValue(rightWrong);

        }

        refreshFlexBox();

        replaceFragmentWithNewOne();
    }

    // put right or wrong value for answered question
    public void putRightWrongValue(int rightWrong) {
        rightWrongAnswers[currentQuestion] = rightWrong;
    }

    // replace fragment with new and new question
    public void replaceFragmentWithNewOne() {
        currentQuestion++;
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        if (currentQuestion < questions.length) {
            Fragment fragment = new QuestionFragment(currentQuestion, shuffled);
            fragmentTransaction.replace(R.id.question_fragment, fragment);
            fragmentTransaction.commit();
        } else {
            Fragment fragment = new ResultsFragment(rightWrongAnswers, shuffled);
            fragmentTransaction.replace(R.id.question_fragment, fragment);
            fragmentTransaction.commit();
        }
    }

    // get question from resources
    public Question[] getQuestions() {

        String[] questionsArray = getResources().getStringArray(R.array.questions);
        // explanations
        String[] explanationsArray = getResources().getStringArray(R.array.explanations);

        Question[] questions = new Question[questionsArray.length];

        for (int i = 0; i < questionsArray.length; i++) {

            // current answers id
            int arryid = getResources()
                    .getIdentifier("answers_" + (i + 1), "array", getPackageName());
            // current answers images id
            int arryIdImg = getResources()
                    .getIdentifier("answers_images_" + (i + 1), "array", getPackageName());
            int idRightAnswers = getResources()
                    .getIdentifier("right_answers_" + (i + 1), "array", getPackageName());
            String[] answersStrings = getResources().getStringArray(arryid);
            TypedArray imageResourcesTypedArray = getResources().obtainTypedArray(arryIdImg);
            int[] rightAnswersIds = getResources().getIntArray(idRightAnswers);

            Answer[] answers = new Answer[answersStrings.length];

            for (int j = 0; j < answersStrings.length; j++) {
                answers[j] = new Answer(j, imageResourcesTypedArray.getResourceId(j, -1), answersStrings[j]);
            }
            questions[i] = new Question(questionsArray[i], explanationsArray[i], answers, rightAnswersIds);
        }
        return questions;
    }

    public int[] shuffleIntArray(int[] toShuffle) {
        Random random = new Random();
        for (int i = 0; i < toShuffle.length; i++) {
            int idxToReplace = random.nextInt(toShuffle.length);
            int replacement = toShuffle[idxToReplace];
            toShuffle[idxToReplace] = toShuffle[i];
            toShuffle[i] = replacement;
        }
        return toShuffle;
    }

    // argument is like [2,1,0,3]
    public int[] getArrayOfIndexesFromString(String indexesInString) {

        List<Integer> questionsIndexes = new ArrayList<>();

        Matcher matcher = Pattern.compile("\\d+").matcher(indexesInString);
        while (matcher.find()) {
            questionsIndexes.add(Integer.valueOf(matcher.group()));
        }
        int[] shuffledIdxs = new int[questionsIndexes.size()];
        for (int i = 0; i < questionsIndexes.size(); i++) {
            shuffledIdxs[i] = questionsIndexes.get(i);
        }
        return shuffledIdxs;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(KEY_BUNDLE_RESULTS, Arrays.toString(rightWrongAnswers));
        outState.putString(KEY_BUNDLE_SHUFFLED, Arrays.toString(shuffled));
        outState.putInt(KEY_BUNDLE_CURRENT_QUESTION, currentQuestion);
        super.onSaveInstanceState(outState);
    }
}
