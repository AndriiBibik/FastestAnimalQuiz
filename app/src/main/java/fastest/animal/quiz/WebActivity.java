package fastest.animal.quiz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebActivity extends AppCompatActivity {

    private WebView webView;

    // intent key
    public static final String KEY_TITLE_ID = "title";

    public static final int READ_MORE_TITLE_ID = R.string.read_more_title;
    public static final int LEAVE_A_REVIEW_TITLE_ID = R.string.leave_a_review_title;
    public static final int WIKIPEDIA_TITLE_ID = R.string.wikipedia_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        // enabling up button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        int titleId = intent.getIntExtra(KEY_TITLE_ID, -1);
        String url = "";
        if (titleId != -1) {
            setTitle(titleId);
            if (titleId == READ_MORE_TITLE_ID) {
                url = getString(R.string.read_more_wiki_page);
            } else if (titleId == LEAVE_A_REVIEW_TITLE_ID) {
                url = getString(R.string.leave_a_review_page_without_package) + getPackageName();
            } else if (titleId == WIKIPEDIA_TITLE_ID) {
                url = getString(R.string.wikipedia_page);
            }
        }

        webView = findViewById(R.id.web_view);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(url);
        webView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                findViewById(R.id.progress_indicator).setVisibility(View.GONE);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
