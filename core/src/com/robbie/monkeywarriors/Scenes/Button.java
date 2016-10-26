package com.robbie.monkeywarriors.Scenes;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.robbie.monkeywarriors.Tools.ActionHandler;

/**
 * Created by robbie on 2016/10/25.
 */
public class Button {

    private Sprite img;
    private TextureRegion regular;
    private TextureRegion hover;
    private String name;
    private ActionHandler actionHandler;

    public Button(TextureRegion regular, TextureRegion hover, float x, float y, float width, float height) {
        this.regular = regular;
        this.hover = hover;
        img = new Sprite(regular);
        img.setBounds(x, y, width, height);
    }

    public void update (SpriteBatch batch, float input_x, float input_y, boolean clicked) {
        checkAction(input_x, input_y, clicked);
        img.draw(batch); // draw the button
    }

    /**
     * Sees if the mouse is currently hovering over the button
     * or clicking it, and responds accordingly
     * @param ix
     * @param iy
     * @param clicked
     */
    private void checkAction (float ix, float iy, boolean clicked) {
        if ((ix > img.getX() && ix < img.getX() + img.getWidth())
            && (iy > img.getY() && iy < img.getY() + img.getHeight())) {
            // the button was clicked, perform an action
            if (clicked) actionHandler.handleClick();
            else img.setRegion(hover);
        }
        else {
            img.setRegion(regular);
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setActionHandler(ActionHandler handler) {
        this.actionHandler = handler;
    }

}
