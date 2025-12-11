package newsugar.Newsugar_Back.domain.quiz.service;

import newsugar.Newsugar_Back.domain.quiz.dto.SubmitResult;
import newsugar.Newsugar_Back.domain.quiz.model.Question;
import newsugar.Newsugar_Back.domain.quiz.model.Quiz;
import newsugar.Newsugar_Back.domain.quiz.model.QuizSubmission;
import newsugar.Newsugar_Back.domain.quiz.model.SubmissionAnswer;
import newsugar.Newsugar_Back.domain.quiz.repository.QuizRepository;
import newsugar.Newsugar_Back.domain.quiz.repository.QuizSubmissionRepository;
import newsugar.Newsugar_Back.domain.quiz.ai.AiQuizClient;
import newsugar.Newsugar_Back.domain.summary.repository.SummaryRepository;
import newsugar.Newsugar_Back.domain.summary.model.Summary;
import newsugar.Newsugar_Back.common.CustomException;
import newsugar.Newsugar_Back.common.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.time.Instant;
import java.util.stream.Collectors;

@Service
public class QuizServiceImpl implements QuizService {
    private final QuizRepository quizRepository;
    private final QuizSubmissionRepository quizSubmissionRepository;
    private final SummaryRepository summaryRepository;
    private final AiQuizClient aiQuizClient;

    public QuizServiceImpl(QuizRepository quizRepository, QuizSubmissionRepository quizSubmissionRepository, SummaryRepository summaryRepository, AiQuizClient aiQuizClient) {
        this.quizRepository = quizRepository;
        this.quizSubmissionRepository = quizSubmissionRepository;
        this.summaryRepository = summaryRepository;
        this.aiQuizClient = aiQuizClient;
    }

    @Override
    public Quiz create(Quiz quiz) {
        if (quiz.getStartAt() != null && quiz.getEndAt() != null) {
            if (quiz.getEndAt().isBefore(quiz.getStartAt())) {
                throw new CustomException(ErrorCode.BAD_REQUEST, "종료 시간이 시작 시간보다 빠릅니다");
            }
        }
        if (quiz.getQuestions() != null) {
            for (Question q : quiz.getQuestions()) {
                if (q.getOptions() == null || q.getOptions().size() < 2) {
                    throw new CustomException(ErrorCode.BAD_REQUEST, "객관식 옵션은 최소 2개 이상이어야 합니다");
                }
                if (q.getCorrectIndex() == null || q.getCorrectIndex() < 0 || q.getCorrectIndex() >= q.getOptions().size()) {
                    throw new CustomException(ErrorCode.BAD_REQUEST, "정답 인덱스가 옵션 범위를 벗어났습니다");
                }
            }
        }
        return quizRepository.save(quiz);
    }

