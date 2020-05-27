package com.twelvenines.radiosai;

import io.muserver.Method;
import io.muserver.MuServer;
import io.muserver.MuServerBuilder;
import io.muserver.acme.AcmeCertManager;
import io.muserver.acme.AcmeCertManagerBuilder;
import io.muserver.handlers.ResourceHandlerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class App {

    private static final Logger log = LoggerFactory.getLogger(App.class);

    MuServer mu;

    App() throws Exception {

        AcmeCertManager certManager = AcmeCertManagerBuilder.letsEncrypt()
                .withDomain("h.12nines.com")
                .withConfigDir(new File("ssl"))
                .build();

        mu = MuServerBuilder
                .httpServer()
                .withHttpPort(8080)
                .withHttpsPort(8443)
                .withHttpsConfig(certManager.createHttpsConfig())
                .addHandler(certManager.createHandler())
                .addHandler(ResourceHandlerBuilder.fileOrClasspath("src/main/resources/webapp", "/webapp"))
                .addHandler(Method.POST, "/fetch", (req, res, params) -> {
                    FetchServlet f = new FetchServlet();
                    f.doGet(req, res);
                })
                .addHandler(Method.GET, "/itunes.xml", (req, res, params) -> {
                    ITunesPodcastFeedServlet i = new ITunesPodcastFeedServlet();
                    res.write(i.get());
                })
                .addHandler(Method.GET, "/api/audioItems", (req, res, params) -> {
                    res.write(AudioStore.getInstance().asJsonArray().toString(2));
                })
                .start();

        certManager.start(mu);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            certManager.stop();
            mu.stop();
        }));
    }

    public static void main(String[] args) throws Exception {
        App app = new App();
        new FetchServlet().main();
        log.info("Started App: {}", app.mu.uri().toString());
    }
}
