package moe.berd.nukkit.FResidence.utils;

import java.util.*;

import cn.nukkit.utils.*;

public class Messages
{
	public static final String INDEX_ENTER="enter",
			INDEX_LEAVE="leave",
			INDEX_PERMISSION="permission";
	
	private static Map<String,String> defaults=new HashMap<String,String>()
	{
		{
			put(INDEX_ENTER,Utils.translate("messages.enter"));
			put(INDEX_LEAVE,Utils.translate("messages.leave"));
			put(INDEX_PERMISSION,Utils.translate("messages.permission"));
		}
	};
	
	public static Map<String,String> getDefaults()
	{
		return new HashMap<>(defaults);
	}
	
	public static boolean validateIndex(String index)
	{
		return defaults.containsKey(index.toLowerCase());
	}
	
	public static String validateIndexThrow(String index)
	{
		if(!validateIndex(index))
		{
			throw new IllegalArgumentException(Utils.translate("exception.invalid_message_index"));
		}
		return index.toLowerCase();
	}
	
	private Residence residence;
	
	private Map<String,String> messages=getDefaults();
	
	public Messages()
	{
	
	}
	
	public Messages(Residence res)
	{
		residence=res;
	}
	
	public Messages(Residence res,ConfigSection data)
	{
		this(res);
		messages.keySet().forEach(key->messages.put(key,data.getString(key)));
	}
	
	public ConfigSection getRawData()
	{
		return new ConfigSection()
		{
			{
				messages.forEach(this::put);
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
	
	public String getMessage(String index)
	{
		return messages.get(validateIndexThrow(index));
	}
	
	public Messages setMessage(String index,String val)
	{
		messages.put(validateIndexThrow(index),val);
		trySave();
		return this;
	}
	
	public Messages resetMessages()
	{
		messages=getDefaults();
		trySave();
		return this;
	}
}
