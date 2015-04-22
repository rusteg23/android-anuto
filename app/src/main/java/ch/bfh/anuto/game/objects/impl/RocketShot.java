package ch.bfh.anuto.game.objects.impl;

import android.graphics.Canvas;

import ch.bfh.anuto.R;
import ch.bfh.anuto.game.Layers;
import ch.bfh.anuto.game.objects.Sprite;
import ch.bfh.anuto.game.objects.Enemy;
import ch.bfh.anuto.game.objects.GameObject;
import ch.bfh.anuto.game.objects.TargetedShot;
import ch.bfh.anuto.util.math.Vector2;

public class RocketShot extends TargetedShot {

    private final static float MOVEMENT_SPEED = 2.5f;
    private final static float ANIMATION_SPEED = 3f;

    private Sprite mSprite;
    private float mAngle = 0f;

    public RocketShot() {
        mSpeed = MOVEMENT_SPEED;
    }

    public RocketShot(Vector2 position, Enemy target) {
        this();

        setPosition(position);
        setTarget(target);

        mAngle = getDirectionTo(mTarget).angle();
    }

    @Override
    public void onClean() {
        super.onClean();

        mGame.remove(mSprite);
    }

    @Override
    public void onInit() {
        super.onInit();

        mSprite = Sprite.fromResources(mGame.getResources(), R.drawable.rocket_shot, 4);
        mSprite.setListener(this);
        mSprite.setMatrix(1f);
        mSprite.setLayer(Layers.SHOT);
        mSprite.getAnimator().setSequence(mSprite.sequenceForward());
        mSprite.getAnimator().setSpeed(ANIMATION_SPEED);
        mGame.add(mSprite);
    }

    @Override
    public void onDraw(Sprite sprite, Canvas canvas) {
        super.onDraw(sprite, canvas);

        canvas.rotate(mAngle);
    }

    @Override
    public void onTick() {
        mDirection = getDirectionTo(mTarget);
        mAngle = mDirection.angle();

        super.onTick();

        mSprite.animate();
    }

    @Override
    protected void onTargetLost() {
        Enemy closest = (Enemy)mGame.getGameObjects(Enemy.TYPE_ID)
                .min(GameObject.distanceTo(mPosition));

        if (closest == null) {
            mGame.remove(this);
        } else {
            setTarget(closest);
        }
    }

    @Override
    protected void onTargetReached() {
        mGame.add(new ExplosionEffect(mTarget.getPosition()));
        mGame.remove(this);
    }
}
