package newsugar.Newsugar_Back.domain.quiz.service;

import newsugar.Newsugar_Back.domain.quiz.dto.SubmitResult;
import newsugar.Newsugar_Back.domain.quiz.model.Question;
import newsugar.Newsugar_Back.domain.quiz.model.Quiz;
import newsugar.Newsugar_Back.domain.quiz.model.QuizSubmission;
import newsugar.Newsugar_Back.domain.quiz.model.SubmissionAnswer;
import newsugar.Newsugar_Back.domain.quiz.repository.QuizRepository;
import newsugar.Newsugar_Back.domain.quiz.repository.QuizSubmissionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class QuizServiceImpl implements QuizService {
    private final QuizRepository quizRepository;
    private final QuizSubmissionRepository quizSubmissionRepository;

    public QuizServiceImpl(QuizRepository quizRepository, QuizSubmissionRepository quizSubmissionRepository) {
        this.quizRepository = quizRepository;
        this.quizSubmissionRepository = quizSubmissionRepository;
    }

    @Override
    public Quiz create(Quiz quiz) {
        return quizRepository.save(quiz);
    }

    @Override
    public Quiz get(Long id) {
        return quizRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public SubmitResult score(Long id, List<Integer> answers) {
        Quiz quiz = get(id);
        if (quiz == null) return new SubmitResult(0, 0, List.of());

        List<Question> qs = quiz.getQuestions();
        int total = qs.size();
        int correct = 0;
        List<Boolean> results = new ArrayList<>();
        List<SubmissionAnswer> storedAnswers = new ArrayList<>();
        for (int i = 0; i < total; i++) {
            Integer answer = (answers != null && i < answers.size()) ? answers.get(i) : null;
            Integer expected = qs.get(i).getCorrectIndex();
            boolean ok = (answer != null && expected != null && answer.equals(expected));
            if (ok) correct++;
            results.add(ok);

            SubmissionAnswer sa = new SubmissionAnswer();
            sa.setQuestionIndex(i);
            sa.setChosenIndex(answer);
            sa.setCorrect(ok);
            storedAnswers.add(sa);
        }
        QuizSubmission submission = new QuizSubmission();
        submission.setQuiz(quiz);
        submission.setAnswers(storedAnswers);
        submission.setTotal(total);
        submission.setCorrect(correct);
        quizSubmissionRepository.save(submission);

        return new SubmitResult(total, correct, results);
    }
}
