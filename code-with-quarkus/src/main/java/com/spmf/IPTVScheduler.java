package com.spmf;


import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class IPTVScheduler {

    @Inject
    IPTVConfig config;

    @Inject
    IPTVImportService importer;

    @Scheduled(every = "12h")
    @Transactional
    void refresh() throws Exception {

        for (IPTVConfig.Provider provider : config.providers()) {

            importer.importFromUrl(
                    provider.name(),
                    provider.url()
            );

        }

    }

}