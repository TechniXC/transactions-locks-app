package ru.jpoint.transactionslocksapp.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.jpoint.transactionslocksapp.dto.Likes;
import ru.jpoint.transactionslocksapp.service.SpeakerService;

@Slf4j
@RestController
@RequiredArgsConstructor
public class TestPoolController {

    @GetMapping("/test")
    @Transactional
    public ResponseEntity<String> testTransaction() throws InterruptedException {
        log.warn("Thread {} started", Thread.currentThread().getId());
        log.warn("Thread {} finished the work", Thread.currentThread().getId());
        return new ResponseEntity<>("Test passed!", HttpStatus.OK);
    }

    //<editor-fold desc="TransactionTemplate">

    private final TransactionTemplate transactionTemplate;
    private final SpeakerService speakerService;


    private void usingTransactionTemplate() {
        transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_DEFAULT);
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        transactionTemplate.setTimeout(5);
        transactionTemplate.setName(this.getClass().getSimpleName());
        transactionTemplate.execute(status -> {
            var likes = Likes.builder()
                    .talkName("Spring best practice")
                    .likes(100)
                    .build();
            speakerService.addLikesToSpeaker(likes);
//            throw new RuntimeException();
            return likes.getTalkName();
        });
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                speakerService.addLikesToSpeaker(Likes.builder()
                        .talkName("Spring best practice")
                        .likes(100)
                        .build());
            }
        });
    }
    //</editor-fold>
}
