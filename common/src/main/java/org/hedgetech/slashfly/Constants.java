package org.hedgetech.slashfly;

import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Constants Utility class
 */
public final class Constants {
	/**
	 * Mod Id - should be all lowercase
	 */
	public static final String MOD_ID = "slashfly";

	/**
	 * Mod Name - Should be PascalCase
	 */
	public static final String MOD_NAME = "SlashFly";

	/**
	 * Logger - To make more consistent logging instead of using stdout
	 */
	public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);

	public static final String FLY_TOGGLE_PERM = "slashfly.command.fly";
	public static final String FLY_SPEED_PERM = "slashfly.command.fly.speed";
	public static final String FLY_OTHERS_PERM = "slashfly.command.fly.others"; // For future use

	private Constants() {
		throw new UnsupportedOperationException("Static Utility class, no need to instantiate");
	}
}