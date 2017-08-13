package moe.berd.nukkit.FResidence.utils;

import java.util.*;
import java.util.regex.*;
import java.util.function.*;

import cn.nukkit.*;
import cn.nukkit.level.*;
import cn.nukkit.math.*;
import cn.nukkit.plugin.*;
import cn.nukkit.utils.*;

import moe.berd.nukkit.FResidence.*;
import moe.berd.nukkit.FResidence.event.*;
import moe.berd.nukkit.FResidence.exception.*;

public class Utils
{
	public static final int CONFIG_VERSION=2;
	
	private static final Pattern namePattern=Pattern.compile("^[a-zA-Z0-9_]{3,16}$");
	
	private static Main main;
	private static PluginManager pluginManager;
	
	public static void init(Main main)
	{
		Utils.main=main;
		Utils.pluginManager=main.getServer().getPluginManager();
	}
	
	public static boolean callEvent(FResidenceEvent ev)
	{
		pluginManager.callEvent(ev);
		return !(ev instanceof CancellableFResidenceEvent) || !ev.isCancelled();
	}
	
	public static int calculateSize(Vector3 pos1,Vector3 pos2)
	{
		return Math.abs(pos1.getFloorX()-pos2.getFloorX())*Math.abs(pos1.getFloorY()-pos2.getFloorY())*Math.abs(pos1.getFloorZ()-pos2.getFloorZ());
	}
	
	public static String getPlayerName(Object var)
	{
		if(var instanceof String)
		{
			return ((String)var).toLowerCase();
		}
		if(var instanceof IPlayer)
		{
			return ((IPlayer)var).getName().toLowerCase();
		}
		throw new IllegalArgumentException(Utils.translate("exception.get_player_name_args"));
	}
	
	public static boolean validatePlayerName(String val)
	{
		return namePattern.matcher(val).find();
	}
	
	public static String makeList(String title,Object[] data,int page,int itemPerPage,Function<Object,String> callback)
	{
		int total=(int)Math.ceil(data.length/(double)itemPerPage);
		if(page>total || page<1)
		{
			page=1;
		}
		StringBuilder result=new StringBuilder("--- "+title+" ["+page+"/"+total+"] ---\n");
		for(int i=(page-1)*itemPerPage;i<Math.min(page*itemPerPage,data.length);i++)
		{
			result.append(callback.apply(data[i])).append("\n");
		}
		if(result.charAt(result.length()-1)=='\n')
		{
			result.deleteCharAt(result.length()-1);
		}
		return result.toString();
	}
	
	public static boolean parseBool(Object val)
	{
		if(val instanceof Boolean)
		{
			return (boolean)val;
		}
		if(val instanceof Integer)
		{
			return ((int)val)>0;
		}
		if(val instanceof String)
		{
			String v=((String)val).toLowerCase();
			return v.equals("true") || v.equals("1") || v.equals(Utils.translate("option.on.1")) || v.equals(Utils.translate("option.on.2")) || v.equals(Utils.translate("option.on.3"));
		}
		return false;
	}
	
	public static Position parsePosition(ConfigSection data,Level level)
	{
		return new Position(data.getDouble("x"),data.getDouble("y"),data.getDouble("z"),level);
	}
	
	public static ConfigSection encodeVector3(Vector3 data)
	{
		return new ConfigSection()
		{
			{
				set("x",data.x);
				set("y",data.y);
				set("z",data.z);
			}
		};
	}
	
	public static List updateConfig(int version,List data) throws VersionException
	{
		if(version>CONFIG_VERSION)
		{
			throw new VersionException("exception.version_too_old");
		}
		while(version<CONFIG_VERSION)
		{
			for(Object res : data)
			{
				switch(version)
				{
				case 1:
					throw new VersionException("exception.wtf");
				}
			}
			version++;
		}
		return data;
	}
	
	public static String translate(String data,String... params)
	{
		return Main.getLanguage().translateString(data,params).replace("\\n","\n");
	}
	
	public static String getColoredString(String msg,TextFormat color)
	{
		return getColoredString(msg,color,new String[0]);
	}
	
	public static String getColoredString(String msg,TextFormat color,String... params)
	{
		return "[FResidence] "+color+translate(msg,params);
	}
}