    @Override
    public Quiz get(Long id) {
        return quizRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public SubmitResult score(Long id, Long userId, List<Integer> answers) {
        Quiz quiz = get(id);
        if (quiz == null) {
            throw new CustomException(ErrorCode.QUIZ_NOT_FOUND, "퀴즈를 찾을 수 없습니다");
        }
        Instant now = Instant.now();
        if ((quiz.getStartAt() != null && now.isBefore(quiz.getStartAt())) ||
            (quiz.getEndAt() != null && now.isAfter(quiz.getEndAt()))) {
            throw new CustomException(ErrorCode.QUIZ_EXPIRED, "퀴즈 제출 기간이 아닙니다");
        }

        List<Question> qs = quiz.getQuestions();
        int total = qs.size();
        int correct = 0;
        List<Boolean> results = new ArrayList<>();
        List<SubmissionAnswer> storedAnswers = new ArrayList<>();
        for (int i = 0; i < total; i++) {
            Integer answer = (answers != null && i < answers.size()) ? answers.get(i) : null;
            Integer expected = qs.get(i).getCorrectIndex();
            int optionSize = qs.get(i).getOptions() != null ? qs.get(i).getOptions().size() : 0;
            boolean inRange = (answer != null && answer >= 0 && answer < optionSize);
            boolean ok = (inRange && expected != null && answer.equals(expected));
            if (ok) correct++;
            results.add(ok);

            SubmissionAnswer sa = new SubmissionAnswer();
            sa.setQuestionIndex(i);
            sa.setChosenIndex(answer);
            sa.setCorrect(ok);
            sa.setAnsweredAt(Instant.now());
            sa.setUserId(userId);
            sa.setQuizId(quiz.getId());
            storedAnswers.add(sa);
        }
        QuizSubmission submission = new QuizSubmission();
        submission.setQuiz(quiz);
        submission.setAnswers(storedAnswers);
        submission.setTotal(total);
        submission.setCorrect(correct);
        submission.setUserId(userId);
        quizSubmissionRepository.save(submission);

        return new SubmitResult(total, correct, results, userId);
    }

    @Override
    public SubmitResult score(Long id, List<Integer> answers) {
        return score(id, null, answers);
    }

    @Override
    public List<Quiz> listToday() {
        Instant now = Instant.now();
        return quizRepository.findAll().stream()
                .filter(q -> (q.getStartAt() == null || !now.isBefore(q.getStartAt()))
                        && (q.getEndAt() == null || !now.isAfter(q.getEndAt())))
                .collect(Collectors.toList());
    }

    @Override
    public List<Quiz> listByPeriod(Instant from, Instant to) {
        List<Quiz> all = quizRepository.findAll();
        return all.stream().filter(q -> {
            Instant qs = q.getStartAt() != null ? q.getStartAt() : Instant.MIN;
            Instant qe = q.getEndAt() != null ? q.getEndAt() : Instant.MAX;
            return !(qe.isBefore(from) || qs.isAfter(to));
        }).collect(Collectors.toList());
    }

    @Override
    public SubmitResult lastResult(Long quizId) {
        return quizSubmissionRepository.findTopByQuiz_IdOrderByCreatedAtDesc(quizId)
                .map(sub -> {
                    int total = sub.getTotal();
                    int correct = sub.getCorrect();
                    List<Boolean> results = sub.getAnswers() != null ?
                            sub.getAnswers().stream().map(SubmissionAnswer::getCorrect).toList() : List.of();
                    return new SubmitResult(total, correct, results, sub.getUserId());
                })
                .orElse(null);
    }

    

    @Override
    public void ensurePlayable(Long id) {
        Quiz quiz = get(id);
        if (quiz == null) {
            throw new CustomException(ErrorCode.QUIZ_NOT_FOUND, "퀴즈를 찾을 수 없습니다");
        }
        Instant now = Instant.now();
        if ((quiz.getStartAt() != null && now.isBefore(quiz.getStartAt())) ||
            (quiz.getEndAt() != null && now.isAfter(quiz.getEndAt()))) {
            throw new CustomException(ErrorCode.QUIZ_EXPIRED, "퀴즈 시작 기간이 아닙니다");
        }
    }

    @Override
    public SubmitResult resultOrThrow(Long quizId) {
        return quizSubmissionRepository.findTopByQuiz_IdOrderByCreatedAtDesc(quizId)
                .map(sub -> {
                    int total = sub.getTotal();
                    int correct = sub.getCorrect();
                    List<Boolean> results = sub.getAnswers() != null ?
                            sub.getAnswers().stream().map(SubmissionAnswer::getCorrect).toList() : List.of();
                    return new SubmitResult(total, correct, results, sub.getUserId());
                })
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "결과가 없습니다"));
    }

    @Override
    public Quiz generateFromSummary(Long summaryId) {
        Summary summary = summaryRepository.findById(summaryId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "요약을 찾을 수 없습니다"));

        List<AiQuizClient.QuestionData> gen = aiQuizClient.generate(summary.getSummaryText());
        List<Question> questions = new ArrayList<>();
        if (gen != null) {
            for (AiQuizClient.QuestionData d : gen) {
                Question q = new Question();
                q.setText(d.text);
                q.setOptions(d.options != null ? d.options : List.of());
                q.setCorrectIndex(d.correctIndex);
                questions.add(q);
            }
        }
        Quiz quiz = new Quiz();
        quiz.setSummary(summary);
        quiz.setQuestions(questions);
        return create(quiz);
    }
}
