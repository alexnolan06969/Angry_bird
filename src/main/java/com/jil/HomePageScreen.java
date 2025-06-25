package com.jil;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

public class HomePageScreen implements Screen{
    private Stage stage;
    private Texture backgroundTexture;
    private Texture startButtonTexture;
    private Texture settingsButtonTexture;
    private Texture logoTexture;
    private final Game game;
    private Music backgroundMusic;
    private Sound clickSound;


    public HomePageScreen(Game game) {
        this.game = game;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        backgroundTexture = new Texture(Gdx.files.internal("background_agb.jpg"));
        startButtonTexture = new Texture(Gdx.files.internal("BUTTONS_SHEET_1.png"));
        settingsButtonTexture = new Texture(Gdx.files.internal("womp2.png"));
        logoTexture = new Texture(Gdx.files.internal("newnewlogo.png"));

        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("title_theme.mp3"));
        clickSound = Gdx.audio.newSound(Gdx.files.internal("click.mp3"));

        backgroundMusic.setLooping(true);
        backgroundMusic.play();

        Image background = new Image(new TextureRegionDrawable(backgroundTexture));
        background.setFillParent(true);

        Image logo = new Image(new TextureRegionDrawable(logoTexture));
        logo.setSize(500,150);
        logo.setPosition(710 , 600);

        ImageButton startButton = new ImageButton(new TextureRegionDrawable(startButtonTexture));
        startButton.setPosition(960 - startButtonTexture.getWidth()/2, 400 - startButtonTexture.getHeight()/2);
        startButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Start button clicked!");
                clickSound.play();
                backgroundMusic.stop();
                game.setScreen(new SelectEpisodeScreen(((Main)game).getBatch() , (Main)game));
            }
        });

        ImageButton settingsButton = new ImageButton(new TextureRegionDrawable(settingsButtonTexture));
        settingsButton.setPosition(0, 0);
        settingsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Quit button clicked!");
                Gdx.app.exit();
            }
        });

        stage.addActor(background);
        stage.addActor(startButton);
        stage.addActor(settingsButton);
        stage.addActor(logo);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        backgroundTexture.dispose();
        startButtonTexture.dispose();
        settingsButtonTexture.dispose();
        logoTexture.dispose();
        stage.dispose();
    }
}
