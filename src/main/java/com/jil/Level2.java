package com.jil;


import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.*;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.utils.viewport.*;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class Level2 implements Screen {
    private Game game;
    private Stage stage;
    private OrthographicCamera camera;
    private Viewport viewport;

    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;

    private Texture backgroundTexture, slingshotTexture, birdTexture, birdTexture1;
    private Image slingshotImage;

    public World getWorld() {
        return world;
    }

    private World world;
    private Box2DDebugRenderer debugRenderer;
    private static final float PPM = 100f;

    private Array<Sprite> birdSprites;
    private Array<Body> birdBodies;
    private int currentBirdIndex = 0;

    public Array<Body> getPigBodies() {
        return pigBodies;
    }

    public Array<Image> getPigImages() {
        return pigImages;
    }

    public Array<Body> getBlockBodies() {
        return blockBodies;
    }

    public Array<Image> getBlockImages() {
        return blockImages;
    }

    private Array<Body> pigBodies;
    private Array<Image> pigImages;
    private Array<Body> blockBodies;
    private Array<Image> blockImages;

    private Vector2 anchorPoint;
    private boolean isDragging = false;

    private Texture restarttexture;
    private Texture MenuTexture, next_texture;

    private Music backgroundMusic;

    private ImageButton restart,menu;

    //t ke liye
    private Array<Vector2> trajectoryPoints;
    private int trajectoryPointCount = 50;
    private float timeStep = 1/60f;
    private Texture trajectoryDotTexture;
    private Array<Sprite> trajectorySprites;

    public Level2(Game game) {
        this.game = game;
        create();
    }

    public int score = 0;

    public int RedbirdDamage = 200;
    public int BigredbirdDamage = 400;
    public int BluebirdDamage = 100;
    public int BlockDamage = 100;
    public int GroundDamage = 100;

    public Base_pig p1 = new Base_pig();
    public M_Pig p2 = new M_Pig();
    public Crown_Pig p3 = new Crown_Pig();
    public M_Pig p4 = new M_Pig();

    //1 --> base
    //2 --> uncle
    //3 --> crown

    public static class GameState {
        public ArrayList<BodyState> birdStates;
        public ArrayList<BodyState> pigStates;
        public ArrayList<BodyState> blockStates;
        public int score;
        public int currentBirdIndex;
        public Vector2 anchorPoint;
        public boolean isDragging;
        public int p1_health, p2_health, p3_health,p4_health;
    }

    public static class BodyState {
        public String userData;
        public Vector2 position;
        public float angle;
        public Vector2 linearVelocity;
        public float angularVelocity;
    }

    public GameState getCurrentState() {
        GameState state = new GameState();
        state.score = this.score;
        state.currentBirdIndex = this.currentBirdIndex;
        state.anchorPoint = this.anchorPoint.cpy();
        state.isDragging = this.isDragging;

        state.birdStates = new ArrayList<>();
        for (Body birdBody : birdBodies) {
            BodyState bodyState = new BodyState();
            bodyState.userData = (String) birdBody.getUserData();
            bodyState.position = birdBody.getPosition().cpy();
            bodyState.angle = birdBody.getAngle();
            bodyState.linearVelocity = birdBody.getLinearVelocity().cpy();
            bodyState.angularVelocity = birdBody.getAngularVelocity();
            state.birdStates.add(bodyState);
        }

        state.pigStates = new ArrayList<>();
        for (Body pigBody : pigBodies) {
            BodyState bodyState = new BodyState();
            bodyState.userData = (String) pigBody.getUserData();
            bodyState.position = pigBody.getPosition().cpy();
            bodyState.angle = pigBody.getAngle();
            bodyState.linearVelocity = pigBody.getLinearVelocity().cpy();
            bodyState.angularVelocity = pigBody.getAngularVelocity();
            state.pigStates.add(bodyState);
        }

        state.blockStates = new ArrayList<>();
        for (Body blockBody : blockBodies) {
            BodyState bodyState = new BodyState();
            bodyState.userData = (String) blockBody.getUserData();
            bodyState.position = blockBody.getPosition().cpy();
            bodyState.angle = blockBody.getAngle();
            bodyState.linearVelocity = blockBody.getLinearVelocity().cpy();
            bodyState.angularVelocity = blockBody.getAngularVelocity();
            state.blockStates.add(bodyState);
        }

        state.p1_health = this.p1.getHealth();
        state.p2_health = this.p2.getHealth();
        state.p3_health = this.p3.getHealth();
        state.p4_health = this.p4.getHealth();

        return state;
    }

    public void deleteSaveGame() {
        FileHandle saveFile = Gdx.files.local("savegame2.json");

        if (saveFile.exists()) {
            boolean deleted = saveFile.delete();
            if (deleted) {
                Gdx.app.log("Delete", "Savegame2.json deleted successfully.");
            } else {
                Gdx.app.log("Delete", "Failed to delete savegame2.json.");
            }
        } else {
            Gdx.app.log("Delete", "No savegame2.json found to delete.");
        }
    }


    public void saveGameState() {
        GameState state = getCurrentState();
        Json json = new Json();
        String jsonString = json.toJson(state);


        Gdx.files.local("savegame2.json").writeString(jsonString, false);

        Gdx.app.log("Save", "Game state saved successfully.");
    }

    public GameState loadGameState() {
        if (!Gdx.files.local("savegame2.json").exists()) {
            Gdx.app.log("Load", "No saved game found.");
            return null;
        }

        Json json = new Json();
        String jsonString = Gdx.files.local("savegame2.json").readString();
        GameState state = json.fromJson(GameState.class, jsonString);

        Gdx.app.log("Load", "Game state loaded successfully.");
        return state;
    }



    public void restoreGameState(GameState state) {
        if (state == null) return;

        this.score = state.score;
        this.currentBirdIndex = state.currentBirdIndex;
        this.anchorPoint = state.anchorPoint.cpy();
        this.isDragging = state.isDragging;

        // Restore birds
        for (int i = 0; i < state.birdStates.size(); i++) {
            BodyState bodyState = state.birdStates.get(i);
            Body birdBody = birdBodies.get(i);
            birdBody.setTransform(bodyState.position, bodyState.angle);
            birdBody.setLinearVelocity(bodyState.linearVelocity);
            birdBody.setAngularVelocity(bodyState.angularVelocity);
            // Restore any other properties as needed
        }

        // Repeat for pigs and blocks
        for (int i = 0; i < state.pigStates.size(); i++) {
            BodyState bodyState = state.pigStates.get(i);
            Body pigBody = pigBodies.get(i);
            pigBody.setTransform(bodyState.position, bodyState.angle);
            pigBody.setLinearVelocity(bodyState.linearVelocity);
            pigBody.setAngularVelocity(bodyState.angularVelocity);
            // Restore any other properties as needed
        }

        for (int i = 0; i < state.blockStates.size(); i++) {
            BodyState bodyState = state.blockStates.get(i);
            Body blockBody = blockBodies.get(i);
            blockBody.setTransform(bodyState.position, bodyState.angle);
            blockBody.setLinearVelocity(bodyState.linearVelocity);
            blockBody.setAngularVelocity(bodyState.angularVelocity);
            // Restore any other properties as needed
        }

        this.p1.setHealth(state.p1_health);
        this.p2.setHealth(state.p2_health);
        this.p3.setHealth(state.p3_health);
        this.p4.setHealth(state.p4_health);

        // Update sprite positions based on restored body positions
        updateBirdSprites();
        updateActors();

        Gdx.app.log("Restore", "Game state restored successfully.");
    }

    private Texture yellow_bird_texture;
    private Texture White_bird_texture;

    public Texture ability;

    public void create() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();


        camera = new OrthographicCamera();
        viewport = new StretchViewport(Gdx.graphics.getWidth() / PPM, Gdx.graphics.getHeight() / PPM, camera);
//        camera.position.set(viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 2, 0);
//        camera.update();

        camera.position.set(viewport.getWorldWidth() / 2 + 7f, viewport.getWorldHeight() / 2, 0);
        camera.update();


        world = new World(new Vector2(0, -9.81f), true);
        debugRenderer = new Box2DDebugRenderer();

        backgroundTexture = new Texture("BG1.jpeg");
        slingshotTexture = new Texture("slingshot.png");
        birdTexture = new Texture("redbird.png");
        birdTexture1 = new Texture(("bluebird.png"));
        yellow_bird_texture = new Texture("yellowbird.png");
        White_bird_texture = new Texture("WhiteBird.png");
        trajectoryDotTexture = new Texture("TrajectoryDot.png");
        trajectorySprites = new Array<>();

        restarttexture = new Texture("REPEAT_LEVEL.png");
        MenuTexture = new Texture("menu.png");
        next_texture = new Texture("next.png");
        ability = new Texture("AbilityButton.png");

//        ImageButton next = new ImageButton(new TextureRegionDrawable(new TextureRegion(next_texture)));
//        next.setSize(1f, 1f);
//        next.setPosition(17,9);
//        next.addListener(new ClickListener() {
//            @Override
//            public void clicked(InputEvent event, float x, float y) {
//                System.out.println("restart button clicked!");
//                backgroundMusic.stop();
//
//                game.setScreen(new GameScreen(game));
//            }
//        });

        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("gameplay_m.mp3"));

        backgroundMusic.setLooping(true);
        backgroundMusic.play();

        ImageButton abilityb = new ImageButton(new TextureRegionDrawable(new TextureRegion(ability)));
        abilityb.setSize(1f, 1f);
        abilityb.setPosition(5,9);

        ImageButton restart = new ImageButton(new TextureRegionDrawable(new TextureRegion(restarttexture)));
        restart.setSize(1f, 1f);
        restart.setPosition(3,9);
        restart.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("restart button clicked!");
                backgroundMusic.stop();
                deleteSaveGame();

                game.setScreen(new Level2(game));
            }
        });

        ImageButton menu = new ImageButton(new TextureRegionDrawable(new TextureRegion(MenuTexture)));
        menu.setSize(1f, 1f);
        menu.setPosition(1,9);
        menu.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Menu button clicked!");
                backgroundMusic.stop();
                saveGameState();

                game.setScreen(new Pause_Screen(game));
            }
        });


        stage = new Stage(viewport, batch);
        Gdx.input.setInputProcessor(stage);

