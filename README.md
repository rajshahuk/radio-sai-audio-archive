# radio-sai-audio-archive

Instructions for installing gcloud java stuff:
https://cloud.google.com/appengine/docs/standard/java/download

## How to build...

Using maven - run `mvn package` to make sure everything works okay. You should not see any build failures.

# How to run locally....

```bash
mvn com.google.cloud.tools:appengine-maven-plugin:run
```

## How to deploy...

```bash
mvn package appengine:deploy
```
