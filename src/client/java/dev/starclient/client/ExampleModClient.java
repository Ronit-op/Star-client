
package dev.starclient.client;

import dev.starclient.client.gui.ClickGUI;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

public class ExampleModClient implements ClientModInitializer {

    private static KeyMapping openGuiKey;

	    @Override
		    public void onInitializeClient() {

			        openGuiKey = KeyBindingHelper.registerKeyBinding(
					                new KeyMapping(
									                        "key.starclient.open_gui",
															                        GLFW.GLFW_KEY_RIGHT_SHIFT,
																					                        KeyMapping.Category.MISC
																											                )
																															        );

																																	        ClientTickEvents.END_CLIENT_TICK.register(client -> {
																																			            while (openGuiKey.consumeClick()) {
																																						                if (client.screen == null) {
																																										                    client.setScreen(new ClickGUI());
																																															                }
																																																			            }
																																																						        });
																																																								    }
																																																									}
																																																									