//        stage.addActor(next);
        stage.addActor(restart);
        stage.addActor(menu);
        stage.addActor(abilityb);


        slingshotImage = new Image(slingshotTexture);
        slingshotImage.setSize(1f, 2f); // In meters
        slingshotImage.setPosition(viewport.getWorldWidth() / 6 + 1, 2.2f);
        stage.addActor(slingshotImage);

        anchorPoint = new Vector2(
            slingshotImage.getX() + slingshotImage.getWidth() * 0.5f,
            slingshotImage.getY() + slingshotImage.getHeight() * 1f
        );


        birdSprites = new Array<>();
        birdBodies = new Array<>();
        createBirds();


        createGround();


        pigBodies = new Array<>();
        pigImages = new Array<>();
        blockBodies = new Array<>();
        blockImages = new Array<>();


        createPigs();
        createBlocks();



        for (Image pigImage : pigImages) {
            stage.addActor(pigImage);
        }
        for (Image blockImage : blockImages) {
            stage.addActor(blockImage);
        }



//        p1 = new Base_pig();
//        p2 = new M_Pig();
//        p3 = new Crown_Pig();

        // 0 --> upig and block
        // 1 --> cpig and block
        // 2 --> bpig and block
        // 3 --> upig and ground
        // 4 --> cpig and ground
        // 5 --> bpig and ground
        // 6 --> upig and bird
        // 7 --> cpig and bird
        // 8 --> bpig and bird

        ArrayList<Integer> bird1 = new ArrayList<>();
        ArrayList<Integer> bird2 = new ArrayList<>();
        ArrayList<Integer> bird3 = new ArrayList<>();
        ArrayList<Integer> bird4 = new ArrayList<>();
        ArrayList<Integer> bird5= new ArrayList<>();
        ArrayList<Integer> bird6 = new ArrayList<>();
        ArrayList<Integer> bird7 = new ArrayList<>();

        for (int i = 0; i < 12; i++) {
            bird1.add(0);
            bird2.add(0);
            bird3.add(0);
            bird4.add(0);
            bird5.add(0);
            bird6.add(0);
            bird7.add(0);
        }

        world.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
                Fixture fixtureA = contact.getFixtureA();
                Fixture fixtureB = contact.getFixtureB();

                Body bodyA = fixtureA.getBody();
                Body bodyB = fixtureB.getBody();

                Object userDataA = bodyA.getUserData();
                Object userDataB = bodyB.getUserData();

                ArrayList<Integer> op;
                if(currentBirdIndex == 0) op = bird1;
                else if(currentBirdIndex == 1) op = bird2;
                else if(currentBirdIndex == 2) op = bird3;
                else if(currentBirdIndex == 3) op = bird4;
                else if(currentBirdIndex == 4) op = bird5;
                else if(currentBirdIndex == 5) op = bird6;
                else op = bird7;

                // 0 --> upig and block
                // 1 --> cpig and block
                // 2 --> bpig and block
                // 3 --> mpig and block
                // 4 --> upig and ground
                // 5 --> cpig and ground
                // 6 --> bpig and ground
                // 7 --> mpig and ground
                // 8 --> upig and bird
                // 9 --> cpig and bird
                // 10 --> bpig and bird
                // 11 --> mpig and bird

                //1 --> base
                //2 --> uncle
                //3 --> crown
                // 4 -- > motu

                handleBeginContact(userDataA,userDataB,op);



            }

            @Override
            public void endContact(Contact contact) {

            }

            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {

            }

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {

            }
        });

        GameState loadedState = loadGameState();
        if (loadedState != null) {
            restoreGameState(loadedState);
        }

