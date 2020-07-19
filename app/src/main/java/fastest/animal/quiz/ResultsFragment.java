package fastest.animal.quiz;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.Arrays;

public class ResultsFragment extends Fragment {

    // results to process
    private int[] results;

    // questions
    private Question[] questions;

    // shuffled indexes
    private int[] shuffled;

    // keys to save values
    public static final String KEY_RESULTS = "results";
    public static final String KEY_SHUFFLED = "shuffled";

    public ResultsFragment() {
        // Required empty public constructor
    }

    public ResultsFragment(int[] results, int[] shuffled) {
        this.results = results;
        this.shuffled = shuffled;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_results, container, false);

        if (savedInstanceState != null) {
            results = ((QuizActivity)getActivity()).getArrayOfIndexesFromString(savedInstanceState.getString(KEY_RESULTS));
            shuffled = ((QuizActivity)getActivity()).getArrayOfIndexesFromString(savedInstanceState.getString(KEY_SHUFFLED));
        }

        // questions
        questions = ((QuizActivity)getActivity()).getQuestions();

        // number of right answers
        int nRightAnswers = nRightAnswers();

        // whole number of questions
        int nQuestions = results.length;

        // apply circle
        applyCircle(nRightAnswers, layout);

        // put text with number of right questions
        applyResultsNumberText(nRightAnswers, nQuestions, layout);

        // build results with explanations
        buildResultsWithExplanations(layout);

        // again button
        layout.findViewById(R.id.button_again).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), StartQuizActivity.class);
                startActivity(intent);
            }
        });

        // read more button
        layout.findViewById(R.id.button_read_more).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(Utils.isNetworkConnected(getActivity())) {
                    Intent intent = new Intent(getActivity(), WebActivity.class);
                    intent.putExtra(WebActivity.KEY_TITLE_ID, R.string.read_more_title);
                    startActivity(intent);
                } else {
                    // show message
                    showNoInternetMessage();
                }
            }
        });

        // leave a review button
        layout.findViewById(R.id.button_leave_review).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(Utils.isNetworkConnected(getActivity())) {
                    try {
                        startActivity(new Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("market://details?id=" + getActivity().getPackageName())));
                    } catch (android.content.ActivityNotFoundException e) {
                        Intent intent = new Intent(getActivity(), WebActivity.class);
                        intent.putExtra(WebActivity.KEY_TITLE_ID, R.string.leave_a_review_title);
                        startActivity(intent);
                    }

                } else {
                    // show message
                    showNoInternetMessage();
                }
            }
        });

        // special thanks
        prepareSpecialThanks(layout);

        return layout;
    }

    ;public void prepareSpecialThanks(View layout) {

        TextView textView = layout.findViewById(R.id.special_thanks_text);
        String text = getString(R.string.special_thanks_text);
        SpannableString spannableString = new SpannableString(text);
        ClickableSpan wikiSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                if(Utils.isNetworkConnected(getActivity())) {
                    Intent intent = new Intent(getActivity(), WebActivity.class);
                    intent.putExtra(WebActivity.KEY_TITLE_ID, R.string.wikipedia_title);
                    startActivity(intent);
                } else {
                    showNoInternetMessage();
                }
            }
        };
        spannableString.setSpan(wikiSpan, 20, 33, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(spannableString);
        textView.setMovementMethod(LinkMovementMethod.getInstance());

    }

    public void showNoInternetMessage() {
        TextView noInternetTextView = (TextView) LayoutInflater.from(getActivity())
                .inflate(R.layout.internet_problem_message_layout, null, false);
        Toast toast = new Toast(getActivity());
        toast.setView(noInternetTextView);
        toast.setDuration(Toast.LENGTH_LONG);
        int topOffset = (int)getActivity().getResources().getDimension(R.dimen.no_internet_top_offset);
        toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP, 0, topOffset);
        toast.show();

    }

    // apply appropriate circle for results
    public void applyCircle(int nRightAnswers, View layout) {
        ImageView circleImageView = layout.findViewById(R.id.results_circle);
        circleImageView.setImageResource(getCircleId(nRightAnswers));
    }

    // put text as number of right answers
    public void applyResultsNumberText(int nRightAnswers, int nQuestions, View layout) {
        TextView totalResultsTextView = layout.findViewById(R.id.results_text);
        totalResultsTextView.setText("" + nRightAnswers + "/" + nQuestions);
    }

    // build results. (questions, indicators, results)
    public void buildResultsWithExplanations(View layout) {
        LinearLayout explanationsContainer = layout.findViewById(R.id.explanations_container);
        int idx = 0;
        for (int shuffledIdx: shuffled) {
            LinearLayout questionExplanationLayout = (LinearLayout) LayoutInflater.from(getContext())
                    .inflate(R.layout.question_explanation_layout, explanationsContainer, false);

            Question question = questions[shuffledIdx];

            // question
            ((TextView)questionExplanationLayout.findViewById(R.id.question))
                    .setText("" + (idx + 1) + ". " + question.getQuestionText());
            // indicator
            if (results[idx] != QuizActivity.RIGHT) {
                ((ImageView) questionExplanationLayout.findViewById(R.id.right_wrong_indicator))
                        .setImageResource(R.drawable.ic_wrong_colored);
            }
            // explanation
            ((TextView) questionExplanationLayout.findViewById(R.id.question_explanation_text))
                    .setText(question.getExplanationText());

            // adding inflated view
            explanationsContainer.addView(questionExplanationLayout);

            idx++;

        }

    }

    // number of right answers
    public int nRightAnswers() {
        int nRightAnswers = 0;
        for (int result: results) {
            if (result == QuizActivity.RIGHT)
                nRightAnswers++;
        }
        return nRightAnswers;
    }

    // circle shape for appropriate number of answers
    public int getCircleId(int nRightAnswers) {
        switch (nRightAnswers) {
            case 0:
            case 1:
            case 2:
                return R.drawable.circle_results_1;
            case 3:
            case 4:
                return R.drawable.circle_results_2;
            case 5:
            case 6:
                return R.drawable.circle_results_3;
            case 7:
            case 8:
                return R.drawable.circle_results_4;
            case 9:
            case 10:
                return R.drawable.circle_results_5;
            case 11:
            case 12:
                return R.drawable.circle_results_6;
            case 13:
            case 14:
                return R.drawable.circle_results_7;
            default:
                return R.drawable.circle_results_1;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString(KEY_RESULTS, Arrays.toString(results));
        savedInstanceState.putString(KEY_SHUFFLED, Arrays.toString(shuffled));
    }
}
