package dev.whisperlyric.carpet_hfut_addition.mixins.client;

import dev.whisperlyric.carpet_hfut_addition.utils.HFUTChatPage;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MessageSignature;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

//#if MC >= 260100
//$$ import net.minecraft.client.multiplayer.chat.GuiMessage;
//$$ import net.minecraft.client.multiplayer.chat.GuiMessageSource;
//$$ import net.minecraft.client.multiplayer.chat.GuiMessageTag;
//#else
import net.minecraft.client.GuiMessage;
import net.minecraft.client.GuiMessageTag;
//#endif

/**
 * Client half of paged command output. When a line our server tagged as a page
 * arrives, the previous page of the same command is dropped from chat, so pages
 * replace each other instead of stacking. Only the display list is touched: the
 * removed lines were already written to the log when they first arrived, so
 * nothing is lost.
 */
@Mixin(ChatComponent.class)
public abstract class ChatComponentMixin {

    //#if MC >= 260100
    //$$ @Shadow
    //$$ @Final
    //$$ private List<GuiMessage> allMessages;
    //#else
    @Shadow
    @Final
    private List<GuiMessage> allMessages;
    //#endif

    @Shadow
    private void refreshTrimmedMessages() {
        throw new AssertionError();
    }

    //#if MC >= 260100
    //$$ @Inject(
    //$$         method = "addMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;Lnet/minecraft/client/multiplayer/chat/GuiMessageSource;Lnet/minecraft/client/multiplayer/chat/GuiMessageTag;)V",
    //$$         at = @At("HEAD"))
    //$$ private void hfut$replacePreviousPage(Component content, MessageSignature signature, GuiMessageSource messageSource, GuiMessageTag tag, CallbackInfo ci) {
    //$$     hfut$dropPreviousPages(content);
    //$$ }
    //#else
    @Inject(
            method = "addMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;Lnet/minecraft/client/GuiMessageTag;)V",
            at = @At("HEAD"))
    private void hfut$replacePreviousPage(Component content, MessageSignature signature, GuiMessageTag tag, CallbackInfo ci) {
        hfut$dropPreviousPages(content);
    }
    //#endif

    /** Drops every stored line of the same command whose batch differs from the incoming one. */
    private void hfut$dropPreviousPages(Component incoming) {
        HFUTChatPage.PageTag incomingTag = hfut$pageTag(incoming);
        if (incomingTag == null) {
            return;
        }
        boolean removed = this.allMessages.removeIf(message -> {
            HFUTChatPage.PageTag tag = hfut$pageTag(message.content());
            return tag != null && tag.channel().equals(incomingTag.channel()) && tag.batch() != incomingTag.batch();
        });
        if (removed) {
            this.refreshTrimmedMessages();
        }
    }

    /** Reads the page tag: the zero-width marker sibling first, the root style for older servers. */
    @Unique
    private static HFUTChatPage.PageTag hfut$pageTag(Component content) {
        HFUTChatPage.PageTag tag = HFUTChatPage.parse(content.getStyle().getInsertion());
        if (tag != null) {
            return tag;
        }
        for (Component sibling : content.getSiblings()) {
            tag = HFUTChatPage.parse(sibling.getStyle().getInsertion());
            if (tag != null) {
                return tag;
            }
        }
        return null;
    }
}