//        System.out.println("Initial health of BasePig (p1): " + p1.getHealth());
//        System.out.println("Initial health of M_Pig (p2): " + p2.getHealth());
//        System.out.println("Initial health of Crown_Pig (p3): " + p3.getHealth());
    }

    public static void handlecontact(Object userDataA,Object userDataB,int[] score_i){
        if(("Block".equals(userDataA) && "Pig".equals(userDataB))){
            score_i[0] += 100;
            score_i[1] -= 100;
        }
    }

    public void handleBeginContact(Object userDataA,Object userDataB,ArrayList<Integer> op) {

        if (("Block".equals(userDataA) && "UnclePig".equals(userDataB)) ||
            ("UnclePig".equals(userDataA) && "Block".equals(userDataB))) {
//                    System.out.println("UnclePig collided with Block!");
            if(op.get(0) < 1){
                p1.setHealth(p1.getHealth() - BlockDamage);
                op.set(0,1);
                System.out.println("u pig health --> " + p1.getHealth());
                score += BlockDamage;
                System.out.println(score);
                System.out.println(currentBirdIndex);
            }
        }
        else if (("Block".equals(userDataA) && "CrownPig".equals(userDataB)) ||
            ("CrownPig".equals(userDataA) && "Block".equals(userDataB))) {
//                    System.out.println("CrownPig collided with Block!");
            if(op.get(1) < 1){
                p3.setHealth(p3.getHealth() - BlockDamage);
                op.set(1,1);
                score += BlockDamage;
                System.out.println("cpig health --> " + p3.getHealth());
                System.out.println(score);
                System.out.println(currentBirdIndex);
            }
        }
        else if (("Block".equals(userDataA) && "MPig".equals(userDataB)) ||
            ("MPig".equals(userDataA) && "Block".equals(userDataB))) {
//                    System.out.println("CrownPig collided with Block!");
            if(op.get(3) < 1){
                p4.setHealth(p4.getHealth() - BlockDamage);
                op.set(3,1);
                score += BlockDamage;
                System.out.println("mpig health --> " + p4.getHealth());
                System.out.println(score);
                System.out.println(currentBirdIndex);
            }
        }
        else if (("BlueBird".equals(userDataA) && "MPig".equals(userDataB)) ||
            ("MPig".equals(userDataA) && "BlueBird".equals(userDataB))) {
//                    System.out.println("CrownPig collided with Block!");
            if(op.get(11) < 1){
                p4.setHealth(p4.getHealth() - BluebirdDamage);
                op.set(11,1);
                score += BluebirdDamage;
                System.out.println("mpig health --> " + p4.getHealth());
                System.out.println(score);
                System.out.println(currentBirdIndex);
            }
        }
        else if (("RedBird".equals(userDataA) && "MPig".equals(userDataB)) ||
            ("MPig".equals(userDataA) && "RedBird".equals(userDataB))) {
//                    System.out.println("CrownPig collided with Block!");
            if(op.get(11) < 1){
                p4.setHealth(p4.getHealth() - RedbirdDamage);
                op.set(11,1);
                score += RedbirdDamage;
                System.out.println("mpig health --> " + p4.getHealth());
                System.out.println(score);
                System.out.println(currentBirdIndex);
            }
        }
        else if (("MotuBird".equals(userDataA) && "MPig".equals(userDataB)) ||
            ("MPig".equals(userDataA) && "MotuBird".equals(userDataB))) {
//                    System.out.println("CrownPig collided with Block!");
            if(op.get(11) < 1){
                p4.setHealth(p4.getHealth() - BigredbirdDamage);
                op.set(11,1);
                score += BigredbirdDamage;
                System.out.println("mpig health --> " + p4.getHealth());
                System.out.println(score);
                System.out.println(currentBirdIndex);
            }
        }
        else if (("Ground".equals(userDataA) && "MPig".equals(userDataB)) ||
            ("MPig".equals(userDataA) && "Ground".equals(userDataB))) {
//                    System.out.println("CrownPig collided with Block!");
            if(op.get(7) < 1){
                p4.setHealth(p4.getHealth() - GroundDamage);
                op.set(7,1);
                score += GroundDamage;
                System.out.println("mpig health --> " + p4.getHealth());
                System.out.println(score);
                System.out.println(currentBirdIndex);
            }
        }
        else if (("Block".equals(userDataA) && "BasePig".equals(userDataB)) ||
            ("BasePig".equals(userDataA) && "Block".equals(userDataB))) {
//                    System.out.println("BasePig collided with Block!");
            if(op.get(2) < 1){
                p2.setHealth(p2.getHealth() - BlockDamage);
                op.set(2,1);
                score += BlockDamage;
                System.out.println(p2.getHealth());
                System.out.println(score);
                System.out.println(currentBirdIndex);
            }
        }
        else if (("BasePig".equals(userDataA) && "Ground".equals(userDataB)) ||
            ("Ground".equals(userDataA) && "BasePig".equals(userDataB))) {
//                    System.out.println("BlueBird collided with Ground!");

            if(op.get(5) < 1){
                p1.setHealth(p1.getHealth() - GroundDamage);
                op.set(5,1);
                score += GroundDamage;
                System.out.println(score);
                System.out.println(currentBirdIndex);
            }
        }
        else if (("CrownPig".equals(userDataA) && "Ground".equals(userDataB)) ||
            ("Ground".equals(userDataA) && "CrownPig".equals(userDataB))) {
//                    System.out.println("RedBird collided with Ground!");
            if(op.get(4) < 1){
                p3.setHealth(p3.getHealth() - GroundDamage);
                op.set(4,1);
                score += GroundDamage;
                System.out.println(score);
                System.out.println(currentBirdIndex);
            }
        }
        else if (("UnclePig".equals(userDataA) && "Ground".equals(userDataB)) ||
            ("Ground".equals(userDataA) && "UnclePig".equals(userDataB))) {
//                    System.out.println("MotuBird collided with Ground!");
            if(op.get(3) < 1){
                p2.setHealth(p2.getHealth() - GroundDamage);
                op.set(3,1);
                score += GroundDamage;
                System.out.println(score);
                System.out.println(currentBirdIndex);
            }
        }
        else if (("BlueBird".equals(userDataA) && "UnclePig".equals(userDataB)) ||
            ("UnclePig".equals(userDataA) && "BlueBird".equals(userDataB))) {
//                    System.out.println("BlueBird collided with UnclePig!");
            //6
            if(op.get(6) < 1){
                p2.setHealth(p2.getHealth() - BluebirdDamage);
                op.set(6,1);
                score += BluebirdDamage;
                System.out.println(score);
                System.out.println(currentBirdIndex);
            }
        }
        else if (("BlueBird".equals(userDataA) && "CrownPig".equals(userDataB)) ||
            ("CrownPig".equals(userDataA) && "BlueBird".equals(userDataB))) {
//                    System.out.println("BlueBird collided with CrownPig!");
            //7
            if(op.get(7) < 1){
                p3.setHealth(p3.getHealth() - BluebirdDamage);
                op.set(7,1);
                score += BluebirdDamage;
                System.out.println(score);
                System.out.println(currentBirdIndex);
            }
        }
        else if (("BlueBird".equals(userDataA) && "BasePig".equals(userDataB)) ||
            ("BasePig".equals(userDataA) && "BlueBird".equals(userDataB))) {
//                    System.out.println("BlueBird collided with BasePig!");
            //8
            if(op.get(8) < 1){
                p1.setHealth(p1.getHealth() - BluebirdDamage);
                op.set(8,1);
                score += BluebirdDamage;
                System.out.println(score);
                System.out.println(currentBirdIndex);
            }
        }
        else if (("RedBird".equals(userDataA) && "UnclePig".equals(userDataB)) ||
            ("UnclePig".equals(userDataA) && "RedBird".equals(userDataB))) {
//                    System.out.println("RedBird collided with UnclePig!");
            if(op.get(6) < 1){
                p2.setHealth(p2.getHealth() - RedbirdDamage);
                op.set(6,1);
                score += RedbirdDamage;
                System.out.println(score);
                System.out.println(currentBirdIndex);
            }
        }
        else if (("RedBird".equals(userDataA) && "CrownPig".equals(userDataB)) ||
            ("CrownPig".equals(userDataA) && "RedBird".equals(userDataB))) {
//                    System.out.println("RedBird collided with CrownPig!");
            if(op.get(7) < 1){
                p3.setHealth(p3.getHealth() - RedbirdDamage);
                op.set(7,1);
                score += RedbirdDamage;
                System.out.println(score);
                System.out.println(currentBirdIndex);
            }
        }
        else if (("RedBird".equals(userDataA) && "BasePig".equals(userDataB)) ||
            ("BasePig".equals(userDataA) && "RedBird".equals(userDataB))) {
//                    System.out.println("RedBird collided with BasePig!");
            if(op.get(8) < 1){
                p1.setHealth(p1.getHealth() - RedbirdDamage);
                op.set(8,1);
                score += RedbirdDamage;
                System.out.println(score);
                System.out.println(currentBirdIndex);
            }
        }
        else if (("MotuBird".equals(userDataA) && "UnclePig".equals(userDataB)) ||
            ("UnclePig".equals(userDataA) && "MotuBird".equals(userDataB))) {
//                    System.out.println("MotuBird collided with UnclePig!");
            if(op.get(6) < 1){
                p2.setHealth(p2.getHealth() - BigredbirdDamage);
                op.set(6,1);
                score += BigredbirdDamage;
                System.out.println(score);
                System.out.println(currentBirdIndex);
            }
        }
        else if (("MotuBird".equals(userDataA) && "CrownPig".equals(userDataB)) ||
            ("CrownPig".equals(userDataA) && "MotuBird".equals(userDataB))) {
//                    System.out.println("MotuBird collided with CrownPig!");
            if(op.get(7) < 1){
                p3.setHealth(p3.getHealth() - BigredbirdDamage);
                op.set(7,1);
                score += BigredbirdDamage;
                System.out.println(score);
                System.out.println(currentBirdIndex);
            }
        }
        else if (("MotuBird".equals(userDataA) && "BasePig".equals(userDataB)) ||
            ("BasePig".equals(userDataA) && "MotuBird".equals(userDataB))) {
//                    System.out.println("MotuBird collided with BasePig!");
            if(op.get(8) < 1){
                p1.setHealth(p1.getHealth() - BigredbirdDamage);
                op.set(8,1);
                score += BigredbirdDamage;
                System.out.println(score);
                System.out.println(currentBirdIndex);
            }
        }
    }

    private void calculateTrajectory(Vector2 startPosition, Vector2 initialVelocity, float linearDamping) {
        trajectorySprites.clear();
        Vector2 gravity = world.getGravity().cpy();
        float maxTrajectoryDistance = 13f;
        float timeStep = 1 / 60f * 4;
        float dampingFactor = 1 - linearDamping * timeStep;

        Vector2 position = new Vector2(startPosition);
        Vector2 velocity = new Vector2(initialVelocity);

        for (int i = 0; i < trajectoryPointCount; i++) {

            position.x += velocity.x * timeStep;
            position.y += velocity.y * timeStep;


            velocity.add(gravity.x * timeStep, gravity.y * timeStep);


            velocity.scl(dampingFactor);


            if (position.y < 2f) break;
            if (position.x - startPosition.x > maxTrajectoryDistance) break;


            Sprite sprite = new Sprite(trajectoryDotTexture);
            sprite.setSize(0.175f, 0.175f);
            sprite.setOriginCenter();
            sprite.setPosition(position.x - sprite.getWidth() / 2, position.y - sprite.getHeight() / 2);

            trajectorySprites.add(sprite);
        }
    }

    private void createBirds() {
        for (int i = 0; i < 5; i++) {
            if (i < 2){

                BodyDef bd = new BodyDef();
                bd.type = BodyDef.BodyType.DynamicBody;
                bd.position.set(viewport.getWorldWidth() / 6 - i * 0.7f, 4.3f);

                CircleShape shape = new CircleShape();
                shape.setRadius(0.2f);

                FixtureDef fd = new FixtureDef();
                fd.shape = shape;
                fd.density = 2.2f;
                fd.friction = 0.5f;
                fd.restitution = 0.5f;

                Body birdBody = world.createBody(bd);
                birdBody.setLinearDamping(1f);
                birdBody.setAngularDamping(0.2f);
                birdBody.createFixture(fd);
                shape.dispose();

                birdBodies.add(birdBody);
                birdBody.setUserData("RedBird");


                Sprite birdSprite = new Sprite(birdTexture);
                birdSprite.setSize(0.5f, 0.5f);
                birdSprites.add(birdSprite);
            }else if (i == 3){

                BodyDef bd = new BodyDef();
                bd.type = BodyDef.BodyType.DynamicBody;
                bd.position.set(viewport.getWorldWidth() / 6 - i * 0.7f, 4);

                CircleShape shape = new CircleShape();
                shape.setRadius(0.4f);

                FixtureDef fd = new FixtureDef();
                fd.shape = shape;
                fd.density = .7f;
                fd.friction = 0.5f;
                fd.restitution = 0.5f;

                Body birdBody = world.createBody(bd);
                birdBody.setLinearDamping(.5f);
                birdBody.setAngularDamping(0.1f);
                birdBody.createFixture(fd);
                shape.dispose();

                birdBodies.add(birdBody);
                birdBody.setUserData("MotuBird");


                Sprite birdSprite = new Sprite(birdTexture);
                birdSprite.setSize(1f, 1f);
                birdSprites.add(birdSprite);
            }
            else{

                BodyDef bd = new BodyDef();
                bd.type = BodyDef.BodyType.DynamicBody;
                bd.position.set(viewport.getWorldWidth() / 6 - i * 0.7f, 4);

                CircleShape shape = new CircleShape();
                shape.setRadius(0.2f);

                FixtureDef fd = new FixtureDef();
                fd.shape = shape;
                fd.density = 2.2f;
                fd.friction = 0.5f;
                fd.restitution = 0.5f;

                Body birdBody = world.createBody(bd);
                birdBody.setLinearDamping(1f);
                birdBody.setAngularDamping(0.2f);
                birdBody.createFixture(fd);
                shape.dispose();

                birdBodies.add(birdBody);
                birdBody.setUserData("BlueBird");

                Sprite birdSprite = new Sprite(birdTexture1);
                birdSprite.setSize(0.5f, 0.5f);
                birdSprites.add(birdSprite);
            }
        }
    }

    private void createGround() {
        BodyDef groundBodyDef = new BodyDef();
        groundBodyDef.type = BodyDef.BodyType.StaticBody;
        groundBodyDef.position.set(0, 1);

        PolygonShape groundShape = new PolygonShape();
        groundShape.setAsBox(viewport.getWorldWidth(), 1.25f);

        FixtureDef groundFixtureDef = new FixtureDef();
        groundFixtureDef.shape = groundShape;
        groundFixtureDef.friction = 0.5f;



        Body groundBody = world.createBody(groundBodyDef);
        groundBody.createFixture(groundFixtureDef);
        groundBody.setUserData("Ground");
        groundShape.dispose();

    }

    private void createPigs() {
        Object[][] pigData = {
            {1650f / PPM, (675f - 170) / PPM, 45f / PPM, "base_pig.png"},//1500f / PPM, (545f - 175 )/ PPM
            {1500f / PPM, (545f - 175 )/ PPM, 60f / PPM, "uncle_pig.png"},
            {1650f / PPM, (550f - 175)/ PPM, 55f / PPM, "PIG.png"},
            {1650f / PPM, (550f - 250)/ PPM, 60f / PPM, "uncle_pig.png"},
        };
        int counter = 0;
        for (Object[] data : pigData) {
            float x = (float) data[0];
            float y = (float) data[1];
            float radius = (float) data[2] / 2;
            String texturePath = (String) data[3];

            Image pigImage = new Image(new Texture(texturePath));
            pigImage.setSize(radius * 2, radius * 2);
            pigImage.setOrigin(radius, radius);
            pigImage.setPosition(x - radius, y - radius);


            BodyDef bodyDef = new BodyDef();
            bodyDef.type = BodyDef.BodyType.DynamicBody;
            bodyDef.position.set(x, y);

            CircleShape shape = new CircleShape();
            shape.setRadius(radius - .05f);

            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.shape = shape;
            fixtureDef.density = 3f;
            fixtureDef.friction = 0.5f;
            fixtureDef.restitution = 0.3f;

            Body body = world.createBody(bodyDef);
            body.setLinearDamping(1f);
            body.createFixture(fixtureDef);
            shape.dispose();

            body.setUserData(pigImage);
            body.setGravityScale(1);

            if(counter == 0) body.setUserData("BasePig");
            if(counter == 1) body.setUserData("UnclePig");
            if(counter == 2) body.setUserData("CrownPig");
            if(counter == 3) body.setUserData("MPig");

            pigBodies.add(body);
            pigImages.add(pigImage);
            counter++;
        }
    }

    private void createBlocks() {
        Object[][] blockData = {
            {11.5f + 3f, 2.75f, 0.25f, 1f, 1f, "Toons_Stone_Block.png"},
            {12.0f + 3f, 2.75f, 0.25f, 1f, 1f, "Toons_Stone_Block.png"},
            {12.5f + 3f, 2.75f, 0.25f, 1f, 1f, "Toons_Stone_Block.png"},
            {12.0f + 3f, 3.35f, 1.5f, 0.25f, 0.7f, "Toons_Wood_Block(90).png"},
            {13.0f + 3f, 2.75f, 0.25f, 1f, 1f, "Toons_Stone_Block.png"},
//            {13.5f + 3f, 2.75f, 0.25f, 1f, 1f, "Toons_Stone_Block.png"},
            {14.0f + 3f, 2.75f, 0.25f, 1f, 1f, "Toons_Stone_Block.png"},
            {13.5f + 3f, 3.35f, 1.5f, 0.25f, 0.5f, "Toons_Glass_Block(90).png"},
            {13.0f + 3f, 4f, 0.25f, 1f, .5f, "Toons_Glass_Block.png"},
            {14.0f + 3f, 4f, 0.25f, 1f, .5f, "Toons_Glass_Block.png"},
            {13.5f + 3f, 4.625f, 1.5f, 0.25f, .5f, "Toons_Glass_Block(90).png"},
        };

        for (Object[] data : blockData) {
            float x = (float) data[0];
            float y = (float) data[1];
            float width = (float) data[2];
            float height = (float) data[3];
            float density = (float) data[4];
            String texturePath = (String) data[5];


            Image blockImage = new Image(new Texture(texturePath));
            blockImage.setSize(width+.05f, height+.05f);
            blockImage.setOrigin(width / 2, height / 2);
            blockImage.setPosition(x - width / 2, y - height / 2);


            BodyDef bodyDef = new BodyDef();
            bodyDef.type = BodyDef.BodyType.DynamicBody;
            bodyDef.position.set(x, y);

            PolygonShape shape = new PolygonShape();
            shape.setAsBox(width / 2, height / 2);

            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.shape = shape;
            fixtureDef.density = density;
            fixtureDef.friction = 0.5f;
            fixtureDef.restitution = 0.1f;

            Body body = world.createBody(bodyDef);
            body.createFixture(fixtureDef);
            shape.dispose();

            body.setUserData(blockImage);
            body.setUserData("Block");

            blockBodies.add(body);
            blockImages.add(blockImage);
            body.setGravityScale(1);

            stage.addActor(blockImage);
        }
    }

    @Override
    public void render(float delta) {

        handleInput();

        checkPigHealth();

        if(currentBirdIndex == 5){
            backgroundMusic.stop();
            ResultScreen2 r = new ResultScreen2((Main)game,score);
            game.setScreen(r);
        }

        if(p1.getHealth() == 0 && p2.getHealth() == 0 && p3.getHealth() == 0){
            backgroundMusic.stop();
            ResultScreen2 r = new ResultScreen2((Main)game,score);
            game.setScreen(r);
        }


        world.step(1 / 60f, 6, 2);


        updateBirdSprites();

        updateActors();


        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(backgroundTexture, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());
        for (Sprite birdSprite : birdSprites) {
            birdSprite.draw(batch);
        }
        for (Sprite sprite : trajectorySprites) {
            sprite.draw(batch);
        }
        batch.end();


        stage.act(delta);
        stage.draw();

        if (isDragging) {
            shapeRenderer.setProjectionMatrix(camera.combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(Color.BLACK);

            Body currentBirdBody = birdBodies.get(currentBirdIndex);
            shapeRenderer.line(anchorPoint.x, anchorPoint.y, currentBirdBody.getPosition().x, currentBirdBody.getPosition().y);

            shapeRenderer.end();
        }

//        debugRenderer.render(world, camera.combined);
    }

    private void handleInput() {
        if (currentBirdIndex >= birdBodies.size) return;

        Body currentBirdBody = birdBodies.get(currentBirdIndex);

        if (Gdx.input.isTouched()) {
            Vector3 touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);

            Vector2 touchPoint = new Vector2(touchPos.x, touchPos.y);

            if (!isDragging) {
                isDragging = true;
                currentBirdBody.setLinearVelocity(0, 0);
                currentBirdBody.setAngularVelocity(0);
                trajectorySprites.clear();
            }

            Vector2 direction = touchPoint.cpy().sub(anchorPoint);
            float maxDistance = 2f;
            if (direction.len() > maxDistance) {
                direction.nor().scl(maxDistance);
            }
            Vector2 newPosition = anchorPoint.cpy().add(direction);
            currentBirdBody.setTransform(newPosition, 0);

            Vector2 force = anchorPoint.cpy().sub(currentBirdBody.getPosition()).scl(3f);
            float mass = currentBirdBody.getMass();
            Vector2 initialVelocity = force.cpy().scl(1 / mass);
            calculateTrajectory(currentBirdBody.getPosition(), initialVelocity, currentBirdBody.getLinearDamping());

        } else if (isDragging) {
            isDragging = false;

            Vector2 force = anchorPoint.cpy().sub(currentBirdBody.getPosition()).scl(3f);
            currentBirdBody.setLinearVelocity(0, 0);
            currentBirdBody.applyLinearImpulse(force, currentBirdBody.getWorldCenter(), true);

            trajectorySprites.clear();

            currentBirdIndex++;
        }
    }

    private void checkPigHealth() {
        if (p1.getHealth() <= 0) {
            removePig("BasePig");
        }
        if (p2.getHealth() <= 0) {
            removePig("UnclePig");
        }
        if (p3.getHealth() <= 0) {
            removePig("CrownPig");
        }
        if(p4.getHealth() <=0){
            removePig("MPig");
        }
    }

    private void removePig(String pigName) {
        for (int i = 0; i < pigBodies.size; i++) {
            Body pigBody = pigBodies.get(i);
            String bodyUserData = (String) pigBody.getUserData();

            if (pigName.equals(bodyUserData)) {
                Image pigImage = pigImages.get(i);
                world.destroyBody(pigBody);
                stage.getActors().removeValue(pigImage, true);


                pigBodies.removeIndex(i);
                pigImages.removeIndex(i);

                System.out.println(pigName + " removed from the game!");
                break;
            }
        }
    }

    private void updateBirdSprites() {
        for (int i = birdBodies.size - 1; i >= 0; i--) {
            Body birdBody = birdBodies.get(i);
            Sprite birdSprite = birdSprites.get(i);
            birdSprite.setPosition(
                birdBody.getPosition().x - birdSprite.getWidth() / 2,
                birdBody.getPosition().y - birdSprite.getHeight() / 2
            );
            birdSprite.setOriginCenter();
            birdSprite.setRotation(birdBody.getAngle() * MathUtils.radiansToDegrees);
        }
    }

    private void updateActors() {

        for (int i = 0; i < pigBodies.size; i++) {
            Body body = pigBodies.get(i);
            Image image = pigImages.get(i);

            Vector2 position = body.getPosition();
            float angle = body.getAngle();

            image.setPosition(position.x - image.getWidth() / 2, position.y - image.getHeight() / 2);
            image.setRotation(MathUtils.radiansToDegrees * angle);
        }


        for (int i = 0; i < blockBodies.size; i++) {
            Body body = blockBodies.get(i);
            Image image = blockImages.get(i);

            Vector2 position = body.getPosition();
            float angle = body.getAngle();

            image.setPosition(position.x - image.getWidth() / 2, position.y - image.getHeight() / 2);
            image.setRotation(MathUtils.radiansToDegrees * angle);
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        camera.position.set(viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 2, 0);
        camera.update();
    }

    @Override
    public void dispose() {
        saveGameState();
        batch.dispose();
        shapeRenderer.dispose();
        backgroundTexture.dispose();
        slingshotTexture.dispose();
        birdTexture.dispose();
        world.dispose();
        debugRenderer.dispose();
        stage.dispose();
        trajectoryDotTexture.dispose();


        for (Image img : pigImages) {
            ((TextureRegionDrawable) img.getDrawable()).getRegion().getTexture().dispose();
        }
        for (Image img : blockImages) {
            ((TextureRegionDrawable) img.getDrawable()).getRegion().getTexture().dispose();
        }
    }

    @Override
    public void show() {}
    @Override
    public void hide() {}
    @Override
    public void pause() {
        saveGameState();
    }
    @Override
    public void resume() {}
}
