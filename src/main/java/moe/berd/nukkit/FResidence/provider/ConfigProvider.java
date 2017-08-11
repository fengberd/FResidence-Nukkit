package moe.berd.nukkit.FResidence.provider;

import java.io.*;
import java.util.*;
import java.util.stream.*;

import cn.nukkit.item.*;
import cn.nukkit.utils.*;

import moe.berd.nukkit.FResidence.*;
import moe.berd.nukkit.FResidence.exception.*;

public class ConfigProvider
{
	private static Config config=null;
	private static ConfigSection defaults=new ConfigSection()
	{
		{
			set("Provider","Yaml");
			set("MoneyName","元");
			set("SelectVert",false);
			set("SelectItem",Item.STRING);
			set("PreferEconomy","EconomyAPI");
			set("CheckMoveTick",10);
			set("MoneyPerBlock",0.05);
			set("MaxResidenceCount",3);
			set("BlackListWorld",new ArrayList<String>());
			set("WhiteListWorld",new ArrayList<String>());
		}
	};
	
	public static Config getConfig()
	{
		return config;
	}
	
	public static ConfigSection getDefaults()
	{
		return new ConfigSection(defaults);
	}
	
	public static boolean validateIndex(String index)
	{
		return defaults.containsKey(index);
	}
	
	public static void init(Main main) throws ConfigException
	{
		main.getDataFolder().mkdirs();
		config=new Config(new File(main.getDataFolder(),"config.yml").toString(),Config.YAML,defaults);
		if(config.exists("landItem"))
		{
			throw new ConfigException("Why you fucking have this version of config file???Are you copying a very old PocketMine config to Nukkit server?");
		}
		ConfigSection data=getDefaults();
		for(String key : config.getAll().keySet())
		{
			data.set(key,config.get(key,data.get(key)));
		}
		data.set("BlackListWorld",data.getStringList("BlackListWorld").stream()
				.map(String::toLowerCase)
				.collect(Collectors.toList()));
		data.set("WhiteListWorld",data.getStringList("WhiteListWorld").stream()
				.map(String::toLowerCase)
				.collect(Collectors.toList()));
		config.setAll(data);
		config.save();
	}
	
	public static void save()
	{
		config.save();
	}
	
	public static void set(String key,Object value) throws ConfigException
	{
		if(!validateIndex(key))
		{
			throw new ConfigException("Invalid config index.");
		}
		config.set(key,value);
		config.save();
	}
	
	public static int getInt(String key)
	{
		return config.getInt(key,defaults.getInt(key));
	}
	
	public static double getDouble(String key)
	{
		return config.getDouble(key,defaults.getDouble(key));
	}
	
	public static String getString(String key)
	{
		return config.getString(key,defaults.getString(key));
	}
	
	public static boolean getBoolean(String key)
	{
		return config.getBoolean(key,defaults.getBoolean(key));
	}
	
	public static List<String> getStringList(String key)
	{
		return config.getStringList(key);
	}
}
