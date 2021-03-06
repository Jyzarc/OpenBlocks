package openblocks.common;

import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import openblocks.IOpenBlocksProxy;
import cpw.mods.fml.common.network.IGuiHandler;

public class ServerProxy implements IOpenBlocksProxy {

	@Override
	public IGuiHandler createGuiHandler() {
		return new CommonGuiHandler();
	}

	@Override
	public void preInit() {}

	@Override
	public void init() {}

	@Override
	public void postInit() {}

	@Override
	public void registerRenderInformation() {}

	@Override
	public void spawnLiquidSpray(World worldObj, FluidStack water, double x, double y, double z, ForgeDirection direction, float angleRadians, float spread) {}

}
