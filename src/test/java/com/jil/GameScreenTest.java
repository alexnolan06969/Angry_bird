package com.jil;

import com.badlogic.gdx.physics.box2d.Body;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GameScreenTest {

    @Test
    public void checkCollision() {

        int[] score = new int[2];
        score[0] = 0;

        Body pigBody = mock(Body.class);
        Body blockBody = mock(Body.class);
        when(pigBody.getUserData()).thenReturn("Pig");
        when(blockBody.getUserData()).thenReturn("Block");

        GameScreen.handlecontact(blockBody.getUserData(),pigBody.getUserData(),score);
        int expected_score = 100;

        assertEquals(score[0],expected_score);

    }

    @Test
    public void checkCollision2() {
        int[] score = new int[2];
        score[1] = 500;

        Body pigBody = mock(Body.class);
        Body blockBody = mock(Body.class);
        when(pigBody.getUserData()).thenReturn("Pig");
        when(blockBody.getUserData()).thenReturn("Block");

        GameScreen.handlecontact(blockBody.getUserData(),pigBody.getUserData(),score);
        int expected_pig_health = 400;

        assertEquals(score[1],expected_pig_health);
    }

    @Test
    public void checkstars(){
        int score = 2111;

        int expected_stars = 2;

        assertEquals(ResultScreen.calculateStars(score) , expected_stars);

    }
}
