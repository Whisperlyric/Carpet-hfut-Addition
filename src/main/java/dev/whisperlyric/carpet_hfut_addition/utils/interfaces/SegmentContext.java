package dev.whisperlyric.carpet_hfut_addition.utils.interfaces;

import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * Duck interface for the tripwire departure-veto criterion (MC-305475): the
 * endpoints of the movement segment currently swept by
 * {@code Entity.checkInsideBlocks}, plus their bounding boxes. Written by
 * EntityMixin at sweep entry; reads outside the sweep return null.
 */
public interface SegmentContext {
    Vec3 hfut$segFrom();

    Vec3 hfut$segTo();

    AABB hfut$segBox(Vec3 at);
}
