package dev.whisperlyric.carpet_hfut_addition.utils;

import me.fallenbreath.conditionalmixin.api.mixin.ConditionTester;

/**
 * Satisfied only when GCA ships the menu API our compat mixins were compiled
 * against, i.e. {@code api.menu.control.ButtonBuilder}.
 *
 * <p>GCA 2.12.6 predates that class and registered buttons through
 * {@code CustomMenu.addButton(int, Button)} instead. Applying our
 * {@code @Invoker("addButton")} to it throws {@code InvalidAccessorException}
 * while {@code CustomMenu} is being loaded, and GCA loads that class from a
 * {@code ServerPlayer} init handler - so the failure surfaces during login as
 * vanilla's "invalid player data" disconnect. Skipping the compat mixins
 * instead degrades to a missing extra button.
 */
public class GcaMenuApiTester implements ConditionTester {
    private static final String BUTTON_BUILDER = "dev.dubhe.gugle.carpet.api.menu.control.ButtonBuilder";

    @Override
    public boolean isSatisfied(String mixinClassName) {
        try {
            Class.forName(BUTTON_BUILDER, false, GcaMenuApiTester.class.getClassLoader());
            return true;
        } catch (Throwable t) {
            return false;
        }
    }
}
