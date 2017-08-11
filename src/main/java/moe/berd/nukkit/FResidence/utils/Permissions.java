package moe.berd.nukkit.FResidence.utils;

import java.util.*;

import cn.nukkit.utils.*;

public class Permissions
{
	public static final String PERMISSION_USE="use",
			PERMISSION_MOVE="move",
			PERMISSION_BUILD="build",
			PERMISSION_TELEPORT="tp";
	public static final String PERMISSION_PVP="pvp",
			PERMISSION_FLOW="flow",
			PERMISSION_FIRE="fire",
			PERMISSION_DAMAGE="damage",
			PERMISSION_HEALING="healing";
	
	private static Map<String,Boolean> playerDefaults=new HashMap<String,Boolean>()
	{
		{
			put(PERMISSION_USE,false);
			put(PERMISSION_MOVE,true);
			put(PERMISSION_BUILD,false);
			put(PERMISSION_TELEPORT,false);
		}
	}, defaults=new HashMap<String,Boolean>(playerDefaults)
	{
		{
			put(PERMISSION_PVP,true);
			put(PERMISSION_FLOW,true);
			put(PERMISSION_FIRE,false);
			put(PERMISSION_DAMAGE,true);
			put(PERMISSION_HEALING,false);
		}
	};
	
	public static Map<String,Boolean> getDefaults()
	{
		return new HashMap<>(defaults);
	}
	
	public static Map<String,Boolean> getPlayerDefaults()
	{
		return new HashMap<>(playerDefaults);
	}
	
	public static boolean validateIndex(String index)
	{
		return defaults.containsKey(index.toLowerCase());
	}
	
	public static String validateIndexThrow(String index)
	{
		if(!validateIndex(index))
		{
			throw new IllegalArgumentException(Utils.translate("exception.invalid_permission_index"));
		}
		return index.toLowerCase();
	}
	
	public static boolean validatePlayerIndex(String index)
	{
		return playerDefaults.containsKey(index.toLowerCase());
	}
	
	public static String validatePlayerIndexThrow(String index)
	{
		if(!validatePlayerIndex(index))
		{
			throw new IllegalArgumentException(Utils.translate("exception.invalid_permission_index"));
		}
		return index.toLowerCase();
	}
	
	private Residence residence;
	
	private Map<String,Boolean> permissions=getDefaults();
	private Map<String,Map<String,Boolean>> playerPermissions=new HashMap<>();
	
	public Permissions()
	{
	
	}
	
	public Permissions(Residence res)
	{
		residence=res;
	}
	
	public Permissions(Residence res,ConfigSection data)
	{
		this(res);
		ConfigSection players=data.getSection("players");
		players.getKeys(false).forEach(k->
		{
			ConfigSection player=players.getSection(k);
			Map<String,Boolean> tempPermission=new HashMap<>();
			playerDefaults.keySet().forEach(key->
			{
				if(player.containsKey(key))
				{
					tempPermission.put(key,player.getBoolean(key));
				}
			});
			if(tempPermission.size()>0)
			{
				playerPermissions.put(Utils.getPlayerName(k),tempPermission);
			}
		});
		ConfigSection default_=data.getSection("default");
		permissions.keySet().forEach(key->permissions.put(key,default_.getBoolean(key)));
	}
	
	public ConfigSection getRawData()
	{
		return new ConfigSection()
		{
			{
				set("default",permissions);
				set("players",playerPermissions);
			}
		};
	}
	
	public boolean trySave()
	{
		if(residence!=null)
		{
			residence.save();
			return true;
		}
		return false;
	}
	
	public boolean hasPermission(Object player,String index)
	{
		index=validateIndexThrow(index);
		Map<String,Boolean> temp=playerPermissions.getOrDefault(Utils.getPlayerName(player),null);
		if(temp!=null && temp.getOrDefault(index,false))
		{
			return true;
		}
		return permissions.get(index);
	}
	
	public boolean getPermission(String index)
	{
		return permissions.get(validateIndexThrow(index));
	}
	
	public Permissions setPermission(String index,Object val)
	{
		permissions.put(validateIndexThrow(index),Utils.parseBool(val));
		trySave();
		return this;
	}
	
	public Permissions resetPermissions()
	{
		permissions=getDefaults();
		trySave();
		return this;
	}
	
	public Permissions setPlayerPermission(Object player,String index,Object val)
	{
		Map<String,Boolean> temp=playerPermissions.getOrDefault(Utils.getPlayerName(player),null);
		if(temp==null)
		{
			temp=new HashMap<>();
		}
		temp.put(validatePlayerIndexThrow(index),Utils.parseBool(val));
		playerPermissions.put(Utils.getPlayerName(player),temp);
		trySave();
		return this;
	}
	
	public Permissions removePlayerPermission(Object player,String index)
	{
		Map<String,Boolean> temp=playerPermissions.getOrDefault(Utils.getPlayerName(player),null);
		if(temp!=null)
		{
			temp.remove(validatePlayerIndexThrow(index));
			if(temp.size()==0)
			{
				playerPermissions.remove(Utils.getPlayerName(player));
			}
		}
		trySave();
		return this;
	}
	
	public Permissions clearPlayerPermissions(Object player)
	{
		playerPermissions.remove(Utils.getPlayerName(player));
		trySave();
		return this;
	}
	
	public Permissions clearAllPlayerPermissions()
	{
		playerPermissions.clear();
		trySave();
		return this;
	}
}
