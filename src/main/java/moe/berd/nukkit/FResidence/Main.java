package moe.berd.nukkit.FResidence;

import java.util.*;

import cn.nukkit.*;
import cn.nukkit.item.*;
import cn.nukkit.lang.*;
import cn.nukkit.utils.*;
import cn.nukkit.block.*;
import cn.nukkit.event.*;
import cn.nukkit.level.*;
import cn.nukkit.plugin.*;
import cn.nukkit.command.*;
import cn.nukkit.event.block.*;
import cn.nukkit.event.level.*;
import cn.nukkit.event.player.*;
import cn.nukkit.event.entity.*;

import moe.berd.nukkit.FResidence.event.*;
import moe.berd.nukkit.FResidence.utils.*;
import moe.berd.nukkit.FResidence.command.*;
import moe.berd.nukkit.FResidence.provider.*;
import moe.berd.nukkit.FResidence.exception.*;
import moe.berd.nukkit.FResidence.utils.Utils;

public class Main extends PluginBase implements Listener
{
	private static CommandCollection _RES_COMMAND_HELP=new CommandCollection("default")
	{
		{
			addCommand("select",1);
			
			// TODO: area
			addCommand("create",1);
			addCommand("remove",1);
			addCommand("removeall",0);
			// TODO: subzone
			
			addCommand("current",0);
			addCommand("info",0);
			addCommand("list",0);
			// TODO: sublist
			addCommand("version",0);
			addCommand("help",0);
			addCommand("confirm",1);
			
			addCommand("pset",4);
			addCommand("set",3);
			
			addCommand("default",1);
			addCommand("give",2);
			addCommand("message",2);
			addCommand("mirror",2);
			addCommand("rename",2);
			// TODO: renamearea
			addCommand("tp",1);
			addCommand("tpset",0);
			// TODO: unstuck
		}
	}, _RESADMIN_COMMAND_HELP=new CommandCollection("admin")
	{
		{
			addCommand("list",1);
			addCommand("listall",0);
			
			addCommand("removeall",1);
			addCommand("removeworld",1);
			
			addCommand("setowner",2);
			addCommand("server",1);
			
			addCommand("reload",0);
			addCommand("help",0);
		}
	};
	
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
			baseLang=new LanguageProvider(this,this.getServer().getLanguage().getLang(),"chs");
			CommandMap cmd=getServer().getCommandMap();
			cmd.register("FResidence",new ResidenceCommand(this));
			cmd.register("FResidence",new ResidenceAdminCommand(this));
			reload();
			this.getServer().getPluginManager().registerEvents(this,this);
			this.getServer().getScheduler().scheduleRepeatingTask(this,new SystemTask(this),20);
		}
		catch(Exception e)
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
		if(val instanceof Player)
		{
			return players.getOrDefault(((Player)val).getId(),null);
		}
		return null;
	}
	
	public boolean onResidenceCommand(CommandSender sender,String[] args) throws FResidenceException
	{
		return onResidenceCommand(sender,args,false);
	}
	
	//@SuppressWarnings("All")
	@SuppressWarnings("ConstantConditions")
	public boolean onResidenceCommand(CommandSender sender,String[] args,boolean granted) throws FResidenceException
	{
		PlayerInfo player;
		CommandInfo cmd;
		if(!_RES_COMMAND_HELP.containsKey(args[0]))
		{
			return false;
		}
		else if(args.length<(cmd=_RES_COMMAND_HELP.get(args[0])).argsCount)
		{
			sender.sendMessage(Utils.getColoredString(cmd.usage,TextFormat.AQUA));
		}
		else if((player=getPlayer(sender))!=null || args[0].equals("help") || args[0].equals("version"))
		{
			switch(args[0])
			{
			case "select":
			{
				switch(args[1].toLowerCase())
				{
				case "size":
					if(!player.isSelectFinish())
					{
						player.sendColorMessage("commands.select.select_first",TextFormat.RED);
						break;
					}
					int size=player.validateSelect();
					if(size==-1)
					{
						break;
					}
					Position pos1=player.getPos1(), pos2=player.getPos2();
					player.sendColorMessage("commands.select.size",TextFormat.GREEN,
							String.valueOf(size),String.valueOf(ConfigProvider.getDouble("MoneyPerBlock")*size),ConfigProvider.getString("MoneyName"),
							String.valueOf(pos1.getFloorX()),String.valueOf(pos1.getFloorY()),String.valueOf(pos1.getFloorZ()),
							String.valueOf(pos2.getFloorX()),String.valueOf(pos2.getFloorY()),String.valueOf(pos2.getFloorZ()));
					break;
				case "chunk":
					player.setPos1(new Position((player.getPlayer().getFloorX()>>4)*16,0,(player.getPlayer().getFloorZ()>>4)*16,player.getPlayer().getLevel()))
							.setPos2(new Position((player.getPlayer().getFloorX()>>4+1)*16,256,(player.getPlayer().getFloorZ()>>4+1)*16,player.getPlayer().getLevel()))
							.sendColorMessage("commands.select.chunk.success",TextFormat.GREEN).validateSelect();
					break;
				case "vert":
					if(ConfigProvider.getBoolean("SelectVert"))
					{
						player.sendColorMessage("commands.select.vert.no_need",TextFormat.RED);
						break;
					}
					if(!player.isSelectFinish())
					{
						player.sendColorMessage("commands.select.select_first",TextFormat.RED);
						break;
					}
					player.getPos1().y=0;
					player.getPos2().y=256;
					player.sendColorMessage("commands.select.vert.success",TextFormat.GREEN).validateSelect();
					break;
				default:
					/* TODO: WTF
					if(isset($args[3]))
					{
						$offset=new Vector3(...array_map(function($val)
						{
							return abs(intval($val));
						},array_slice($args,1)));
						$sender->setPos1(Position::fromObject($sender->add($offset),$sender->getLevel()))->setPos2(Position::fromObject($sender->add($offset->multiply(-1)),$sender->getLevel()))->validateSelect(true);
						unset($offset);
					}
					*/
					sender.sendMessage(Utils.getColoredString(cmd.usage,TextFormat.AQUA));
					break;
				}
				break;
			}
			case "create":
			{
				if(!player.isSelectFinish())
				{
					player.sendColorMessage("commands.create.select_first",TextFormat.RED);
					break;
				}
				if(!granted && ConfigProvider.getStringList("BlackListWorld").contains(player.getPos1().getLevel().getFolderName().toLowerCase()))
				{
					player.sendColorMessage("notice.blacklist",TextFormat.RED);
					break;
				}
				if(args[1].length()<=0 || args[1].length()>=60)
				{
					player.sendColorMessage("commands.create.invalid_name",TextFormat.RED);
					break;
				}
				if(!granted && provider.getResidencesByOwner(player).length>=ConfigProvider.getInt("MaxResidenceCount"))
				{
					player.sendColorMessage("commands.create.too_many_residences",TextFormat.YELLOW,ConfigProvider.getString("MaxResidenceCount"));
					break;
				}
				if(provider.getResidenceByName(args[1])!=null)
				{
					player.sendColorMessage("commands.create.name_exists",TextFormat.RED);
					break;
				}
				double money=player.validateSelect();
				if(money==-1)
				{
					break;
				}
				money*=granted ? 0 : ConfigProvider.getDouble("MoneyPerBlock");
				if(money>Economy.getMoney(player))
				{
					player.sendColorMessage("commands.create.out_of_money",TextFormat.RED,String.valueOf(money),ConfigProvider.getString("MoneyName"));
					break;
				}
				Position pos1=player.getPos1(), pos2=player.getPos2();
				String level=pos1.getLevel().getFolderName().toLowerCase();
				int conflict=0;
				for(Residence res : provider.getAllResidences().values())
				{
					if(res.getLevelName().equals(level) && this.check(res.getPos1(),res.getPos2(),pos1,pos2))
					{
						player.sendColorMessage("commands.create.conflict",TextFormat.YELLOW,res.getName());
						++conflict;
					}
				}
				if(conflict>0)
				{
					player.sendColorMessage("commands.create.conflict_count",TextFormat.RED,String.valueOf(conflict));
					break;
				}
				ResidenceAddEvent ev=new ResidenceAddEvent(money,pos1,pos2,args[1],player.getPlayer());
				if(Utils.callEvent(ev))
				{
					provider.addResidence(ev.getPos1(),ev.getPos2(),ev.getPlayer(),ev.getResName());
					Economy.reduceMoney(player,ev.getMoney());
					player.setPos1(null).setPos2(null).sendColorMessage("commands.create.success",TextFormat.GREEN,String.valueOf(ev.getMoney()),ConfigProvider.getString("MoneyName"));
				}
				break;
			}
			case "remove":
			{
				Residence res=provider.getResidenceByName(args[1]);
				if(res==null)
				{
					player.sendColorMessage("commands.remove.not_exists",TextFormat.RED);
					break;
				}
				if(!granted && !res.isOwner(player))
				{
					player.sendColorMessage("commands.remove.no_permission",TextFormat.RED);
					break;
				}
				if(Utils.callEvent(new ResidenceRemoveEvent(res)))
				{
					provider.removeResidence(res);
					player.sendColorMessage("commands.remove.success",TextFormat.GREEN,res.getName());
				}
				break;
			}
			case "removeall":
			{
				String code=String.valueOf((int)(Math.random()*9999));
				player.addConfirm("default",code,"removeall")
						.sendColorMessage("commands.removeall.notice",TextFormat.YELLOW,code);
				break;
			}
			case "current":
			{
				Residence res=provider.getResidenceByPosition(player.getPlayer());
				if(res==null)
				{
					player.sendColorMessage("commands.current.empty",TextFormat.WHITE);
					break;
				}
				player.sendColorMessage("commands.current.success",TextFormat.AQUA,res.getOwner().isEmpty() ? Utils.translate("server") : res.getOwner(),res.getName());
				break;
			}
			case "info":
			{
				Residence res=args.length>1 ? provider.getResidenceByName(args[1]) : provider.getResidenceByPosition(player.getPlayer());
				if(res==null)
				{
					player.sendColorMessage(args.length>1 ? "commands.info.not_found" : "commands.info.empty",TextFormat.RED,args.length>1 ? args[1] : "");
					break;
				}
				player.sendColorMessage("commands.info.success",TextFormat.GREEN,res.getName(),res.getOwner().isEmpty() ? Utils.translate("server") : res.getOwner(),String.valueOf(res.getSize()));
				break;
			}
			case "list":
			{
				Residence[] res=provider.getResidencesByOwner(player);
				if(res.length==0)
				{
					player.sendColorMessage("commands.list.empty",TextFormat.YELLOW);
					break;
				}
				int page=0;
				if(args.length>1)
				{
					try
					{
						page=Integer.parseInt(args[1]);
					}
					catch(Exception ignored)
					{
					
					}
				}
				player.getPlayer().sendMessage(Utils.makeList("领地列表",res,page,5,v->
				{
					Residence val=(Residence)v;
					return TextFormat.DARK_GREEN+val.getName()+TextFormat.WHITE+" - "+Utils.translate("commands.list.world")+":"+val.getLevelName()+","+Utils.translate("commands.list.size")+":"+val.getSize()+" "+Utils.translate("commands.list.blocks");
				}));
				break;
			}
			case "version":
			{
				sender.sendMessage(("--- FResidence ---\n{WHITE}版本/Version: {DARK_GREEN}"+getDescription().getVersion()+"\n{WHITE}作者/Author: {DARK_GREEN}FENGberd\n{WHITE}E-Mail: {DARK_GREEN}fengberd@gmail.com")
						.replace("{WHITE}",TextFormat.WHITE.toString())
						.replace("{DARK_GREEN}",TextFormat.DARK_GREEN.toString()));
				break;
			}
			case "help":
			{
				int page=0;
				if(args.length>1)
				{
					try
					{
						page=Integer.parseInt(args[1]);
					}
					catch(Exception ignored)
					{
					
					}
				}
				sender.sendMessage(Utils.makeList("FResidence Help",_RES_COMMAND_HELP.values().toArray(),page,player==null ? 50 : 5,v->
				{
					CommandInfo val=((CommandInfo)v);
					return TextFormat.DARK_GREEN+val.command+": "+TextFormat.WHITE+Utils.translate(val.description);
				}));
			}
			case "confirm":
			{
				PlayerInfo.ConfirmInfo confirm=player.getConfirm("default",args[1]);
				switch(confirm.action)
				{
				case "removeall":
					player.sendColorMessage("commands.removeall.success",TextFormat.GREEN,String.valueOf(provider.removeResidencesByOwner(player)));
					break;
				}
				break;
			}
			// TODO: onResidenceCommand
			case "pset":
			{
			
			}
			case "set":
			{
			
			}
			case "default":
			{
			
			}
			case "give":
			{
			
			}
			case "message":
			{
			
			}
			case "mirror":
			{
			
			}
			case "rename":
			{
			
			}
			case "tp":
			{
			
			}
			case "tpset":
			{
			
			}
			}
		}
		else
		{
			sender.sendMessage(Utils.getColoredString("command.residence.in_game_only",TextFormat.RED));
		}
		return true;
	}
	
	public boolean onResidenceAdminCommand(CommandSender sender,String[] args) throws FResidenceException
	{
		// TODO: onResidenceAdminCommand
		return true;
	}
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		if(event.getAction()==PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK)
		{
			PlayerInfo player=getPlayer(event.getPlayer());
			Residence res=provider.getResidenceByPosition(event.getBlock());
			if(res!=null)
			{
				if(!player.isOp() && !res.isOwner(player) && (isProtectBlock(event.getBlock()) || isBlockedItem(event.getItem())) && !res.hasPermission(player,Permissions.PERMISSION_USE))
				{
					player.sendColorTip(res.getMessage(Messages.INDEX_PERMISSION));
					event.setCancelled();
				}
				else if(event.getItem() instanceof ItemFlintSteel && !res.getPermission(Permissions.PERMISSION_FIRE))
				{
					event.setCancelled();
				}
			}
			else
			{
				if(event.getItem().getId()==ConfigProvider.getInt("SelectItem"))
				{
					player.setPos1(event.getBlock()).sendColorMessage("select.pos1",TextFormat.YELLOW).validateSelect();
					event.setCancelled();
				}
				else if(!player.isOp() && ConfigProvider.getStringList("WhiteListWorld").contains(event.getBlock().getLevel().getFolderName().toLowerCase()))
				{
					player.sendColorMessage("notice.whitelist",TextFormat.YELLOW);
					event.setCancelled();
				}
			}
		}
	}
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void onItemFrameDropItem(ItemFrameDropItemEvent event)
	{
		PlayerInfo player=getPlayer(event.getPlayer());
		Residence res=provider.getResidenceByPosition(event.getBlock());
		if(res!=null)
		{
			if(!player.isOp() && !res.isOwner(player) && !res.hasPermission(player,Permissions.PERMISSION_USE))
			{
				player.sendColorTip(res.getMessage(Messages.INDEX_PERMISSION));
				event.setCancelled();
			}
		}
	}
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void onPlayerMove(PlayerMoveEvent event)
	{
		PlayerInfo player=getPlayer(event.getPlayer());
		player.movementLog.add(event.getFrom());
		if(player.movementLog.size()>ConfigProvider.getInt("CheckMoveTick"))
		{
			player.movementLog.remove(0);
		}
		if(--player.checkMoveTick>0)
		{
			return;
		}
		player.checkMoveTick=ConfigProvider.getInt("CheckMoveTick");
		Residence res=provider.getResidenceByPosition(event.getTo().add(-0.5,0,-0.5).round());
		if(res!=null)
		{
			if(!player.isOp() && !res.isOwner(player) && !res.hasPermission(player,Permissions.PERMISSION_MOVE))
			{
				event.setTo(player.movementLog.get(0));
				player.movementLog.remove(player.movementLog.size()-1);
				player.sendColorTip(res.getMessage(Messages.INDEX_PERMISSION));
			}
			else if(!player.inResidence() || player.getResidence().getId()!=res.getId())
			{
				player.sendColorMessage(res.getMessage(Messages.INDEX_ENTER)
						.replace("%name",res.getName())
						.replace("%owner",res.getOwner()),TextFormat.WHITE);
				player.setResidence(res);
			}
		}
		else if(player.inResidence())
		{
			res=player.getResidence();
			player.sendColorMessage(res.getMessage(Messages.INDEX_LEAVE)
					.replace("%name",res.getName())
					.replace("%owner",res.getOwner()),TextFormat.WHITE);
			player.setResidence(null);
		}
	}
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void onBlockPlace(BlockPlaceEvent event)
	{
		PlayerInfo player=getPlayer(event.getPlayer());
		if(!player.isOp())
		{
			Residence res=provider.getResidenceByPosition(event.getBlock());
			if(res!=null)
			{
				if(!res.isOwner(player) && !res.hasPermission(player,Permissions.PERMISSION_BUILD))
				{
					player.sendColorTip(res.getMessage(Messages.INDEX_PERMISSION));
					event.setCancelled();
				}
			}
			else if(ConfigProvider.getStringList("WhiteListWorld").contains(event.getBlock().getLevel().getFolderName().toLowerCase()))
			{
				player.sendColorMessage("notice.whitelist",TextFormat.YELLOW);
				event.setCancelled();
			}
		}
	}
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void onBlockBreak(BlockBreakEvent event)
	{
		PlayerInfo player=getPlayer(event.getPlayer());
		Residence res=provider.getResidenceByPosition(event.getBlock());
		if(res!=null)
		{
			if(!player.isOp() && !res.isOwner(player) && !res.hasPermission(player,Permissions.PERMISSION_BUILD))
			{
				player.sendColorTip(res.getMessage(Messages.INDEX_PERMISSION));
				event.setCancelled();
			}
		}
		else
		{
			if(event.getItem().getId()==ConfigProvider.getInt("SelectItem"))
			{
				player.setPos2(event.getBlock()).sendColorMessage("select.pos2",TextFormat.YELLOW).validateSelect();
				event.setCancelled();
			}
			else if(!player.isOp() && ConfigProvider.getStringList("WhiteListWorld").contains(event.getBlock().getLevel().getFolderName().toLowerCase()))
			{
				player.sendColorMessage("notice.whitelist",TextFormat.YELLOW);
				event.setCancelled();
			}
		}
	}
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void onBlockUpdate(BlockUpdateEvent event)
	{
		Residence res;
		Block block=event.getBlock();
		if(block.getId()>=8 && block.getId()<=11)
		{
			if((res=provider.getResidenceByPosition(block))!=null && !res.getPermission(Permissions.PERMISSION_FLOW))
			{
				event.setCancelled();
			}
		}
		else if(block.getId()==51 && (res=provider.getResidenceByPosition(block))!=null && !res.getPermission(Permissions.PERMISSION_FIRE))
		{
			event.setCancelled();
			block.getLevel().setBlock(block,new BlockAir());
		}
	}
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void onLevelLoad(LevelLoadEvent event)
	{
		provider.reload();
	}
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void onEntityDamage(EntityDamageEvent event)
	{
		Residence res=provider.getResidenceByPosition(event.getEntity());
		if(res!=null)
		{
			if(!res.getPermission(Permissions.PERMISSION_DAMAGE))
			{
				event.setCancelled();
			}
			else if(event instanceof EntityDamageByEntityEvent)
			{
				EntityDamageByEntityEvent event1=(EntityDamageByEntityEvent)event;
				if(event1.getDamager() instanceof Player && event1.getEntity() instanceof Player)
				{
					if(!res.getPermission(Permissions.PERMISSION_PVP) || !(res=provider.getResidenceByPosition(event1.getDamager())).getPermission(Permissions.PERMISSION_PVP))
					{
						getPlayer(event1.getDamager()).sendColorMessage(res.getMessage(Messages.INDEX_PERMISSION),TextFormat.YELLOW);
						event.setCancelled();
					}
				}
			}
		}
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
		double A1LX=Math.min(pos1.x,pos2.x), A1LY=Math.min(pos1.y,pos2.y), A1LZ=Math.min(pos1.z,pos2.z),
				A1HX=Math.max(pos1.x,pos2.x), A1HY=Math.max(pos1.y,pos2.y), A1HZ=Math.max(pos1.z,pos2.z),
				A2LX=Math.min(pos3.x,pos4.x), A2LY=Math.min(pos3.y,pos4.y), A2LZ=Math.min(pos3.z,pos4.z),
				A2HX=Math.max(pos3.x,pos4.x), A2HY=Math.max(pos3.y,pos4.y), A2HZ=Math.max(pos3.z,pos4.z);
		return (((A1HX>=A2LX) && (A1HX<=A2HX)) || ((A1LX>=A2LX) && (A1LX<=A2HX)) || ((A2HX>=A1LX) && (A2HX<=A1HX)) || ((A2LX>=A1LX) && (A2LX<=A1HX))) &&
				(((A1HY>=A2LY) && (A1HY<=A2HY)) || ((A1LY>=A2LY) && (A1LY<=A2HY)) || ((A2HY>=A1LY) && (A2HY<=A1HY)) || ((A2LY>=A1LY) && (A2LY<=A1HY))) &&
				(((A1HZ>=A2LZ) && (A1HZ<=A2HZ)) || ((A1LZ>=A2LZ) && (A1LZ<=A2HZ)) || ((A2HZ>=A1LZ) && (A2HZ<=A1HZ)) || ((A2LZ>=A1LZ) && (A2LZ<=A1HZ)));
	}
}
