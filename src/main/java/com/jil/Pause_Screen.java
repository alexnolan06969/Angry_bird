package com.jil;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.Game;

public class Pause_Screen implements Screen {

    Pause_Screen(Game game){
        this.game = game;
    }

    private Texture backgroundtexture,menutexture,lvltexture,backtexture;
    private Stage stage;
    private ImageButton main_menu, level_screen ,back;
    private Game game;

    private Music backgroundMusic;
    private Sound clickSound;


    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        clickSound = Gdx.audio.newSound(Gdx.files.internal("click.mp3"));

        backgroundtexture = new Texture("idk.jpg");

        Image background = new Image(new TextureRegionDrawable(backgroundtexture));
        background.setFillParent(true);


        menutexture = new Texture("womp2.png");
        main_menu = new ImageButton(new TextureRegionDrawable(menutexture));
        main_menu.setPosition(400,740);  // Set the position
        main_menu.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("main menu button clicked!");
                clickSound.play();
                backgroundMusic.stop();
                game.setScreen(new HomePageScreen(game));
            }
        });

        lvltexture = new Texture("womp.png");
        level_screen = new ImageButton(new TextureRegionDrawable(lvltexture));
        level_screen.setPosition(400,540);
        level_screen.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Start button clicked!");
                clickSound.play();
                backgroundMusic.stop();
                game.setScreen(new LevelSelectionScreen((Main)game,backgroundMusic));
            }
        });

        backtexture = new Texture("prev.png");
        back = new ImageButton(new TextureRegionDrawable(backtexture));
        back.setPosition(400,340);
        back.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Start button clicked!");
                clickSound.play();
                backgroundMusic.stop();
                game.setScreen(new GameScreen(game));
            }
        });

        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("pause_sound.mp3"));

        backgroundMusic.setLooping(true);
        backgroundMusic.play();

        stage.addActor(background);
        stage.addActor(main_menu);
        stage.addActor(level_screen);
        stage.addActor(back);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {

    }
}
