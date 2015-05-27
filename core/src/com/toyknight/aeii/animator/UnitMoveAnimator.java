package com.toyknight.aeii.animator;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.toyknight.aeii.entity.Point;
import com.toyknight.aeii.entity.Unit;
import com.toyknight.aeii.renderer.UnitRenderer;
import com.toyknight.aeii.screen.GameScreen;

import java.util.ArrayList;

/**
 * Created by toyknight on 4/21/2015.
 */
public class UnitMoveAnimator extends UnitAnimator {

    private static final String MOVER_KEY = "mover";

    private final ArrayList<Point> path;

    private int current_location;
    private float x_offset;
    private float y_offset;

    public UnitMoveAnimator(Unit unit, ArrayList<Point> path) {
        this.addUnit(unit, MOVER_KEY);
        this.path = path;

        current_location = 0;
        x_offset = 0f;
        y_offset = 0f;
    }

    @Override
    public void render(SpriteBatch batch, GameScreen screen) {
        if (path.size() > 0) {
            Point current = path.get(current_location);
            screen.getUnitRenderer().drawUnitWithInformation(batch, getUnit(MOVER_KEY), current.x, current.y, x_offset, y_offset);
//            int sx = (int) (screen.getXOnScreen(current.x) + x_offset);
//            int sy = (int) (screen.getYOnScreen(current.y) + y_offset);
//            validateViewport(screen, sx, sy);
        }
    }

    @Override
    public void update(float delta) {
        if (current_location < path.size() - 1) {
            float offset_delta = ts / (4 * (1f / delta) / 30f);
            Point current = path.get(current_location);
            Point next = path.get(current_location + 1);
            if (current.x > next.x) {
                x_offset -= offset_delta;
            }
            if (current.x < next.x) {
                x_offset += offset_delta;
            }
            if (current.y > next.y) {
                y_offset += offset_delta;
            }
            if (current.y < next.y) {
                y_offset -= offset_delta;
            }
            if (Math.abs(x_offset) >= ts || Math.abs(y_offset) >= ts) {
                x_offset = 0;
                y_offset = 0;
                current_location++;
            }
        }
    }

    private void validateViewport(GameScreen screen, int sx, int sy) {

    }

    @Override
    public boolean isAnimationFinished() {
        return current_location >= path.size() - 1;
    }

}
