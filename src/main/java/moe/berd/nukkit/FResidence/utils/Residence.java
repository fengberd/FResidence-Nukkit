package moe.berd.nukkit.FResidence.utils;

import cn.nukkit.*;
import cn.nukkit.level.*;
import cn.nukkit.utils.*;

import moe.berd.nukkit.FResidence.exception.*;
import moe.berd.nukkit.FResidence.provider.*;

public class Residence
{
	private IDataProvider provider;
	
	private int id=-1, minX, minY, minZ, maxX, maxY, maxZ;
	
	private String name, owner, level;
	private Position pos1, pos2, teleport;
	
	private Messages message;
	private Permissions permission;
	
	public Residence(IDataProvider provider,int id,String name,Object owner,Position pos1,Position pos2)
	{
		this.id=id;
		this.provider=provider;
		
		this.name=name;
		this.owner=Utils.getPlayerName(owner);
		this.level=pos1.getLevel().getFolderName().toLowerCase();
		
		this.pos1=pos1;
		this.pos2=pos2;
		this.teleport=pos1;
		
		message=new Messages(this);
		permission=new Permissions(this);
		calculateBoundary();
	}
	
	public Residence(IDataProvider provider,int id,ConfigSection data) throws ResidenceInstantiationException
	{
		this.id=id;
		this.provider=provider;
		
		this.name=data.getString("name");
		this.owner=Utils.getPlayerName(data.getString("owner"));
		this.level=data.getString("level").toLowerCase();
		
		Level level=Server.getInstance().getLevelByName(this.level);
		if(level==null)
		{
			throw new ResidenceInstantiationException("exception.level_not_found");
		}
		
		this.pos1=Utils.parsePosition(data.getSection("positions.pos1"),level);
		this.pos2=Utils.parsePosition(data.getSection("positions.pos2"),level);
		this.teleport=Utils.parsePosition(data.getSection("positions.teleport"),level);
		
		message=new Messages(this,data.getSection("messages"));
		permission=new Permissions(this,data.getSection("permissions"));
		calculateBoundary();
	}
	
	public ConfigSection getRawData()
	{
		return new ConfigSection()
		{
			{
				set("name",name);
				set("owner",owner);
				set("level",level);
				set("messages",message.getRawData());
				set("positions.pos1",Utils.encodeVector3(pos1));
				set("positions.pos2",Utils.encodeVector3(pos2));
				set("positions.teleport",Utils.encodeVector3(teleport));
				set("permissions",permission.getRawData());
			}
		};
	}
	
	public Residence save()
	{
		provider.save();
		return this;
	}
	
	public int getId()
	{
		return id;
	}
	
	public Position getPos1()
	{
		return pos1;
	}
	
	public Position getPos2()
	{
		return pos2;
	}
	
	public int getSize()
	{
		return Utils.calculateSize(pos1,pos2);
	}
	
	public String getName()
	{
		return name;
	}
	
	public Residence setName(String name)
	{
		this.name=name;
		save();
		return this;
	}
	
	public boolean isOwner(Object player)
	{
		return owner.equals(Utils.getPlayerName(player));
	}
	
	public String getOwner()
	{
		return owner;
	}
	
	public Residence setOwner(Object owner)
	{
		this.owner=Utils.getPlayerName(owner);
		save();
		return this;
	}
	
	public String getMessage(String index)
	{
		return message.getMessage(index);
	}
	
	public Messages getMessages()
	{
		return message;
	}
	
	public Residence setMessages(Messages message)
	{
		this.message=message;
		save();
		return this;
	}
	
	public String getLevelName()
	{
		return level;
	}
	
	public boolean hasPermission(Object player,String index)
	{
		return permission.hasPermission(player,index);
	}
	
	public boolean getPermission(String index)
	{
		return permission.getPermission(index);
	}
	
	public Permissions getPermissions()
	{
		return permission;
	}
	
	public Residence setPermissions(Permissions permission)
	{
		this.permission=permission;
		save();
		return this;
	}
	
	public Position getTeleportPos()
	{
		return teleport;
	}
	
	public boolean setTeleportPos(Position teleport)
	{
		if(teleport!=null && !teleport.getLevel().getFolderName().toLowerCase().equals(level))
		{
			return false;
		}
		this.teleport=teleport;
		save();
		return true;
	}
	
	public Residence calculateBoundary()
	{
		maxX=Math.max(pos1.getFloorX(),pos2.getFloorX());
		maxY=Math.max(pos1.getFloorY(),pos2.getFloorY());
		maxZ=Math.max(pos1.getFloorZ(),pos2.getFloorZ());
		minX=Math.min(pos1.getFloorX(),pos2.getFloorX());
		minY=Math.min(pos1.getFloorY(),pos2.getFloorY());
		minZ=Math.min(pos1.getFloorZ(),pos2.getFloorZ());
		return this;
	}
	
	public boolean inResidence(Position pos)
	{
		return pos.getLevel().getFolderName().toLowerCase().equals(level) && minX<=pos.x && pos.x<=maxX && minY<=pos.y && pos.y<=maxY && minZ<=pos.z && pos.z<=maxZ;
	}
}
