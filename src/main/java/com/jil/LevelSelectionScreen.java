package com.jil;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.audio.Music;

public class LevelSelectionScreen implements Screen {

    private final Main game;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Viewport viewport;
    private Stage stage;
    private Texture backgroundTexture;
    private Texture unlockedTexture;
    private Texture lockedTexture;
    private Texture backTexture;
    private ImageButton nextButton, prevButton, backButton;
    private BitmapFont customFont;
    private Sound clickSound;
    private Music backgroundMusic;

    private final float[][] BUTTON_POSITIONS = {
        {300, 700}, {500, 700}, {700, 700}, {900, 700}, {1100, 700}, {1300, 700}, {1500, 700}, // Top row
        {300, 500}, {500, 500}, {700, 500}, {900, 500}, {1100, 500}, {1300, 500}, {1500, 500}, // Middle row
        {300, 300}, {500, 300}, {700, 300}, {900, 300}, {1100, 300}, {1300, 300}, {1500, 300}  // Bottom row
    };

    private int currentPage = 1;
    private int totalLevels = 105;

    public LevelSelectionScreen(Main game,Music backgroundmusic) {
        this.game = game;
        batch = game.getBatch();
        camera = new OrthographicCamera();
        viewport = new StretchViewport(1920, 1080, camera);
        camera.position.set(viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 2, 0);
        camera.update();
        backgroundTexture = new Texture(Gdx.files.internal("BACKGROUNDS_GE_1.PNG"));
        unlockedTexture = new Texture(Gdx.files.internal("Level_Unlocked.png"));
        lockedTexture = new Texture(Gdx.files.internal("Level_Locked.png"));
        backTexture = new Texture(Gdx.files.internal("BACK.png"));
        customFont = loadCustomFont(36);
        stage = new Stage(viewport, batch);
        Gdx.input.setInputProcessor(stage);
        createNavigationButtons();
        createBackButton();
        backgroundMusic = backgroundmusic;
        loadLevelsForPage(currentPage);
    }

    private BitmapFont loadCustomFont(int fontSize) {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("angrybirds-regular.ttf"));
        FreeTypeFontParameter parameter = new FreeTypeFontParameter();
        parameter.size = fontSize;
        parameter.color = Color.WHITE;

