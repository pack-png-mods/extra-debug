package net.earthcomputer.extradebug.mixin;

import net.minecraft.src.Entity;
import net.minecraft.src.EntitySorter;
import net.minecraft.src.WorldRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(EntitySorter.class)
public class MixinEntitySorter {

    @Shadow private Entity field_1594_a;

    /**
     * The best way of avoiding repeated calls to func_1202_a
     * @author earth
     */
    @Overwrite
    public int func_1063_a(WorldRenderer var1, WorldRenderer var2) {
        return Float.compare(var1.func_1202_a(this.field_1594_a), var2.func_1202_a(this.field_1594_a));
    }

}
