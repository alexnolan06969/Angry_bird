package com.jil;

import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;

public class HeadlessSetup {
    public static void initialize() {
        if (com.badlogic.gdx.Gdx.app == null) {
            HeadlessApplicationConfiguration config = new HeadlessApplicationConfiguration();
            new HeadlessApplication(new DummyApplicationListener(), config);
        }
    }
    private static class DummyApplicationListener implements com.badlogic.gdx.ApplicationListener {
        @Override public void create() {}
        @Override public void resize(int width, int height) {}
        @Override public void render() {}
        @Override public void pause() {}
        @Override public void resume() {}
        @Override public void dispose() {}
    }
}
