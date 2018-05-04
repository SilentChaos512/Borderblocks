package net.silentchaos512.borderblocks.client.key;

import javax.annotation.Nullable;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.silentchaos512.borderblocks.Borderblocks;
import net.silentchaos512.borderblocks.client.ClientTickHandler;
import net.silentchaos512.borderblocks.client.gui.GuiChooseClass;
import net.silentchaos512.borderblocks.client.gui.GuiFactoryBB;
import net.silentchaos512.borderblocks.client.gui.GuiSkillTree;
import net.silentchaos512.borderblocks.config.Config;
import net.silentchaos512.borderblocks.lib.character.CharacterClass;
import net.silentchaos512.borderblocks.network.MessageUseActionSkill;
import net.silentchaos512.borderblocks.util.PlayerDataHandler;
import net.silentchaos512.borderblocks.util.PlayerDataHandler.PlayerData;
import net.silentchaos512.lib.client.key.KeyTrackerSL;

public class KeyTracker extends KeyTrackerSL {

  public static KeyTracker INSTANCE = new KeyTracker();

  private @Nullable KeyBinding keyOpenConfig = null;
  private KeyBinding keyOpenSkillTree;
  private KeyBinding keyActionSkill;

  public KeyTracker() {

    super(Borderblocks.MOD_NAME);

    if (Config.DEBUG_MODE || Borderblocks.BUILD_NUM <= 0) {
      keyOpenConfig = createBinding("[DEBUG] Open Config", KeyConflictContext.IN_GAME, KeyModifier.NONE, Keyboard.KEY_O);
    }
    keyOpenSkillTree = createBinding("Open Skill Tree", KeyConflictContext.IN_GAME, KeyModifier.NONE, Keyboard.KEY_K);
    keyActionSkill = createBinding("Use Action Skill", KeyConflictContext.IN_GAME, KeyModifier.NONE, Keyboard.KEY_R);
  }

  @Override
  public void onKeyInput(KeyInputEvent event) {

    Minecraft mc = Minecraft.getMinecraft();
    PlayerData data = PlayerDataHandler.get(mc.player);
    // Open Config key (dev only)
    if (safeCheckPressed(keyOpenConfig)) {
      mc.displayGuiScreen(new GuiFactoryBB().createConfigGui(null));
    }
    // Open Skill Tree key
    else if (keyOpenSkillTree.isPressed()) {
      // If player has not chosen a class, display choose class GUI instead of skill tree.
      if (data.getCharacterClass() == CharacterClass.CLASS_UNDEFINED)
        mc.displayGuiScreen(new GuiChooseClass());
      else
        mc.displayGuiScreen(new GuiSkillTree());
    }
    // Action Skill key
    else if (keyActionSkill.isPressed()) {
      double distance = data.getCharacterClass().getActionSkill().getSkillReach(data);
      // FIXME rayTrace will never detect entities
      RayTraceResult rayTrace = mc.player.rayTrace(distance, ClientTickHandler.partialTicks);
      BlockPos hitPos = null;
      EnumFacing hitSide = EnumFacing.UP;
      if (rayTrace != null && rayTrace.typeOfHit == Type.BLOCK) {
        hitPos = rayTrace.getBlockPos();
        hitSide = rayTrace.sideHit;
      } else if (rayTrace != null && rayTrace.typeOfHit == Type.ENTITY) {
        hitPos = rayTrace.entityHit.getPosition();
        hitSide = EnumFacing.UP;
      }

      Borderblocks.network.wrapper.sendToServer(new MessageUseActionSkill(mc.player, hitPos, hitSide, isAltDown()));
    }
  }

  private boolean safeCheckPressed(@Nullable KeyBinding binding) {

    if (binding == null)
      return false;
    return binding.isPressed();
  }
}
