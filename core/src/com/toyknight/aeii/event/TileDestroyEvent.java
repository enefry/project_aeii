package com.toyknight.aeii.event;

import com.toyknight.aeii.AnimationDispatcher;
import com.toyknight.aeii.animator.DustAriseAnimator;
import com.toyknight.aeii.entity.GameCore;
import com.toyknight.aeii.entity.Point;
import com.toyknight.aeii.entity.Tile;

import java.io.Serializable;

/**
 * Created by toyknight on 5/17/2015.
 */
public class TileDestroyEvent implements GameEvent, Serializable {

    private static final long serialVersionUID = 05172015L;

    private final int target_x;
    private final int target_y;

    public TileDestroyEvent(int target_x, int target_y) {
        this.target_x = target_x;
        this.target_y = target_y;
    }

    @Override
    public Point getFocus() {
        return new Point(target_x, target_y);
    }

    @Override
    public boolean canExecute(GameCore game) {
        return game.getMap().getTile(target_x, target_y).isDestroyable();
    }

    @Override
    public void execute(GameCore game, AnimationDispatcher animation_dispatcher) {
        Tile tile = game.getMap().getTile(target_x, target_y);
        game.setTile(tile.getDestroyedTileIndex(), target_x, target_y);
        animation_dispatcher.submitAnimation(new DustAriseAnimator(target_x, target_y));
    }

}
