package com.swiftcart.inventory.jobs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class BackfillJob {

    private static final Logger log = LoggerFactory.getLogger(BackfillJob.class);

    @Scheduled(fixedDelayString = "PT5M")
    public void reconcile() {
        // TODO: reconcile inventory from authoritative sources (orders DB + movements)
        log.info("Running inventory reconciliation job...");
    }
}
