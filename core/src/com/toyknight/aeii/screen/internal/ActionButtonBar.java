package com.toyknight.aeii.screen.internal;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.SnapshotArray;
import com.toyknight.aeii.entity.GameCore;
import com.toyknight.aeii.entity.player.LocalPlayer;
import com.toyknight.aeii.manager.GameManager;
import com.toyknight.aeii.manager.LocalGameManager;
import com.toyknight.aeii.ResourceManager;
import com.toyknight.aeii.entity.Ability;
import com.toyknight.aeii.entity.Unit;
import com.toyknight.aeii.screen.GameScreen;
import com.toyknight.aeii.screen.widgets.CircleButton;
import com.toyknight.aeii.utils.Platform;

/**
 * Created by toyknight on 4/26/2015.
 */
public class ActionButtonBar extends HorizontalGroup {

    private final int ts;
    private final GameScreen screen;
    private final int PADDING_LEFT;
    private final int BUTTON_WIDTH;
    private final int BUTTON_HEIGHT;
    private final ShapeRenderer shape_renderer;

    private CircleButton btn_buy;
    private CircleButton btn_standby;
    private CircleButton btn_attack;
    private CircleButton btn_move;
    private CircleButton btn_occupy;
    private CircleButton btn_repair;
    private CircleButton btn_summon;
    private CircleButton btn_heal;
    //private ImageButton btn_info;

    public ActionButtonBar(GameScreen screen) {
        this.screen = screen;
        this.ts = screen.getContext().getTileSize();
        this.PADDING_LEFT = screen.getContext().getTileSize() / 4;
        this.BUTTON_WIDTH = getPlatform() == Platform.Desktop ? ts / 24 * 20 : ts / 24 * 40;
        this.BUTTON_HEIGHT = getPlatform() == Platform.Desktop ? ts / 24 * 21 : ts / 24 * 42;
        this.shape_renderer = new ShapeRenderer();
        this.shape_renderer.setAutoShapeType(true);
        initComponents();
    }

    private void initComponents() {

        btn_buy = new CircleButton(CircleButton.SMALL, ResourceManager.getActionIcon(0), ts);
        btn_buy.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                getGameManger().setState(GameManager.STATE_SELECT);
                screen.showUnitStore();
            }
        });
        btn_occupy = new CircleButton(CircleButton.SMALL, ResourceManager.getActionIcon(1), ts);
        btn_occupy.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                getGameManger().doOccupy();
                screen.onButtonUpdateRequested();
            }
        });
        btn_repair = new CircleButton(CircleButton.SMALL, ResourceManager.getActionIcon(1), ts);
        btn_repair.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                getGameManger().doRepair();
                screen.onButtonUpdateRequested();
            }
        });
        btn_attack = new CircleButton(CircleButton.SMALL, ResourceManager.getActionIcon(2), ts);
        btn_attack.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                getGameManger().beginAttackPhase();
                screen.onButtonUpdateRequested();
            }
        });
        btn_summon = new CircleButton(CircleButton.SMALL, ResourceManager.getActionIcon(3), ts);
        btn_summon.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                getGameManger().beginSummonPhase();
                screen.onButtonUpdateRequested();
            }
        });
        btn_move = new CircleButton(CircleButton.SMALL, ResourceManager.getActionIcon(4), ts);
        btn_move.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                getGameManger().beginMovePhase();
                screen.onButtonUpdateRequested();
            }
        });
        btn_standby = new CircleButton(CircleButton.SMALL, ResourceManager.getActionIcon(5), ts);
        btn_standby.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                getGameManger().standbySelectedUnit();
                screen.onButtonUpdateRequested();
            }
        });
        btn_heal = new CircleButton(CircleButton.SMALL, ResourceManager.getActionIcon(7), ts);
    }

    public GameManager getGameManger() {
        return screen.getGameManager();
    }

    private Platform getPlatform() {
        return screen.getContext().getPlatform();
    }

    private GameCore getGame() {
        return getGameManger().getGame();
    }

    public void updateButtons() {
        this.clear();
        if (getGame().getCurrentPlayer() instanceof LocalPlayer && !getGameManger().isAnimating()) {
            Unit selected_unit = getGameManger().getSelectedUnit();
            switch (getGameManger().getState()) {
                case GameManager.STATE_ACTION:
                    getGameManger().createAttackablePositions(selected_unit);
                    if (getGameManger().canSelectUnitAct()) {
                        if (getGameManger().hasEnemyWithinRange(selected_unit)) {
                            addActor(btn_attack);
                        }
                        if (selected_unit.hasAbility(Ability.NECROMANCER)
                                && getGameManger().hasTombWithinRange(selected_unit)) {
                            addActor(btn_summon);
                        }
                        if (selected_unit.hasAbility(Ability.HEALER)
                                && getGameManger().hasAllyWithinRange(selected_unit)) {
                            addActor(btn_heal);
                        }
                        if (getGameManger().getGame().canOccupy(selected_unit, selected_unit.getX(), selected_unit.getY())) {
                            addActor(btn_occupy);
                        }
                        if (getGameManger().getGame().canRepair(selected_unit, selected_unit.getX(), selected_unit.getY())) {
                            addActor(btn_repair);
                        }
                    }
                    addActor(btn_standby);
                    break;
                case GameManager.STATE_BUY:
                    getGameManger().createAttackablePositions(selected_unit);
                    if (selected_unit.isCommander() && selected_unit.getTeam() == getGame().getCurrentTeam()
                            && getGame().isCastleAccessible(getGame().getMap().getTile(selected_unit.getX(), selected_unit.getY()))) {
                        addActor(btn_buy);
                        addActor(btn_move);
                        if (getGameManger().hasEnemyWithinRange(selected_unit)) {
                            addActor(btn_attack);
                        }
                    }
                    break;
                default:
                    //do nothing
            }
            this.layout();
        }
    }

    @Override
    public void layout() {
        SnapshotArray<Actor> children = getChildren();
        for (int i = 0; i < children.size; i++) {
            children.get(i).setBounds(
                    PADDING_LEFT + i * (BUTTON_WIDTH + PADDING_LEFT), 0, BUTTON_WIDTH, BUTTON_HEIGHT);
        }
    }

    @Override
    public void draw(Batch batch, float parent_alpha) {
        super.draw(batch, parent_alpha);
    }

}
