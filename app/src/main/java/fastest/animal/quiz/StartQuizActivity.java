package fastest.animal.quiz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class StartQuizActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_quiz);
        ButterKnife.bind(this);
    }

    // starting quiz activity
    @OnClick(R.id.start_quiz_button)
    void startQuiz() {
        Intent intent = new Intent(StartQuizActivity.this, QuizActivity.class);
        startActivity(intent);
    }
}
