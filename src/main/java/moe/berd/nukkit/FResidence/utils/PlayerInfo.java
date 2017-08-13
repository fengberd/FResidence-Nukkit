package moe.berd.nukkit.FResidence.utils;

import java.util.*;

import cn.nukkit.*;
import cn.nukkit.utils.*;
import cn.nukkit.level.*;
import cn.nukkit.plugin.*;
import cn.nukkit.metadata.*;

import moe.berd.nukkit.FResidence.provider.*;

public class PlayerInfo implements IPlayer
{
	public int checkMoveTick=10;
	public List<Location> movementLog=new ArrayList<>();
	
	private Player player;
	private Position pos1, pos2;
	private Residence currentResidence;
	private Map<String,ConfirmInfo> confirmQueue=new HashMap<>();
	
	public PlayerInfo(Player player)
	{
		this.player=player;
	}
	
	public boolean inResidence()
	{
		return currentResidence!=null;
	}
	
	public Residence getResidence()
	{
		return currentResidence;
	}
	
	public PlayerInfo setResidence(Residence res)
	{
		this.currentResidence=res;
		return this;
	}
	
	public ConfirmInfo getConfirm(String type,String code)
	{
		ConfirmInfo c=confirmQueue.getOrDefault(type+code,null);
		return c.expired() ? null : c;
	}
	
	public PlayerInfo addConfirm(String type,String code,String action)
	{
		return addConfirm(type,code,action,new String[0],60);
	}
	
	public PlayerInfo addConfirm(String type,String code,String action,String[] args)
	{
		return addConfirm(type,code,action,args,60);
	}
	
	public PlayerInfo addConfirm(String type,String code,String action,String[] args,int expires)
	{
		confirmQueue.put(type+code,new ConfirmInfo(action,args,expires));
		return this;
	}
	
	public PlayerInfo sendColorTip(String msg)
	{
		return sendColorTip(msg,TextFormat.WHITE,new String[0]);
	}
	
	public PlayerInfo sendColorTip(String msg,TextFormat color)
	{
		return sendColorTip(msg,color,new String[0]);
	}
	
	public PlayerInfo sendColorTip(String msg,TextFormat color,String... args)
	{
		player.sendTip(color+Utils.translate(msg,args));
		return this;
	}
	
	public PlayerInfo sendColorMessage(String msg,TextFormat color)
	{
		return sendColorMessage(msg,color,new String[0]);
	}
	
	public PlayerInfo sendColorMessage(String msg,TextFormat color,String... args)
	{
		player.sendMessage(Utils.getColoredString(msg,color,args));
		return this;
	}
	
	public int validateSelect()
	{
		return validateSelect(true);
	}
	
	public int validateSelect(boolean notify)
	{
		int valid=-1;
		if(isSelectFinish())
		{
			valid=pos1.level.getFolderName().equals(pos2.level.getFolderName()) ? Utils.calculateSize(pos1,pos2) : -1;
			if(valid>=2*2*2)
			{
				sendColorMessage("select.done",TextFormat.YELLOW,String.valueOf(ConfigProvider.getDouble("MoneyPerBlock")*valid),ConfigProvider.getString("MoneyName"));
			}
			else
			{
				valid=-1;
				sendColorMessage("select.invalid",TextFormat.YELLOW);
			}
		}
		return valid;
	}
	
	public boolean isSelectFinish()
	{
		return pos1!=null && pos2!=null;
	}
	
	public Position getPos1()
	{
		return pos1;
	}
	
	public PlayerInfo setPos1(Position pos1)
	{
		if(pos1!=null)
		{
			pos1=pos1.floor();
			pos1.y=ConfigProvider.getBoolean("SelectVert") ? 0 : Math.min(Math.max(pos1.y,0),256);
		}
		this.pos1=pos1;
		return this;
	}
	
	public Position getPos2()
	{
		return pos2;
	}
	
	public PlayerInfo setPos2(Position pos2)
	{
		if(pos2!=null)
		{
			pos2=pos2.floor();
			pos2.y=ConfigProvider.getBoolean("SelectVert") ? 256 : Math.min(Math.max(pos2.y,0),256);
		}
		this.pos2=pos2;
		return this;
	}
	
	@Override
	public boolean isOnline()
	{
		return player.isOnline();
	}
	
	@Override
	public String getName()
	{
		return player.getName();
	}
	
	@Override
	public boolean isBanned()
	{
		return player.isBanned();
	}
	
	@Override
	public void setBanned(boolean value)
	{
		player.setBanned(value);
	}
	
	@Override
	public boolean isWhitelisted()
	{
		return player.isWhitelisted();
	}
	
	@Override
	public void setWhitelisted(boolean value)
	{
		player.setWhitelisted(value);
	}
	
	@Override
	public Player getPlayer()
	{
		return player;
	}
	
	@Override
	public Server getServer()
	{
		return player.getServer();
	}
	
	@Override
	public Long getFirstPlayed()
	{
		return player.getFirstPlayed();
	}
	
	@Override
	public Long getLastPlayed()
	{
		return player.getLastPlayed();
	}
	
	@Override
	public boolean hasPlayedBefore()
	{
		return player.hasPlayedBefore();
	}
	
	@Override
	public void setMetadata(String metadataKey,MetadataValue newMetadataValue) throws Exception
	{
		player.setMetadata(metadataKey,newMetadataValue);
	}
	
	@Override
	public List<MetadataValue> getMetadata(String metadataKey) throws Exception
	{
		return player.getMetadata(metadataKey);
	}
	
	@Override
	public boolean hasMetadata(String metadataKey) throws Exception
	{
		return player.hasMetadata(metadataKey);
	}
	
	@Override
	public void removeMetadata(String metadataKey,Plugin owningPlugin) throws Exception
	{
		player.removeMetadata(metadataKey,owningPlugin);
	}
	
	@Override
	public boolean isOp()
	{
		return player.isOp();
	}
	
	@Override
	public void setOp(boolean value)
	{
		player.setOp(value);
	}
	
	public class ConfirmInfo
	{
		public long expires;
		public String action;
		public String[] args;
		
		public ConfirmInfo(String action,String[] args,int expiresSeconds)
		{
			this.args=args;
			this.action=action;
			this.expires=System.currentTimeMillis()+expiresSeconds*1000;
		}
		
		public boolean expired()
		{
			return expires<System.currentTimeMillis();
		}
	}
}
