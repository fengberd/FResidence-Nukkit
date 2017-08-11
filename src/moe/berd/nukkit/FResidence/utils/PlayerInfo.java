package moe.berd.nukkit.FResidence.utils;

import java.util.*;

import cn.nukkit.*;
import cn.nukkit.level.*;
import cn.nukkit.plugin.*;
import cn.nukkit.metadata.*;
import moe.berd.nukkit.FResidence.provider.*;

class PlayerInfo implements IPlayer
{
	public int checkMoveTick=10;
	public List<Position> movementLog=new ArrayList<>();
	
	private Player player;
	private Position pos1,pos2;
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
		return c.expired()?null:c;
	}
	
	public PlayerInfo addConfirm(String type,String code,String action,String[] args,int expires)
	{
		confirmQueue.put(type+code,new ConfirmInfo(action,args,expires));
		return this;
	}
	
	// TODO:Color messages and tips
	
	public int validateSelect()
	{
		return validateSelect(false);
	}
	
	public int validateSelect(boolean notify)
	{
		int valid=-1;
		if(isSelectFinish())
		{
			valid=pos1.level.getFolderName().equals(pos2.level.getFolderName())?Utils.calculateSize(pos1,pos2):-1;
			if(valid>=2*2*2)
			{
				// TODO: sendYellowMessage('选区已设定,需要 '.(ConfigProvider::MoneyPerBlock()*$valid).' '.ConfigProvider::MoneyName().'来创建领地');
			}
			else
			{
				valid=-1;
				// TODO: sendYellowMessage('选区无效,请确保你选择的两个点在同一个世界内并且选区大于2x2x2');
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
	
	public void setPos1(Position pos1)
	{
		if(pos1!=null)
		{
			pos1=pos1.floor();
			pos1.y=ConfigProvider.getBoolean("SelectVert")?0:Math.min(Math.max(pos1.y,0),256);
		}
		this.pos1=pos1;
	}
	
	public Position getPos2()
	{
		return pos2;
	}
	
	public void setPos2(Position pos2)
	{
		if(pos2!=null)
		{
			pos2=pos2.floor();
			pos2.y=ConfigProvider.getBoolean("SelectVert")?256:Math.min(Math.max(pos2.y,0),256);
		}
		this.pos2=pos2;
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
}
