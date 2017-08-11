package moe.berd.nukkit.FResidence;

import cn.nukkit.lang.*;
import cn.nukkit.command.*;

import moe.berd.nukkit.FResidence.exception.*;

public class Main extends cn.nukkit.plugin.PluginBase
{
	private BaseLang baseLang;
	
	@Override
	public void onEnable()
	{
		this.baseLang = new BaseLang(this.getServer().getLanguage().getLang());
	}
	
	public BaseLang getLanguage()
	{
		return baseLang;
	}
	
	public void onResidenceCommand(CommandSender sender,String[] args) throws FResidenceException
	{
	
	}
	
	public void onResidenceAdminCommand(CommandSender sender,String[] args) throws FResidenceException
	{
	
	}
}
