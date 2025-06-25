package com.jil;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.audio.Music;

public class ResultScreen3 implements Screen {

    private final Main game;
    private SpriteBatch batch;
    private Stage stage;
    private Viewport viewport;
    private OrthographicCamera camera;
    private BitmapFont font;
    private Texture star1, star2, star3, pig;
    private Texture nextLevelTexture, repeatLevelTexture;
    private Sound clickSound;
    private Texture backgroundTexture;
    private Music backgroundmusic;

    private int score;
    private int starCount;

    public ResultScreen3(Main game, int score) {
        this.game = game;
        this.score = score;
        this.starCount = calculateStars(score);
        batch = game.getBatch();
        camera = new OrthographicCamera();
        viewport = new FitViewport(1080, 1920, camera);
        stage = new Stage(viewport, game.getBatch());
        Gdx.input.setInputProcessor(stage);
        loadResources();
        setupUI();
    }

    private void loadResources() {
        star1 = new Texture(Gdx.files.internal("Star1.png"));
        star2 = new Texture(Gdx.files.internal("Star2.png"));
        star3 = new Texture(Gdx.files.internal("Star.png"));
        pig = new Texture(Gdx.files.internal("PIG.png"));
        nextLevelTexture = new Texture(Gdx.files.internal("NEXT_LEVEL.png"));
        repeatLevelTexture = new Texture(Gdx.files.internal("REPEAT_LEVEL.png"));
        font = loadCustomFont(48);
    }

    private BitmapFont loadCustomFont(int fontSize) {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("angrybirds-regular.ttf"));
        FreeTypeFontParameter parameter = new FreeTypeFontParameter();
        parameter.size = fontSize;
        parameter.color = com.badlogic.gdx.graphics.Color.WHITE;
        BitmapFont font = generator.generateFont(parameter);
        generator.dispose();
        return font;
    }

    private void setupUI() {
        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);
        Label.LabelStyle labelStyle = new Label.LabelStyle(font, com.badlogic.gdx.graphics.Color.WHITE);
        Label statusLabel = new Label(starCount > 0 ? "LEVEL CLEARED!" : "LEVEL FAILED!", labelStyle);
        statusLabel.setAlignment(Align.center);
        Label scoreLabel = new Label("Score: " + score, labelStyle);
        scoreLabel.setAlignment(Align.center);

        table.add(statusLabel).padTop(50).row();
        if (starCount > 0) {
            table.add(createStars()).padTop(20).row();
        } else {
            table.add(createPig()).size(200, 225).padTop(20).row();
        }
        table.add(scoreLabel).padTop(20).row();

        ImageButton nextButton = new ImageButton(
            new TextureRegionDrawable(new TextureRegion(nextLevelTexture))
        );
        nextButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("next button clicked!");
                clickSound.play();

                game.setScreen(new LevelSelectionScreen(game,backgroundmusic));
            }
        });
        ImageButton repeatButton = new ImageButton(
            new TextureRegionDrawable(new TextureRegion(repeatLevelTexture))
        );
        repeatButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("restart button clicked!");
                clickSound.play();

                game.setScreen(new Level3(game));
            }
        });
        Table buttonTable = new Table();
        buttonTable.add(repeatButton).padRight(20);
        buttonTable.add(nextButton).padLeft(20);
        table.add(buttonTable).padTop(30).row();
    }

    private Stack createStars() {
        Stack starStack = new Stack();
        switch (starCount) {
            case 1:
                starStack.add(new Image(star1));
                break;
            case 2:
                starStack.add(new Image(star2));
                break;
            case 3:
                starStack.add(new Image(star3));
                break;
        }
        return starStack;
    }

    private Image createPig() {
        Image pigImage = new Image(pig);
        pigImage.setSize(50, 50);
        pigImage.setAlign(Align.center);
        return pigImage;
    }

    public static int calculateStars(int score) {
        if (score >= 2500) return 3;
        if (score >= 2000) return 2;
        if (score >= 1000) return 1;
        return 0;
    }

    @Override
    public void show() {
        clickSound = Gdx.audio.newSound(Gdx.files.internal("click.mp3"));
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        viewport.apply();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.setColor(0, 0, 0, 1);
        batch.draw(new Texture(Gdx.files.internal("BG4.jpg")), 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());
        batch.end();
        stage.act(delta);
        stage.draw();
    }


    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
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
        stage.dispose();
        star1.dispose();
        star2.dispose();
        star3.dispose();
        pig.dispose();
        nextLevelTexture.dispose();
        repeatLevelTexture.dispose();
        font.dispose();
    }
}

