package essentialclient.mixins.soulSpeedFovMultiplier;

import essentialclient.feature.clientrule.ClientRules;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.UUID;

@Mixin(AbstractClientPlayerEntity.class)
public abstract class AbstractClientPlayerEntityMixin extends LivingEntity {

    private final UUID SOUL_SPEED_BOOST_ID = UUID.fromString("87f46a96-686f-4796-b035-22e16ee9e038");

    protected AbstractClientPlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "getSpeed()F", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;getAttributeValue(Lnet/minecraft/entity/attribute/EntityAttribute;)D"))
    public void injectedBefore(CallbackInfoReturnable<Float> cir) {
        if (this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).getModifier(SOUL_SPEED_BOOST_ID) != null) {
            this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).removeModifier(SOUL_SPEED_BOOST_ID);
            this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).addTemporaryModifier(new EntityAttributeModifier(SOUL_SPEED_BOOST_ID, "Soul speed boost", 0.01*ClientRules.SOUL_SPEED_FOV_MULTIPLIER.getInt() * (0.03F * (1.0F + (float) EnchantmentHelper.getEquipmentLevel(Enchantments.SOUL_SPEED, this) * 0.35F)), EntityAttributeModifier.Operation.ADDITION));
        }
    }

    @Inject(method = "getSpeed()F", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;getAttributeValue(Lnet/minecraft/entity/attribute/EntityAttribute;)D", shift = At.Shift.AFTER))
    private void injectedAfter(CallbackInfoReturnable<Float> cir) {
        if (this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).getModifier(SOUL_SPEED_BOOST_ID) != null) {
            this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).removeModifier(SOUL_SPEED_BOOST_ID);
            this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).addTemporaryModifier(new EntityAttributeModifier(SOUL_SPEED_BOOST_ID, "Soul speed boost", (0.03F * (1.0F + (float) EnchantmentHelper.getEquipmentLevel(Enchantments.SOUL_SPEED, this) * 0.35F)), EntityAttributeModifier.Operation.ADDITION));
        }
    }

}