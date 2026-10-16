package com.example.pbl2mathchamps;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    // ---------- Quiz data (10 questions) ----------
    private final String[] questions = {
            "5 + 3 = ?",
            "12 - 7 = ?",
            "6 x 4 = ?",
            "20 \u00F7 5 = ?",
            "9 + 8 = ?",
            "15 - 9 = ?",
            "7 x 3 = ?",
            "36 \u00F7 6 = ?",
            "10 x 10 = ?",
            "25 + 17 = ?"
    };

    private final String[][] options = {
            {"6", "7", "8", "9"},
            {"4", "5", "6", "3"},
            {"24", "20", "28", "18"},
            {"3", "4", "5", "6"},
            {"16", "17", "18", "15"},
            {"5", "6", "7", "8"},
            {"21", "24", "18", "28"},
            {"5", "7", "6", "8"},
            {"100", "1000", "110", "10"},
            {"41", "43", "42", "44"}
    };

    // index (0-3) of the correct button for each question
    private final int[] correctAnswers = {2, 1, 0, 1, 1, 1, 0, 2, 0, 2};

    // ---------- State ----------
    private int score;
    private int question;          // index of current question

    // ---------- Views ----------
    private TextView tvProgress, tvQuestion, tvScoreResult, tvMessage;
    private Button[] answerButtons = new Button[4];
    private LinearLayout startContainer, quizContainer, resultContainer;
    private Button btnStart, btnRestart;

    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvProgress = findViewById(R.id.tvProgress);
        tvQuestion = findViewById(R.id.tvQuestion);
        answerButtons[0] = findViewById(R.id.btnAnswer1);
        answerButtons[1] = findViewById(R.id.btnAnswer2);
        answerButtons[2] = findViewById(R.id.btnAnswer3);
        answerButtons[3] = findViewById(R.id.btnAnswer4);
        startContainer = findViewById(R.id.startContainer);
        btnStart = findViewById(R.id.btnStart);
        quizContainer = findViewById(R.id.quizContainer);
        resultContainer = findViewById(R.id.resultContainer);
        tvScoreResult = findViewById(R.id.tvScoreResult);
        tvMessage = findViewById(R.id.tvMessage);
        btnRestart = findViewById(R.id.btnRestart);

        // Each button reports its own index when tapped  ("User taps an answer")
        for (int i = 0; i < answerButtons.length; i++) {
            final int index = i;
            answerButtons[i].setOnClickListener(v -> onAnswerTapped(index));
        }

        // Start page: quiz only begins when the user taps Start
        btnStart.setOnClickListener(v -> initializeQuiz());

        btnRestart.setOnClickListener(v -> initializeQuiz());

        // App opens on the start page (quiz and result screens are hidden in the layout)
    }

    // ===== Initialize quiz =====
    private void initializeQuiz() {
        score = 0;
        question = 0;
        startContainer.setVisibility(View.GONE);
        resultContainer.setVisibility(View.GONE);
        quizContainer.setVisibility(View.VISIBLE);
        showQuestion();
    }

    // ===== Show question (TextView + 4 buttons) =====
    private void showQuestion() {
        tvProgress.setText("Question " + (question + 1) + " / " + questions.length);
        tvQuestion.setText(questions[question]);
        for (int i = 0; i < answerButtons.length; i++) {
            answerButtons[i].setText(options[question][i]);
            answerButtons[i].setEnabled(true);
        }
    }

    // ===== User taps an answer -> Correct? =====
    private void onAnswerTapped(int selectedIndex) {
        setButtonsEnabled(false);              // stop double taps while popup shows

        boolean isCorrect = (selectedIndex == correctAnswers[question]);

        if (isCorrect) {
            score = score + 1;                 // Update score
            showPopup(true);                   // Show tick image + "Correct!"
        } else {
            showPopup(false);                  // Show cross image + "Wrong!"
        }
    }

    // ===== Popup (tick or cross) =====
    private void showPopup(boolean correct) {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_feedback, null);
        ImageView img = view.findViewById(R.id.imgFeedback);
        TextView txt = view.findViewById(R.id.tvFeedback);

        if (correct) {
            img.setImageResource(R.drawable.ic_tick);
            txt.setText("Correct!");
        } else {
            img.setImageResource(R.drawable.ic_cross);
            txt.setText("Wrong!");
        }

        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(view)
                .setCancelable(false)
                .create();
        dialog.show();

        // Auto close after 1 second, then go to the next step
        handler.postDelayed(() -> {
            if (dialog.isShowing()) dialog.dismiss();
            goToNextQuestion();
        }, 1000);
    }

    // ===== Question = Question + 1  ->  More questions? =====
    private void goToNextQuestion() {
        question = question + 1;

        if (question < questions.length) {     // Yes -> loop back
            showQuestion();
        } else {                               // No  -> results
            showResults();
        }
    }

    // ===== Show results (score out of 10 + message) =====
    private void showResults() {
        quizContainer.setVisibility(View.GONE);
        resultContainer.setVisibility(View.VISIBLE);

        tvScoreResult.setText("Your score: " + score + " / " + questions.length);

        String message;
        if (score == questions.length) {
            message = "Perfect! Amazing job!";
        } else if (score >= 7) {
            message = "Great work!";
        } else if (score >= 4) {
            message = "Good try, keep practising!";
        } else {
            message = "Don't give up, try again!";
        }
        tvMessage.setText(message);
    }

    private void setButtonsEnabled(boolean enabled) {
        for (Button b : answerButtons) b.setEnabled(enabled);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}