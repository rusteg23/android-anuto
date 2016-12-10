package ch.logixisland.anuto.game.entity.tower;

import ch.logixisland.anuto.game.entity.Entity;
import ch.logixisland.anuto.game.entity.EntityListener;
import ch.logixisland.anuto.game.entity.enemy.Enemy;

public abstract class AimingTower extends Tower implements EntityListener {

    public enum Strategy {
        Closest,
        Weakest,
        Strongest,
        First,
        Last
    }

    private static Strategy sDefaultStrategy = Strategy.Closest;
    private static boolean sDefaultLockTarget = true;

    private Enemy mTarget = null;
    private Strategy mStrategy = sDefaultStrategy;
    private boolean mLockOnTarget = sDefaultLockTarget;

    @Override
    public void init() {
        super.init();
    }

    @Override
    public void clean() {
        super.clean();
        setTarget(null);
    }

    @Override
    public void tick() {
        super.tick();

        if (getGameEngine().tick100ms(this)) {
            if (mTarget != null && getDistanceTo(mTarget) > getRange()) {
                targetLost();
            }

            if (mTarget == null || !mLockOnTarget) {
                nextTarget();
            }
        }
    }


    public Strategy getStrategy() {
        return mStrategy;
    }

    public void setStrategy(Strategy strategy) {
        mStrategy = strategy;
        sDefaultStrategy = strategy;
    }

    public boolean doesLockOnTarget() {
        return mLockOnTarget;
    }

    public void setLockOnTarget(boolean lock) {
        mLockOnTarget = lock;
        sDefaultLockTarget = lock;
    }


    @Override
    public Tower upgrade() {
        Tower upgrade = super.upgrade();

        if (upgrade instanceof AimingTower) {
            AimingTower aiming = (AimingTower)upgrade;
            aiming.mStrategy = this.mStrategy;
            aiming.mLockOnTarget = this.mLockOnTarget;
        }

        return upgrade;
    }

    public Enemy getTarget() {
        return mTarget;
    }

    protected void setTarget(Enemy target) {
        if (mTarget != null) {
            mTarget.removeListener(this);
        }

        mTarget = target;

        if (mTarget != null) {
            mTarget.addListener(this);
        }
    }

    protected void nextTarget() {
        switch (mStrategy) {
            case Closest:
                setTarget(getPossibleTargets().min(Entity.distanceTo(getPosition())));
                break;

            case Strongest:
                setTarget(getPossibleTargets().max(Enemy.health()));
                break;

            case Weakest:
                setTarget(getPossibleTargets().min(Enemy.health()));
                break;

            case First:
                setTarget(getPossibleTargets().min(Enemy.distanceRemaining()));
                break;

            case Last:
                setTarget(getPossibleTargets().max(Enemy.distanceRemaining()));
        }
     }

    protected void targetLost() {
        setTarget(null);
    }

    @Override
    public void entityRemoved(Entity obj) {
        targetLost();
    }
}
