package moe.berd.nukkit.FResidence;

import java.util.*;

import cn.nukkit.*;
import cn.nukkit.block.*;
import cn.nukkit.event.*;
import cn.nukkit.event.block.*;
import cn.nukkit.event.entity.*;
import cn.nukkit.event.level.*;
import cn.nukkit.event.player.*;
import cn.nukkit.item.*;
import cn.nukkit.lang.*;
import cn.nukkit.level.*;
import cn.nukkit.plugin.*;
import cn.nukkit.command.*;

import moe.berd.nukkit.FResidence.command.*;
import moe.berd.nukkit.FResidence.utils.*;
import moe.berd.nukkit.FResidence.provider.*;
import moe.berd.nukkit.FResidence.exception.*;

public class Main extends PluginBase implements Listener
{
	private static Main obj;
	private static BaseLang baseLang;
	private static IDataProvider provider;
	
	private static final Set<Integer> blockedItems=new HashSet<Integer>()
	{
		{
			add(Item.SEEDS);
			add(Item.BEETROOT_SEEDS);
			add(Item.MELON_SEEDS);
			add(Item.PUMPKIN_SEEDS);
			add(Item.CARROTS);
			add(Item.POTATOES);
			add(Item.DYE);
			add(Item.BUCKET);
			add(Item.MINECART);
			add(Item.REDSTONE);
		}
	};
	
	public static Main getInstance()
	{
		return obj;
	}
	
	public static BaseLang getLanguage()
	{
		return baseLang;
	}
	
	public static IDataProvider getProvider()
	{
		return provider;
	}
	
	private Map<Long,PlayerInfo> players=new HashMap<>();
	
	@Override
	public void onEnable()
	{
		try
		{
			obj=this;
			baseLang=new BaseLang(this.getServer().getLanguage().getLang());
			CommandMap cmd=getServer().getCommandMap();
			cmd.register("FResidence",new ResidenceCommand(this));
			cmd.register("FResidence",new ResidenceAdminCommand(this));
			reload();
			this.getServer().getPluginManager().registerEvents(this,this);
			this.getServer().getScheduler().scheduleRepeatingTask(this,new SystemTask(this),20);
		}
		catch(FResidenceException e)
		{
			getLogger().error(Utils.translate("exception.initial"),e);
			getServer().forceShutdown();
		}
	}
	
	public void reload() throws FResidenceException
	{
		Utils.init(this);
		ConfigProvider.init(this);
		Economy.init(this,ConfigProvider.getString("PreferEconomy"));
		switch(ConfigProvider.getString("Provider"))
		{
		default:
			getLogger().warning(Utils.translate("exception.provider_not_supported"));
		case "yaml":
			provider=new YamlDataProvider(this);
			break;
		}
		ConfigProvider.set("Provider",provider.getName());
		ConfigProvider.set("PreferEconomy",Economy.getApi().toString());
		getLogger().notice(Utils.translate("current.provider",provider.getName()));
		getLogger().notice(Utils.translate("current.economy",Economy.getApi().toString()));
	}
	
	public void systemTaskCallback(int currentTick)
	{
		players.values().forEach(player->
		{
			if(player.inResidence() && player.getResidence().getPermission(Permissions.PERMISSION_HEALING))
			{
				player.getPlayer().heal(new EntityRegainHealthEvent(player.getPlayer(),1,EntityRegainHealthEvent.CAUSE_CUSTOM));
			}
		});
	}
	
	public PlayerInfo getPlayer(Object val)
	{
		if(val instanceof PlayerInfo)
		{
			return (PlayerInfo)val;
		}
		if(val instanceof String)
		{
			val=getServer().getPlayer((String)val);
		}
		if(val instanceof PlayerEvent)
		{
			val=((PlayerEvent)val).getPlayer();
		}
		if(val instanceof Player)
		{
			return players.getOrDefault(((Player)val).getId(),null);
		}
		return null;
	}
	
	public void onResidenceCommand(CommandSender sender,String[] args) throws FResidenceException
	{
		// TODO: onResidenceCommand
	}
	
	public void onResidenceAdminCommand(CommandSender sender,String[] args) throws FResidenceException
	{
		// TODO: onResidenceAdminCommand
	}
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		if(event.getAction()==PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK)
		{
			// TODO: a lot
		}
	}
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void onItemFrameDropItem(ItemFrameDropItemEvent event)
	{
		// TODO: a lot
	}
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void onPlayerMove(PlayerMoveEvent event)
	{
		// TODO: a lot
	}
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void onBlockPlace(BlockPlaceEvent event)
	{
		// TODO: a lot
	}
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void onBlockBreak(BlockBreakEvent event)
	{
		// TODO: a lot
	}
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void onBlockUpdate(BlockUpdateEvent event)
	{
		// TODO: a lot
	}
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void onLevelLoad(LevelLoadEvent event)
	{
		// TODO: a lot
	}
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void onEntityDamage(EntityDamageEvent event)
	{
		// TODO: a lot
	}
	
	@EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		players.put(event.getPlayer().getId(),new PlayerInfo(event.getPlayer()));
	}
	
	@EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
	public void onPlayerQuit(PlayerQuitEvent event)
	{
		players.remove(event.getPlayer().getId());
	}
	
	public boolean isProtectBlock(Block b)
	{
		return b.canBeActivated();
	}
	
	public boolean isBlockedItem(Item i)
	{
		return i instanceof ItemTool || blockedItems.contains(i.getId());
	}
	
	public boolean check(Position pos1,Position pos2,Position pos3,Position pos4)
	{
		// TODO: a lot
		return false;
	}
}
