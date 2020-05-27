package com.twelvenines.radiosai;

import io.muserver.*;
import io.muserver.acme.AcmeCertManager;
import io.muserver.acme.AcmeCertManagerBuilder;
import io.muserver.handlers.ResourceHandlerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class App {

    private static final Logger log = LoggerFactory.getLogger(App.class);

    MuServer mu;

    private final String USER = System.getenv().getOrDefault("USER", "raj");

    App() throws Exception {

        AcmeCertManager certManager = AcmeCertManagerBuilder.letsEncrypt()
                .withDomain("h.12nines.com")
                .withConfigDir(new File("ssl"))
                .disable(USER.equals("raj"))
                .build();

        AudioStorePopulator populator = new AudioStorePopulator();

        mu = MuServerBuilder
                .httpServer()
                .withHttpPort(8080)
                .withHttpsPort(8443)
                .withHttpsConfig(certManager.createHttpsConfig())
                .addHandler(certManager.createHandler())
                .addHandler(Method.GET, "/", (req, res, params) -> {
                    res.redirect("/radiosai");
                })
                .addHandler(ContextHandlerBuilder.context("radiosai")
                        .addHandler(ResourceHandlerBuilder.fileOrClasspath("src/main/resources/webapp", "/webapp"))
                        .addHandler(Method.GET, "/itunes.xml", (req, res, params) -> {
                            ITunesPodcastFeedServlet i = new ITunesPodcastFeedServlet();
                            res.contentType(ContentTypes.APPLICATION_XML);
                            res.write(i.get());
                        })
                        .addHandler(Method.GET, "/api/audioItems", (req, res, params) -> {
                            res.write(AudioStore.getInstance().asJsonArray().toString(2));
                        }))
                .start();

        certManager.start(mu);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            certManager.stop();
            mu.stop();
        }));

        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleWithFixedDelay(() -> {
            try {
                populator.populateAudioStore();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0, 1, TimeUnit.HOURS);
    }

    public static void main(String[] args) throws Exception {
        App app = new App();
        log.info("Started App: {}", app.mu.uri().toString());
    }
}
