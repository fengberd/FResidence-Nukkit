package moe.berd.nukkit.FResidence.command;

import cn.nukkit.command.*;

import moe.berd.nukkit.FResidence.*;
import moe.berd.nukkit.FResidence.utils.*;
import moe.berd.nukkit.FResidence.exception.*;

public class ResidenceAdminCommand extends Command
{
	private Main main;
	
	public ResidenceAdminCommand(Main main)
	{
		super("residenceadmin","领地插件指令 - 使用 /resadmin help 查看帮助","使用 /resadmin help 查看帮助",new String[]{"resadmin"});
		this.main=main;
		this.setPermission("FResidence.admin");
	}
	
	@Override
	public boolean execute(CommandSender sender,String label,String[] args)
	{
		if(this.testPermission(sender))
		{
			try
			{
				if(args.length==0)
				{
					args=new String[]{"help"};
				}
				args[0]=args[0].toLowerCase();
				this.main.onResidenceAdminCommand(sender,args);
			}
			catch(FResidenceException e)
			{
				sender.sendMessage(Utils.getRedString("无法完成操作: "+e.getMessage()));
			}
		}
		return true;
	}
}
