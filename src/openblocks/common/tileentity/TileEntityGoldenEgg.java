package openblocks.common.tileentity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.ForgeDirection;
import openblocks.common.entity.EntityMutant;
import openmods.OpenMods;
import openmods.api.IPlaceAwareTile;
import openmods.entity.EntityBlock;
import openmods.sync.ISyncableObject;
import openmods.sync.SyncableInt;
import openmods.tileentity.SyncedTileEntity;

public class TileEntityGoldenEgg extends SyncedTileEntity implements IPlaceAwareTile {

	private static final String TALLY_NBT_KEY = "tally";
	// private static final String STAGE_NBT_KEY = "stage";
	private static final int STAGE_CHANGE_TICK = 600;
	public static final int ANIMATION_TIME = 400;
	private static final double STAGE_CHANGE_CHANCE = 0.8;
	public int animationStageTicks = 0;
	public float rotation;

	private ArrayList<EntityBlock> blocks = new ArrayList<EntityBlock>();
	private HashMap<String, Integer> dnas = new HashMap<String, Integer>();
	private SyncableInt stage;
	
	private String owner;

	@Override
	protected void createSyncedFields() {
		stage = new SyncableInt(0);
	}

	private boolean stageElapsed() {
		 return animationStageTicks > 0 ? animationStageTicks >= ANIMATION_TIME : OpenMods.proxy.getTicks(worldObj) % STAGE_CHANGE_TICK == 0 && worldObj.rand.nextDouble() < STAGE_CHANGE_CHANCE;
	}
	
	@Override
	public void updateEntity() {
		super.updateEntity();
		if (!worldObj.isRemote) {
			if (stageElapsed()) {
				incrementStage();
				System.out.println("Egg entering stage" + stage.getValue());
			}
			if (stage.getValue() >= 1) {

			}
			if (stage.getValue() >= 2) {

			}
			if (stage.getValue() >= 3) {

			}
			if (stage.getValue() >= 4) {
				// TODO: check whitelist
				// maybe this should be more.. interesting. shapes or
				// something?!
				int posX = xCoord + worldObj.rand.nextInt(20) - 10;
				int posY = yCoord + worldObj.rand.nextInt(2) - 1;
				int posZ = zCoord + worldObj.rand.nextInt(20) - 10;
				if (blocks != null && posX != xCoord && posY != yCoord && posZ != zCoord && worldObj.rand.nextInt(10) == 0) {
					EntityBlock block = EntityBlock.create(worldObj, posX, posY, posZ);
					if (block != null) {
						block.setHasAirResistance(false);
						block.setHasGravity(false);
						block.setShouldDrop(false);
						block.motionY = 0.1;
						//block.setPositionAndRotation(posX, posY, posZ, 0, 0);
						blocks.add(block);
						worldObj.spawnEntityInWorld(block);
					}
				}
				if(ANIMATION_TIME - animationStageTicks < 20 && blocks != null) {
					for(EntityBlock block : blocks) {
						block.setShouldDrop(true);
						block.motionY = -0.9;
						block.setHasGravity(true);
					}
					blocks.clear();
					blocks = null;
				}
			}
			if (stage.getValue() >= 5) {
				worldObj.setBlockToAir(xCoord, yCoord, zCoord);
				worldObj.createExplosion(null, 0.5 + xCoord, 0.5 + yCoord, 0.5 + zCoord, 2, true);
				EntityMutant mutant = new EntityMutant(worldObj);
	            mutant.setOwner(owner);
				mutant.setTraitsFromMap(dnas);
				mutant.setPositionAndRotation(0.5 + xCoord, 0.5 + yCoord, 0.5 + zCoord, 0, 0);
				worldObj.spawnEntityInWorld(mutant);
			}
		}
		if(stage.getValue() >= 4 && animationStageTicks < ANIMATION_TIME) {
			animationStageTicks++;
		}
	}

	public int getStage() {
		return stage.getValue();
	}

	private void incrementStage() {
		if (dnas.size() > 0) {
			stage.modify(1);
			sync();
		}
	}

	public boolean addDNAFromItemStack(ItemStack itemStack) {
		if (itemStack != null && stage.getValue() == 0) {
			NBTTagCompound tag = itemStack.getTagCompound();
			if (tag != null && tag.hasKey("entity")) {
				String entity = tag.getString("entity");
				int count = 0;
				if (dnas.containsKey(entity)) {
					count = dnas.get(entity);
				}
				dnas.put(entity, count + 1);
				return true;
			}
		}
		return false;
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		NBTTagCompound entitiesTag = new NBTTagCompound();
		for (Entry<String, Integer> dnaTally : dnas.entrySet()) {
			entitiesTag.setInteger(dnaTally.getKey(), dnaTally.getValue());
		}
		nbt.setCompoundTag(TALLY_NBT_KEY, entitiesTag);
		nbt.setString("owner", owner);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		dnas.clear();
		if (nbt.hasKey(TALLY_NBT_KEY)) {
			NBTTagCompound tallyTag = nbt.getCompoundTag(TALLY_NBT_KEY);
			for (NBTBase tag : (Collection<NBTBase>)tallyTag.getTags()) {
				dnas.put(tag.getName(), tallyTag.getInteger(tag.getName()));
			}
		}
		owner = nbt.getString("owner");
	}

	@Override
	public void onSynced(Set<ISyncableObject> changes) {

	}

	@Override
	public void onBlockPlacedBy(EntityPlayer player, ForgeDirection side, ItemStack stack, float hitX, float hitY, float hitZ) {
		if (player != null) {
			owner = player.username;
		}
	}
	
}
