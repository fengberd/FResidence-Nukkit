package moe.berd.nukkit.FResidence.utils;

import java.util.*;

import cn.nukkit.plugin.*;

import me.onebone.economyapi.*;
import moe.berd.nukkit.FResidence.*;
import moe.berd.nukkit.FResidence.exception.*;

public class Economy
{
	public enum API
	{
		InternalUse,
		EconomyAPI
	}
	
	private static API CurrentAPI=API.InternalUse;
	
	public static void init(Main main,String preferAPI) throws MissingDependException
	{
		List<API> available=new ArrayList<>();
		PluginManager pm=main.getServer().getPluginManager();
		if(pm.getPlugin("EconomyAPI")!=null)
		{
			available.add(API.EconomyAPI);
		}
		if(available.size()==0)
		{
			throw new MissingDependException("exception.missing_economy");
		}
		API prefer=API.valueOf(preferAPI);
		if(!available.contains(prefer))
		{
			prefer=available.get(0);
		}
		CurrentAPI=prefer;
	}
	
	public static API getApi()
	{
		return CurrentAPI;
	}
	
	public static double getMoney(Object player)
	{
		switch(CurrentAPI)
		{
		case EconomyAPI:
			return EconomyAPI.getInstance().myMoney(Utils.getPlayerName(player));
		}
		return 0;
	}
	
	public static boolean setMoney(Object player,double value)
	{
		switch(CurrentAPI)
		{
		case EconomyAPI:
			return EconomyAPI.getInstance().setMoney(Utils.getPlayerName(player),value)==EconomyAPI.RET_SUCCESS;
		}
		return false;
	}
	
	public static boolean addMoney(Object player,double value)
	{
		switch(CurrentAPI)
		{
		case EconomyAPI:
			return EconomyAPI.getInstance().addMoney(Utils.getPlayerName(player),value)==EconomyAPI.RET_SUCCESS;
		default:
			return setMoney(player,getMoney(player)+value);
		}
	}
	
	public static boolean reduceMoney(Object player,double value)
	{
		switch(CurrentAPI)
		{
		case EconomyAPI:
			return EconomyAPI.getInstance().reduceMoney(Utils.getPlayerName(player),value)==EconomyAPI.RET_SUCCESS;
		default:
			double money=getMoney(player);
			return money>=value && setMoney(player,money-value);
		}
	}
}
