package com.jil;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.Game;

public class SelectEpisodeScreen implements Screen {

    private final SpriteBatch batch;
    private final Stage stage;
    private final OrthographicCamera camera;
    private final Viewport viewport;
    private Texture backgroundTexture , backTexture;
    private Main game;
    private ImageButton backButton;
    private Texture lockTexture;
    private Sound clickSound;
    private Music backgroundMusic;

    private String[] episodeCards = {
        "POACHED_EGGS.PNG", "MIGHTY_HOAX.PNG", "DANGER_ABOVE.PNG",
        "THE_BIG_SETUP.PNG", "HAM'EM_HIGH.PNG", "MINE_AND_DINE.PNG",
        "BIRDDAY_PARTY.PNG", "BAD_PIGGIES.PNG", "RED'S_MIGHTY_FEATHERS.PNG",
        "SHORT_FUSE.PNG", "FLOCK_FAVORITES.PNG", "SURF_AND_TURF.PNG",
        "BIRD_ISLAND.PNG", "PIGGY_FARM.PNG", "JURRASIC_PORK.PNG",
        "BONUS_LEVELS.PNG", "GOLDEN_EGGS.PNG"
    };

    public SelectEpisodeScreen(SpriteBatch batch, Main game) {
        this.batch = batch;
        camera = new OrthographicCamera();
        viewport = new StretchViewport(1920, 1080, camera);
        stage = new Stage(viewport, batch);
        Gdx.input.setInputProcessor(stage);
        backgroundTexture = new Texture(Gdx.files.internal("EpisodeCardsBackgrounds.png"));
        this.game = game;
        loadResources();
        setupUI();
    }

    private void loadResources() {
        lockTexture = new Texture(Gdx.files.internal("LOCK.png"));
    }

    private void setupUI() {
        Table table = new Table();
        table.left();
        table.setFillParent(true);
        ScrollPane scrollPane = new ScrollPane(table);
        scrollPane.setFillParent(true);
        scrollPane.setScrollingDisabled(false, true);

        for (int i = 0; i < episodeCards.length; i++) {
            boolean unlocked = (i == 0);
            Stack episodeCard = createEpisodeCard(episodeCards[i], unlocked);

            Container<Stack> cardContainer = new Container<>(episodeCard);
            cardContainer.size(250, 720);

            table.add(cardContainer)
                .padLeft(50)
                .padRight(50)
                .width(250)
                .height(720);
        }
        stage.addActor(scrollPane);
    }

    private Stack createEpisodeCard(String cardImagePath, boolean unlocked) {

        Texture cardTexture = new Texture(Gdx.files.internal(cardImagePath));
        Image card = new Image(new TextureRegionDrawable(cardTexture));

        Container<Image> cardContainer = new Container<>(card);
        cardContainer.setTransform(true);
        cardContainer.size(225, 690);

        Stack cardStack = new Stack();
        cardStack.add(cardContainer);

        if (!unlocked) {
            Image lockOverlay = new Image(new TextureRegionDrawable(new TextureRegion(lockTexture)));
            Container<Image> lockContainer = new Container<>(lockOverlay);
            lockContainer.setTransform(true);
            lockContainer.size(130, 150);
            cardStack.add(lockContainer);
        } else {
            cardStack.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    System.out.println("Clicked on episode: " + cardImagePath);
                    clickSound.play();
                    game.setScreen(new LevelSelectionScreen(game,backgroundMusic));
                }

                @Override
                public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                    cardContainer.addAction(Actions.scaleTo(1.05f, 1.05f, 0.1f));
                }

                @Override
                public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                    cardContainer.addAction(Actions.scaleTo(1.0f, 1.0f, 0.1f));
                }
            });
        }

        return cardStack;
    }


    @Override
    public void show() {
        backTexture = new Texture(Gdx.files.internal("BACK.png"));
        backButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(backTexture)));
        backButton.setSize(100, 100);
        backButton.setPosition(50, viewport.getWorldHeight() - backButton.getHeight() - 50);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("back button clicked!");
                backgroundMusic.stop();
                game.setScreen(new HomePageScreen(game));
            }
        });
        clickSound = Gdx.audio.newSound(Gdx.files.internal("click.mp3"));
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("pause_sound.mp3"));

        backgroundMusic.setLooping(true);
        backgroundMusic.play();

        stage.addActor(backButton);
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
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        stage.dispose();
        lockTexture.dispose();
        for (String card : episodeCards) {
            new Texture(Gdx.files.internal(card)).dispose();
        }
    }
}
