package texture;

import entity.animateentity.AnimateEntity;
import entity.animateentity.SpikeTrap;
import entity.animateentity.Swamp;
import graphics.Sprite;

public class AnimationEntityTexture {
    public static AnimateEntity setAnimateEntity(char c, int i, int j) {
        switch (c) {
            case '^':
                return new SpikeTrap(j, i, Sprite.spike_trap);
            case '~':
                return new Swamp(j, i, Sprite.swamp);
            default:
                return null;
        }
    }
}
