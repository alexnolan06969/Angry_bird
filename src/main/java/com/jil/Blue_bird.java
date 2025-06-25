package com.jil;

import com.badlogic.gdx.graphics.Texture;

public class Blue_bird extends Bird{
    private static final Texture blue_bird_texture = new Texture("bluebird.png");

    Blue_bird(float x, float y, float width , float height) {
        super(blue_bird_texture, x, y, width, height);
    }
}
