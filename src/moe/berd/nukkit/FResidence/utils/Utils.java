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
		throw new IllegalArgumentException("This function only accepts String/IPlayer.");
	}
	
	public static boolean validatePlayerName(String val)
	{
		return namePattern.matcher(val).find();
	}
	
	public static String makeList(String title,String[][] data,int page,int itemPerPage,Function<String[],String> callback)
	{
		int total=(int)Math.ceil(data.length/itemPerPage);
		if(callback==null)
		{
			callback=s->TextFormat.DARK_GREEN+s[1]+TextFormat.WHITE+s[2];
		}
		StringBuilder result=new StringBuilder("--- "+title+" ["+page+"/"+total+"] ---");
		for(int i=(page-1)*itemPerPage;i<Math.min(page*itemPerPage,data.length);i++)
		{
			result.append(callback.apply(data[i]));
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
			return v.equals("true") || v.equals("1") || v.equals("真") || v.equals("开") || v.equals("开启");
		}
		return false;
	}
	
	public static Position parsePosition(Map<String,String> data,Level level)
	{
		return new Position(Double.parseDouble(data.get("x")),Double.parseDouble(data.get("y")),Double.parseDouble(data.get("z")),level);
	}
	
	public static Map<String,String> encodeVector3(Vector3 data)
	{
		return new HashMap<String,String>()
		{
			{
				put("x",String.valueOf(data.x));
				put("y",String.valueOf(data.y));
				put("z",String.valueOf(data.z));
			}
		};
	}
	
	public static Map<String,Object> updateConfig(int version,Map<String,Object> data) throws VersionException
	{
		if(version>CONFIG_VERSION)
		{
			throw new VersionException("您当前使用的FResidence版本过旧,无法读取领地数据,请更新插件至最新版!");
		}
		while(version<CONFIG_VERSION)
		{
			for(String key : data.keySet())
			{
				switch(version)
				{
				case 1:
					throw new VersionException("Why you fucking have this version of config file???Are you copying a very old PocketMine config to Nukkit server?");
				}
			}
			version++;
		}
		return data;
	}
	
	public static String getRedString(String msg)
	{
		return getRedString(msg,new String[]{});
	}
	
	public static String getRedString(String msg,String[] params)
	{
		return getColoredString(msg,TextFormat.RED,params);
	}
	
	public static String getAquaString(String msg)
	{
		return getAquaString(msg,new String[]{});
	}
	
	public static String getAquaString(String msg,String[] params)
	{
		return getColoredString(msg,TextFormat.AQUA,params);
	}
	
	public static String getGreenString(String msg)
	{
		return getGreenString(msg,new String[]{});
	}
	
	public static String getGreenString(String msg,String[] params)
	{
		return getColoredString(msg,TextFormat.GREEN,params);
	}
	
	public static String getYellowString(String msg)
	{
		return getYellowString(msg,new String[]{});
	}
	
	public static String getYellowString(String msg,String[] params)
	{
		return getColoredString(msg,TextFormat.YELLOW,params);
	}
	
	public static String getColoredString(String msg,TextFormat color,String[] params)
	{
		return "[FResidence] "+color+Utils.main.getLanguage().translateString(msg,params);
	}
}
