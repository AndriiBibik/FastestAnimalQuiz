package fastest.animal.quiz;

import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QuizActivity extends AppCompatActivity {

    // shared preferences
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    public static final String KEY_QUESTIONS_ORDER = "order";
    public static final String KEY_CURRENT_QUESTION = "current_question";

    private static final String LOG_TAG = QuizActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        // questions
        //
        String[] questionsArr = getResources().getStringArray(R.array.questions);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPreferences.edit();

        // initializing shuffled array
        int[] shuffled = null;
        // get shuffled array
        if (!sharedPreferences.contains(KEY_QUESTIONS_ORDER)) {
            // shuffle questions and save info into shared prefs
            int[] idxs = new int[questionsArr.length];
            for (int i = 0; i < questionsArr.length; i++) idxs[i] = i;
            shuffled = shuffleIntArray(idxs);
            // save into shared prefs
            editor.putString(KEY_QUESTIONS_ORDER, Arrays.toString(shuffled)).apply();
        } else {
            shuffled = getArrayOfIndexesForQuestions();
        }

        // get current question idx
        int currentQuestionIdx = 0;
        if (sharedPreferences.contains(KEY_CURRENT_QUESTION)) {
            currentQuestionIdx = sharedPreferences.getInt(KEY_CURRENT_QUESTION, 0);
        }

        // get questions with answers
        Question[] questions = getQuestions(questionsArr);

        Fragment fragment = new QuestionFragment(questions[shuffled[currentQuestionIdx]]);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.question_fragment, fragment);
        fragmentTransaction.commit();
    }

    // get question from resources

    public Question[] getQuestions(String[] questionsArray) {

        Question[] questions = new Question[questionsArray.length];

        for (int i = 0; i < questionsArray.length; i++) {

            // current answers id
            int arryid = getResources()
                    .getIdentifier("answers_" + (i+1), "array", getPackageName());
            // current answers images id
            int arryIdImg = getResources()
                    .getIdentifier("answers_images_" + (i+1), "array", getPackageName());
            Log.v(LOG_TAG, "images arr id: " + arryIdImg);
            int idRightAnswers = getResources()
                    .getIdentifier("right_answers_" + (i+1), "array", getPackageName());
            String[] answersStrings = getResources().getStringArray(arryid);
            TypedArray imageResourcesTypedArray = getResources().obtainTypedArray(arryIdImg);
            int[] rightAnswersIds = getResources().getIntArray(idRightAnswers);

            Answer[] answers = new Answer[answersStrings.length];

            for (int j = 0; j < answersStrings.length; j++) {
                answers[j] = new Answer(j, imageResourcesTypedArray.getResourceId(j, -1), answersStrings[j]);
            }
            questions[i] = new Question(questionsArray[i], answers, rightAnswersIds);
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

    public int[] getArrayOfIndexesForQuestions() {
        String idxString = sharedPreferences.getString(KEY_QUESTIONS_ORDER, "");

        List<Integer> questionsIndexes = new ArrayList<>();

        Matcher matcher = Pattern.compile("\\d+").matcher(idxString);
        while(matcher.find()) {
            questionsIndexes.add(Integer.valueOf(matcher.group()));
        }
        int[] shuffledIdxs = new int[questionsIndexes.size()];
        for (int i = 0; i < questionsIndexes.size(); i++) {
            shuffledIdxs[i] = questionsIndexes.get(i);
        }
        return shuffledIdxs;
    }

}
