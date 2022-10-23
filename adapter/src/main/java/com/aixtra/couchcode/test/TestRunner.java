package com.aixtra.couchcode.test;

import com.aixtra.couchcode.client.challenge.api.EvaluationApi;
import com.aixtra.couchcode.client.challenge.api.TasksApi;
import com.aixtra.couchcode.client.challenge.model.EvaluationScore;
import com.aixtra.couchcode.client.challenge.model.TaskDifficulty;
import com.aixtra.couchcode.client.challenge.model.TaskWithSolution;
import com.aixtra.couchcode.client.ocr.OCRClient;
import com.aixtra.couchcode.util.files.FileBuilder;
import io.micronaut.jackson.databind.JacksonDatabindMapper;
import jakarta.inject.Singleton;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;
import reactor.util.retry.Retry;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;

@Singleton
public class TestRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestRunner.class);
    private final EvaluationApi evaluationApi;
    private final TasksApi tasksApi;
    private final OCRClient ocrClient;
    private final FileBuilder fileBuilder;

    TestRunner(EvaluationApi evaluationApi, TasksApi tasksApi, OCRClient ocrClient, FileBuilder fileBuilder) {
        this.evaluationApi = evaluationApi;
        this.tasksApi = tasksApi;
        this.ocrClient = ocrClient;
        this.fileBuilder = fileBuilder;
    }

    public Mono<String> runNewTest(TaskDifficulty difficulty) {
        return tasksApi.generateTask(difficulty)
                .retryWhen(Retry.backoff(5, Duration.ofSeconds(1))
                        .doBeforeRetry(signal -> LOGGER.warn("Failed to generate task {} times: {}, retrying...", signal.totalRetries() + 1, signal.failure().getMessage()))
                ).flatMap(task -> {
                    byte[] image = Base64.getDecoder().decode(task.getTask());
                    return Mono.zip(
                            Mono.just(fileBuilder.writeToFile(false, image)),
                            ocrClient.recognize(image)
                                    .retryWhen(Retry.backoff(5, Duration.ofSeconds(1))
                                            .doBeforeRetry(signal -> LOGGER.warn("Ocr for  task {} failed {} times: {}, retrying...", task.getId(), signal.totalRetries() + 1, signal.failure().getMessage()))
                                    ).flatMap(ocrResult ->
                                            evaluationApi.evaluate(task.getId(), ocrResult)
                                                    .retryWhen(Retry.backoff(5, Duration.ofSeconds(1))
                                                            .doBeforeRetry(signal -> LOGGER.warn("Failed to evaluate task {} times: {}, retrying...", signal.totalRetries() + 1, signal.failure().getMessage()))
                                                    ).doOnNext(evaluationResult -> LOGGER.info("Evaluation result: {}", evaluationResult))
                                                    .map(evalResult -> Tuples.of(task, evalResult, ocrResult))
                                    ));
                }).map(tuple -> {
                    String imageName = tuple.getT1();
                    TaskWithSolution task = tuple.getT2().getT1();
                    EvaluationScore score = tuple.getT2().getT2();
                    String solution = new String(tuple.getT2().getT3(), StandardCharsets.UTF_8);

                    JSONObject solutionJson = new JSONObject(solution);
                    JSONObject referenceSolution = new JSONObject();
                    try {
                        JacksonDatabindMapper mapper = new JacksonDatabindMapper();
                        byte[] bytes = mapper.writeValueAsBytes(task.getSolution());
                        referenceSolution = new JSONObject(new String(bytes, StandardCharsets.UTF_8));
                    } catch (IOException e) {
                        LOGGER.warn("Failed to serialize solution", e);
                    }
                    return new JSONObject()
                            .put("id", task.getId())
                            .put("image", imageName)
                            .put("difficulty", difficulty)
                            .put("solution", solutionJson.toMap())
                            .put("reference_solution", referenceSolution.toMap())
                            .put("score", score.getAchievedScore())
                            .put("maxScore", score.getPossibleScore())
                            .toString();
                });


    }

}