        BitmapFont font = generator.generateFont(parameter);
        generator.dispose();
        return font;
    }

    private void createNavigationButtons() {
        Texture nextIcon = new Texture(Gdx.files.internal("next.png"));
        Texture prevIcon = new Texture(Gdx.files.internal("prev.png"));

        nextButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(nextIcon)));
        prevButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(prevIcon)));


        nextButton.setSize(150, 150);
        prevButton.setSize(150, 150);
        nextButton.setPosition(viewport.getWorldWidth() - nextButton.getWidth() - 50, (viewport.getWorldHeight() - nextButton.getHeight()) / 2 + 20 );
        prevButton.setPosition(50, (viewport.getWorldHeight() - prevButton.getHeight()) / 2 + 20);
        prevButton.setVisible(false);
        nextButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                int maxPage = (getTotalLevels() + BUTTON_POSITIONS.length - 1) / BUTTON_POSITIONS.length;
                if (currentPage < maxPage) {
                    currentPage++;
                    loadLevelsForPage(currentPage);
                }
            }
        });

        prevButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (currentPage > 1) {
                    currentPage--;
                    loadLevelsForPage(currentPage);
                }
            }
        });
        stage.addActor(nextButton);
        stage.addActor(prevButton);
    }

    private void createBackButton(){
        backButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(backTexture)));
        backButton.setSize(100, 100);
        backButton.setPosition(50, viewport.getWorldHeight() - backButton.getHeight() - 50);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("restart button clicked!");
                clickSound.play();
                if(backgroundMusic != null) backgroundMusic.stop();
                game.setScreen(new SelectEpisodeScreen(game.getBatch(),game));
            }
        });

        stage.addActor(backButton);

    }

    private int getTotalLevels() {
        return totalLevels;
    }

    private int getHighestLevelUnlocked() {
        Preferences prefs = Gdx.app.getPreferences("MyPreferences");
        return prefs.getInteger("highestLevelUnlocked", 68);
    }

    private boolean isLevelUnlocked(int levelIndex) {
        int highestLevelUnlocked = getHighestLevelUnlocked();
        return levelIndex <= highestLevelUnlocked;
    }

    private void loadLevelsForPage(int page) {
        stage.clear();
        stage.addActor(nextButton);
        stage.addActor(prevButton);
        stage.addActor(backButton);

        int levelsPerPage = BUTTON_POSITIONS.length;

        int start = (page - 1) * levelsPerPage;
        int end = Math.min(start + levelsPerPage, getTotalLevels());

        for (int i = start; i < end; i++) {
            boolean unlocked = isLevelUnlocked(i);
            int positionIndex = i % levelsPerPage;

            float x = BUTTON_POSITIONS[positionIndex][0];
            float y = BUTTON_POSITIONS[positionIndex][1];

            TextureRegionDrawable drawable = new TextureRegionDrawable(
                new TextureRegion(unlocked ? unlockedTexture : lockedTexture)
            );

            ImageButton levelButton = new ImageButton(drawable);
            levelButton.setSize(100, 100);
            levelButton.setPosition(0, 0);

            if (unlocked) {
                Label.LabelStyle labelStyle = new Label.LabelStyle();
                labelStyle.font = customFont;

                Label levelLabel = new Label(String.valueOf(i + 1), labelStyle);
                levelLabel.setSize(100, 100);
                levelLabel.setAlignment(Align.center);
                float shiftAmount = levelButton.getHeight() * 0.097f;
                levelLabel.setPosition(0, shiftAmount);
                levelLabel.setTouchable(Touchable.disabled);

                Group buttonWithLabel = new Group();
                buttonWithLabel.setSize(100, 100);
                buttonWithLabel.setPosition(x, y);

                levelButton.setPosition(0, 0);
                buttonWithLabel.addActor(levelButton);
                buttonWithLabel.addActor(levelLabel);
                stage.addActor(buttonWithLabel);

                final int levelNumber = i + 1;
                if(levelNumber == 1){
                    levelButton.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeEvent event, Actor actor) {

                            if(backgroundMusic != null) backgroundMusic.stop();
                            game.setScreen(new GameScreen(game));
                        }
                    });
                }
                else if(levelNumber == 2){
                    levelButton.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeEvent event, Actor actor) {

                            if(backgroundMusic != null) backgroundMusic.stop();
                            game.setScreen(new Level2(game));
                        }
                    });
                }
                else if(levelNumber == 3){
                    levelButton.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeEvent event, Actor actor) {

                            if(backgroundMusic != null) backgroundMusic.stop();
                            game.setScreen(new Level3(game));
                        }
                    });
                }else{
                    levelButton.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeEvent event, Actor actor) {

                            if(backgroundMusic != null) backgroundMusic.stop();
                            game.setScreen(new GameScreen(game));
                        }
                    });
                }
            } else {
                levelButton.setPosition(x, y);
                stage.addActor(levelButton);
            }
        }
        prevButton.setVisible(currentPage > 1);
        int maxPage = (getTotalLevels() + levelsPerPage - 1) / levelsPerPage;
        nextButton.setVisible(currentPage < maxPage);
    }

    @Override
    public void show() {
        clickSound = Gdx.audio.newSound(Gdx.files.internal("click.mp3"));
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(backgroundTexture, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());
        batch.end();

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        camera.position.set(viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 2, 0);
        camera.update();
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        batch.dispose();
        backgroundTexture.dispose();
        unlockedTexture.dispose();
        lockedTexture.dispose();
        stage.dispose();
        customFont.dispose();
    }
}
